package server.main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import common.net.Chunk;
import common.net.ChunkManager;
import common.net.Request;
import common.net.Response;
import common.net.Serializer;

/**
 * UDP-сервер с неблокирующим вводом-выводом (Java NIO).
 * 
 * <p>
 * Основные обязанности:
 * </p>
 * <ul>
 * <li>Прослушивание порта и приём входящих UDP-пакетов через
 * {@link DatagramChannel}</li>
 * <li>Сборка чанков в полные объекты {@link Request}</li>
 * <li>Передача собранного запроса в
 * {@link ServerApplication#handleRequest(Request)}</li>
 * <li>Сериализация {@link Response}, разбиение на чанки через
 * {@link ChunkManager}
 * и отправка клиенту</li>
 * <li>Обработка переполнения буфера сокета — неотправленные чанки попадают в
 * очередь
 * и допосылаются при следующем событии {@code OP_WRITE}</li>
 * <li>Сохранение коллекции при завершении JVM через shutdown hook</li>
 * </ul>
 * 
 * <p>
 * Сетевой слой построен на неблокирующем API {@code java.nio.channels}:
 * </p>
 * <ul>
 * <li>{@link DatagramChannel} — двусторонний канал для UDP-пакетов</li>
 * <li>{@link Selector} — мультиплексор событий, позволяет одному потоку
 * обслуживать
 * чтение и запись на одном канале без блокировки</li>
 * <li>{@link SelectionKey} — ключ, связывающий канал с набором интересующих
 * операций
 * ({@code OP_READ}, {@code OP_WRITE})</li>
 * </ul>
 * 
 * <p>
 * Для сборки и разбиения данных используется {@link ChunkManager} из общего
 * модуля.
 * Это позволяет и клиенту, и серверу работать с чанками единообразно.
 * </p>
 */
public class Server {

    /** Максимальный размер одного UDP-пакета в байтах (64 КБ минус заголовки) */
    private static final int BUFFER_SIZE = 65507;

    /** Размер одного чанка в байтах (меньше BUFFER_SIZE с запасом на заголовки) */
    private static final int CHUNK_SIZE = 9000;

    private static final Logger logger = Logger.getLogger(Server.class.getName());

    /** Бизнес-логика: управление коллекцией, обработка команд */
    private final ServerApplication application;

    /** Неблокирующий UDP-канал для приёма и отправки пакетов */
    private final DatagramChannel channel;

    /** Мультиплексор событий — ожидает готовности канала к чтению или записи */
    private final Selector selector;

    /**
     * Ключ, связывающий канал с селектором и хранящий набор интересующих операций
     */
    private final SelectionKey selectionKey;

    /**
     * Потокобезопасная очередь ответов, ожидающих отправки (буфер сокета был
     * переполнен)
     */
    private final Queue<PendingResponse> responseQueue;

    /**
     * Мапа для сборки входящих запросов из чанков.
     * Ключ — requestId, значение — накопитель {@link RequestAssembly}.
     * Потокобезопасная ({@link ConcurrentHashMap}) для будущей многопоточной
     * обработки.
     */
    private final Map<Long, RequestAssembly> requestAssemblyMap;

    /**
     * Флаг работы сервера.
     * Пока {@code true} — главный цикл продолжается.
     * Установка в {@code false} (через shutdown hook) останавливает сервер.
     */
    private final AtomicBoolean running = new AtomicBoolean(true);

    /**
     * Создаёт сервер, выполняет начальную инициализацию и привязку к порту.
     * 
     * <p>
     * Порядок инициализации:
     * </p>
     * <ol>
     * <li>Создаётся {@link ServerApplication} — загружается коллекция из файла</li>
     * <li>Открывается {@link DatagramChannel} и переводится в неблокирующий
     * режим</li>
     * <li>Канал привязывается к указанному порту методом {@code bind()}</li>
     * <li>Создаётся {@link Selector}, и канал регистрируется на нём с флагом
     * {@code OP_READ}</li>
     * <li>Инициализируются потокобезопасные коллекции для очереди ответов и сборки
     * запросов</li>
     * <li>Регистрируется shutdown hook для аварийного сохранения коллекции</li>
     * </ol>
     *
     * @param port порт, на котором сервер будет принимать подключения
     * @throws IOException если не удалось открыть канал или привязаться к порту
     */
    public Server(int port, String host) throws IOException {
        this.application = new ServerApplication();
        this.application.loadWorkersFromEnv();
        this.channel = DatagramChannel.open();
        this.channel.configureBlocking(false);
        this.channel.bind(new InetSocketAddress(host, port));
        this.selector = Selector.open();
        this.selectionKey = channel.register(selector, SelectionKey.OP_READ);
        this.responseQueue = new ConcurrentLinkedQueue<>();
        this.requestAssemblyMap = new ConcurrentHashMap<>();
        registerShutdownHook();
        ServerConsole serverConsole = new ServerConsole(application, running);
        Thread consoleThread = new Thread(serverConsole, "ServerConsole");
        consoleThread.setDaemon(true);
        consoleThread.start();
        logger.info("Server started on port " + port);
    }

