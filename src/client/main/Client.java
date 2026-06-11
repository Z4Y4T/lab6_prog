package client.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import common.net.Chunk;
import common.net.ChunkManager;
import common.net.Request;
import common.net.Response;
import common.net.Serializer;

/**
 * Сетевой клиент для взаимодействия с сервером по протоколу UDP.
 * 
 * <p>
 * Отвечает за:
 * </p>
 * <ul>
 * <li>Установку соединения с сервером (метод
 * {@link #connect(InetSocketAddress)})</li>
 * <li>Сериализацию и отправку {@link Request} на сервер</li>
 * <li>Получение и сборку {@link Response} от сервера (в том числе из
 * чанков)</li>
 * <li>Обработку тайм-аутов при ожидании ответа</li>
 * </ul>
 * 
 * <p>
 * Использует {@link DatagramSocket} (блокирующий API из {@code java.net}).
 * После вызова {@code connect()} сокет фильтрует входящие пакеты —
 * принимаются только ответы от указанного сервера.
 * </p>
 * 
 * <p>
 * Каждому запросу присваивается уникальный идентификатор (requestId)
 * через {@link AtomicLong}. Это необходимо для сопоставления запросов
 * и ответов при многопоточной обработке, а также для сборки чанков.
 * </p>
 */
public class Client {
    private final DatagramSocket socket;
    private final SocketAddress serverAddress;

    /** Максимальный размер UDP-пакета (64 КБ минус заголовки) */
    private static final int BUFFER_SIZE = 65507;

    /** Тайм-аут ожидания одного пакета от сервера (миллисекунды) */
    private static final int DEFAULT_TIMEOUT_MS = 12000;

    /** Максимальный размер одного чанка (с запасом меньше BUFFER_SIZE) */
    private static final int CHUNK_SIZE = 9000;

    /** Генератор уникальных идентификаторов запросов */
    private static final AtomicLong REQUEST_ID_GENERATOR = new AtomicLong(1);

    /**
     * Создаёт клиента и подключается к серверу.
     *
     * @param host адрес сервера (например, "localhost")
     * @param port порт сервера (например, 8080)
     * @throws IOException если не удалось создать сокет или подключиться
     */
    public Client(String host, int port) throws IOException {
        serverAddress = new InetSocketAddress(host, port);
        socket = new DatagramSocket();
        socket.connect(serverAddress);
        socket.setSoTimeout(DEFAULT_TIMEOUT_MS);
    }

    /**
     * Отправляет запрос на сервер и возвращает ответ.
     * 
     * <p>
     * Присваивает запросу уникальный requestId, сериализует запрос,
     * при необходимости разбивает на чанки (через
     * {@link ChunkManager#chunkify(byte[], long, int)})
     * и отправляет каждый чанк отдельным UDP-пакетом.
     * Затем ожидает и собирает ответ.
     * </p>
     *
     * @param request запрос без requestId (id будет присвоен автоматически)
     * @return ответ от сервера
     * @throws IOException если произошла ошибка отправки или превышен тайм-аут
     */
    public Response send(Request request) throws IOException {
        long requestId = REQUEST_ID_GENERATOR.getAndIncrement();
        Request requestWithId = request.withRequestId(requestId);
        byte[] data = Serializer.serialize(requestWithId);
        Chunk[] chunks = ChunkManager.chunkify(data, requestId, CHUNK_SIZE);
        for (Chunk chunk : chunks) {
            byte[] chunkBytes = Serializer.serialize(chunk);
            DatagramPacket packet = new DatagramPacket(chunkBytes, chunkBytes.length, serverAddress);
            socket.send(packet);
        }

        return receiveResponse(requestId);
    }

    /**
     * Принимает ответ от сервера.
     * 
     * <p>
     * Если ответ помещается в один UDP-пакет — десериализует и возвращает сразу.
     * Если ответ разбит на чанки — собирает все чанки в {@link HashMap} по
     * индексам,
     * затем склеивает через {@link ChunkManager#assemble(Map, int)} и
     * десериализует.
     * </p>
     * 
     * <p>
     * Чанки от других запросов (с чужим requestId) игнорируются.
     * </p>
     *
     * @param requestId ожидаемый идентификатор запроса
     * @return собранный и десериализованный ответ
     * @throws IOException если превышен тайм-аут или ошибка десериализации
     */
    private Response receiveResponse(long requestId) throws IOException {
        Map<Integer, byte[]> chunkMap = new HashMap<>();
        int totalChunks = -1;

        while (true) {
            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);

            try {
                socket.receive(responsePacket);
            } catch (SocketTimeoutException e) {
                throw new IOException("Timed out waiting for server response", e);
            }

            byte[] responseBytes = new byte[responsePacket.getLength()];
            System.arraycopy(
                    responsePacket.getData(), responsePacket.getOffset(),
                    responseBytes, 0, responseBytes.length);

            Object object = Serializer.deserialize(responseBytes);
            if (object instanceof Response) {
                return (Response) object;
            }
            if (object instanceof Chunk) {
                Chunk chunk = (Chunk) object;
                if (chunk.getRequestId() != requestId) {
                    continue;
                }
                if (totalChunks == -1) {
                    totalChunks = chunk.getTotalChunks();
                }
                chunkMap.put(chunk.getChunkIndex(), chunk.getPayload());
                if (totalChunks > 0 && chunkMap.size() == totalChunks) {
                    byte[] fullResponse = ChunkManager.assemble(chunkMap, totalChunks);
                    return (Response) Serializer.deserialize(fullResponse);
                }
            }
        }
    }

    /**
     * Закрывает сетевое соединение с сервером.
     *
     * @throws IOException если произошла ошибка при закрытии сокета
     */
    public void close() throws IOException {
        socket.close();
    }
}