    /**
     * Главный цикл сервера.
     * 
     * <p>
     * Выполняется, пока флаг {@code running} установлен в {@code true}:
     * </p>
     * <ol>
     * <li>Вызывает {@link Selector#select(long)} с таймаутом 500 мс — ожидает
     * события
     * на зарегистрированном канале</li>
     * <li>Получает набор сработавших ключей через
     * {@link Selector#selectedKeys()}</li>
     * <li>Перебирает ключи итератором (с обязательным удалением через
     * {@code iterator.remove()})</li>
     * <li>Если ключ в состоянии {@code OP_READ} — вызывает
     * {@link #readRequest()}</li>
     * <li>Если ключ в состоянии {@code OP_WRITE} — вызывает
     * {@link #flushPendingResponses()}</li>
     * </ol>
     * 
     * <p>
     * После выхода из цикла (running = false) вызывается {@link #shutdown()}
     * для освобождения ресурсов.
     * </p>
     */
    public void run() {
        logger.info("Server main loop started");
        while (running.get()) {
            try {
                selector.select(500);
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isReadable()) {
                        readRequest();
                    }
                    if (key.isWritable()) {
                        flushPendingResponses();
                    }
                }
            } catch (Exception e) {
                if (!running.get()) {
                    break;
                }
                logger.log(Level.SEVERE, "Server error: " + e.getMessage(), e);
            }
        }
        shutdown();
    }

    /**
     * Читает входящие данные из канала.
     * 
     * <p>
     * Выделяет {@link ByteBuffer}, читает данные из канала через
     * {@link DatagramChannel#receive(ByteBuffer)}, получает адрес отправителя.
     * Десериализует байты.
     * </p>
     * 
     * <p>
     * Два сценария после десериализации:
     * </p>
     * <ul>
     * <li><b>Получен {@link Request}</b> — запрос влез в один пакет.
     * Сразу передаётся в {@link ServerApplication#handleRequest(Request)},
     * ответ отправляется клиенту</li>
     * <li><b>Получен {@link Chunk}</b> — запрос был разбит клиентом на части.
     * Чанк помещается в {@link RequestAssembly} по ключу requestId.
     * Когда все чанки собраны — вызывается {@link ChunkManager#assemble(Map, int)},
     * собирается полный Request, обрабатывается, ответ отправляется</li>
     * </ul>
     */
    private void readRequest() {
        ByteBuffer requestBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        try {
            SocketAddress clientAddress = channel.receive(requestBuffer);
            if (clientAddress == null) {
                return;
            }
            logger.fine("Received packet from client: " + clientAddress);

            requestBuffer.flip();
            byte[] requestBytes = new byte[requestBuffer.remaining()];
            requestBuffer.get(requestBytes);

            Object object = Serializer.deserialize(requestBytes);

            if (object instanceof Request) {
                Request request = (Request) object;
                logger.fine("Received request from " + clientAddress
                        + ": command=" + request.getCommand() + ", requestId=" + request.getRequestId());
                Response response = application.handleRequest(request);
                sendResponse(response, clientAddress, request.getRequestId());
                return;
            }

            if (object instanceof Chunk) {
                Chunk chunk = (Chunk) object;
                long requestId = chunk.getRequestId();
                logger.fine("Received chunk from " + clientAddress
                        + ": requestId=" + requestId
                        + ", chunk=" + chunk.getChunkIndex() + "/" + chunk.getTotalChunks());

                RequestAssembly assembly = requestAssemblyMap.computeIfAbsent(
                        requestId, k -> new RequestAssembly(chunk.getTotalChunks()));

                assembly.addChunk(chunk.getChunkIndex(), chunk.getPayload());

                if (assembly.isComplete()) {
                    requestAssemblyMap.remove(requestId);
                    byte[] fullRequest = ChunkManager.assemble(assembly.getChunkMap(), assembly.getTotalChunks());
                    Request request = (Request) Serializer.deserialize(fullRequest);
                    logger.fine("Assembled request from " + clientAddress
                            + ": command=" + request.getCommand() + ", requestId=" + requestId);
                    Response response = application.handleRequest(request);
                    sendResponse(response, clientAddress, requestId);
                }
            }

        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to read or handle incoming request", e);
        }
    }

    /**
     * Отправляет ответ клиенту.
     * 
     * <p>
     * Алгоритм отправки:
     * </p>
     * <ol>
     * <li>Сериализует {@link Response} в массив байтов через
     * {@link Serializer#serialize(Object)}</li>
     * <li>Разбивает массив на чанки через
     * {@link ChunkManager#chunkify(byte[], long, int)}</li>
     * <li>Каждый чанк сериализуется отдельно и отправляется через
     * {@link DatagramChannel#send(ByteBuffer, SocketAddress)}</li>
     * <li>Если {@code send()} возвращает 0 — буфер сокета переполнен.
     * Текущий и все оставшиеся чанки помещаются в очередь,
     * включается флаг {@code OP_WRITE} для последующей доотправки</li>
     * </ol>
     *
     * @param response      ответ сервера для отправки клиенту
     * @param clientAddress адрес клиента-получателя
     * @param requestId     идентификатор запроса, на который даётся ответ
     */
    private void sendResponse(Response response, SocketAddress clientAddress, long requestId) {
        try {
            byte[] responseBytes = Serializer.serialize(response);
            Chunk[] chunks = ChunkManager.chunkify(responseBytes, requestId, CHUNK_SIZE);

            logger.fine("Sending response to " + clientAddress
                    + ": requestId=" + requestId
                    + ", totalChunks=" + chunks.length
                    + ", responseSize=" + responseBytes.length);

            for (int i = 0; i < chunks.length; i++) {
                byte[] chunkBytes = Serializer.serialize(chunks[i]);
                ByteBuffer responseBuffer = ByteBuffer.wrap(chunkBytes);
                int sent = channel.send(responseBuffer, clientAddress);

                if (sent == 0) {
                    logger.fine("Socket buffer full, queuing chunks. requestId=" + requestId + ", chunk=" + i);
                    responseQueue.add(new PendingResponse(responseBuffer, clientAddress));

                    for (int j = i + 1; j < chunks.length; j++) {
                        byte[] remainingBytes = Serializer.serialize(chunks[j]);
                        responseQueue.add(new PendingResponse(ByteBuffer.wrap(remainingBytes), clientAddress));
                    }

                    updateInterestOps();
                    break;
                }
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to send response to " + clientAddress + ", requestId=" + requestId, e);
        }
    }

    /**
     * Отправляет накопившиеся ответы из очереди.
     * 
     * <p>
     * Вызывается при событии {@code OP_WRITE} — когда канал снова готов к отправке.
     * Достаёт ответы из очереди в порядке FIFO через {@code peek()} (без удаления)
     * и пытается отправить. При успехе удаляет из очереди через {@code poll()}.
     * Если буфер снова переполнен — прекращает попытки до следующего события.
     * </p>
     */
    private void flushPendingResponses() {
        int flushedCount = 0;
        while (!responseQueue.isEmpty()) {
            PendingResponse response = responseQueue.peek();
            try {
                int sent = channel.send(response.buffer, response.clientAddress);
                if (sent > 0) {
                    responseQueue.poll();
                    flushedCount++;
                } else {
                    break;
                }
            } catch (IOException e) {
                logger.log(Level.WARNING, "Failed to flush pending response", e);
                break;
            }
        }
        if (flushedCount > 0) {
            logger.fine("Flushed " + flushedCount + " pending response chunks");
        }
        updateInterestOps();
    }

    /**
     * Обновляет набор операций, отслеживаемых селектором для канала.
     * 
     * <p>
     * Логика:
     * </p>
     * <ul>
     * <li>Базовая операция — всегда {@code OP_READ} (ждём запросы от клиентов)</li>
     * <li>Если очередь ответов не пуста — добавляется {@code OP_WRITE}
     * (нужно допослать ожидающие чанки)</li>
     * </ul>
     * 
     * <p>
     * Объединение флагов выполняется побитовым ИЛИ ({@code |}).
     * После обновления {@code interestOps} вызывается {@link Selector#wakeup()},
     * чтобы селектор немедленно применил изменения (не ждал таймаут в 500 мс).
     * </p>
     */
    private void updateInterestOps() {
        int ops = SelectionKey.OP_READ;
        if (!responseQueue.isEmpty()) {
            ops |= SelectionKey.OP_WRITE;
        }
        selectionKey.interestOps(ops);
        selector.wakeup();
    }

    /**
     * Регистрирует shutdown hook — поток, выполняемый при завершении JVM.
     * 
     * <p>
     * Срабатывает при нажатии Ctrl+C, вызове {@code System.exit()} или сигнале
     * SIGTERM.
     * Не срабатывает при kill -9 (SIGKILL) и внезапном отключении питания.
     * </p>
     * 
     * <p>
     * При срабатывании устанавливает {@code running = false}, сохраняет коллекцию
     * через {@link ServerApplication#saveWorkersToFile()}, закрывает канал и будит
     * селектор.
     * </p>
     */
    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown signal received, saving collection state...");
            running.set(false);
            application.saveWorkersToFile();
            try {
                if (channel.isOpen()) {
                    channel.close();
                }
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error closing channel in shutdown hook", e);
            }
            selector.wakeup();
            logger.info("Shutdown hook finished");
        }));
    }

    /**
     * Освобождает ресурсы при штатном завершении сервера.
     * Вызывается после выхода из главного цикла {@link #run()}.
     * Закрывает {@link Selector}.
     */
    private void shutdown() {
        logger.info("Server shutting down...");
        try {
            if (selector.isOpen()) {
                selector.close();
            }
            logger.info("Server shutdown complete");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error closing server resources", e);
        }
    }

    /**
     * Контейнер для чанка ответа, ожидающего отправки.
     * Хранит сериализованные данные в {@link ByteBuffer} и адрес
     * клиента-получателя.
     */
    private static class PendingResponse {
        /** Буфер с сериализованными данными чанка */
        private final ByteBuffer buffer;

        /** Адрес клиента, которому нужно отправить этот чанк */
        private final SocketAddress clientAddress;

        /**
         * Создаёт контейнер ожидающего ответа.
         *
         * @param buffer        буфер с данными для отправки
         * @param clientAddress адрес клиента-получателя
         */
        private PendingResponse(ByteBuffer buffer, SocketAddress clientAddress) {
            this.buffer = buffer;
            this.clientAddress = clientAddress;
        }
    }

    /**
     * Накопитель для сборки входящего запроса из чанков.
     * 
     * <p>
     * Хранит мапу полученных чанков (индекс → payload),
     * ожидаемое общее количество и адрес клиента.
     * Когда количество полученных чанков становится равно ожидаемому,
     * сборка считается завершённой.
     * </p>
     */
    private static class RequestAssembly {
        /** Мапа: индекс чанка → payload (массив байтов) */
        private final Map<Integer, byte[]> chunkMap;

        /** Ожидаемое общее количество чанков */
        private final int totalChunks;

        /**
         * Создаёт накопитель для сборки запроса.
         *
         * @param totalChunks   ожидаемое количество чанков
         * @param clientAddress адрес клиента-отправителя
         */
        private RequestAssembly(int totalChunks) {
            this.chunkMap = new HashMap<>();
            this.totalChunks = totalChunks;
        }

        /**
         * Добавляет чанк в мапу.
         *
         * @param index   индекс чанка (от 0 до totalChunks-1)
         * @param payload данные чанка
         */
        private void addChunk(int index, byte[] payload) {
            chunkMap.put(index, payload);
        }

        /**
         * Проверяет, получены ли все ожидаемые чанки.
         *
         * @return {@code true} если сборка завершена
         */
        private boolean isComplete() {
            return chunkMap.size() == totalChunks;
        }

        /**
         * Возвращает мапу собранных чанков.
         *
         * @return мапа индекс → payload
         */
        private Map<Integer, byte[]> getChunkMap() {
            return chunkMap;
        }

        /**
         * Возвращает ожидаемое общее количество чанков.
         *
         * @return totalChunks
         */
        private int getTotalChunks() {
            return totalChunks;
        }
    }
}