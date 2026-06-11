package server.main;

import java.util.Optional;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import common.net.CommandType;
import common.domain.Worker;
import common.exceptions.FileAccessException;
import common.net.Request;
import common.net.Response;
import server.commands.non_executable.*;
import server.managers.CollectionManager;
import server.managers.ServerCommandManager;
import server.utilities.FromXMLInterpreter;
import server.utilities.ToXMLInterpreter;

/**
 * Центральный класс бизнес-логики сервера.
 * 
 * <p>
 * Связывает воедино все компоненты серверной обработки:
 * </p>
 * <ul>
 * <li>{@link CollectionManager} — управление коллекцией Worker'ов</li>
 * <li>{@link ServerCommandManager} — регистрация и поиск команд по типу</li>
 * <li>Загрузка коллекции из XML-файла при старте</li>
 * <li>Сохранение коллекции в XML-файл (по команде или shutdown hook'у)</li>
 * </ul>
 * 
 * <p>
 * Не занимается сетью или потоками — только бизнес-логика.
 * Сетевой слой ({@link Server}) вызывает {@link #handleRequest(Request)}
 * для каждого входящего запроса.
 * </p>
 * 
 * <p>
 * Имя файла для сохранения/загрузки берётся из переменной окружения
 * {@code WORKER_FILE}. Если она не задана — используется {@code workers.xml}
 * в текущей директории.
 * </p>
 */
public class ServerApplication {
    private static final Logger logger = Logger.getLogger(ServerApplication.class.getName());

    /** Менеджер коллекции — хранение, добавление, удаление, обновление Worker'ов */
    private final CollectionManager collectionManager;

    /** Менеджер серверных команд — связывает CommandType с реализацией */
    private final ServerCommandManager commandManager;

    /**
     * Создаёт серверное приложение: инициализирует менеджеры и регистрирует
     * команды.
     */
    public ServerApplication() {
        this.collectionManager = new CollectionManager();
        this.commandManager = new ServerCommandManager();
        registerServerCommands();
    }

    /**
     * Загружает коллекцию из XML-файла.
     * Имя файла берётся из переменной окружения {@code WORKER_FILE}.
     * Если файл не найден или повреждён — сервер продолжает работу с пустой
     * коллекцией.
     */
    public void loadWorkersFromEnv() {
        String filename = getStorageFile();
        try {
            Vector<Worker> workers = FromXMLInterpreter.loadFromFile(filename);
            collectionManager.setWorkers(workers);
            logger.info("Loaded " + workers.size() + " elements from " + filename);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to load workers from " + filename, e);
        }
    }

    /**
     * Сохраняет текущее состояние коллекции в XML-файл.
     * Вызывается при выполнении команды {@code save} и в shutdown hook'е.
     */
    public void saveWorkersToFile() {
        String filename = getStorageFile();
        try {
            ToXMLInterpreter.saveToFile(filename, collectionManager.getWorkerList());
            logger.info("Saved collection to " + filename);
        } catch (FileAccessException e) {
            logger.log(Level.WARNING, "Unable to save collection to " + filename, e);
        }
    }

    /**
     * Возвращает имя файла для хранения коллекции.
     * Берётся из переменной окружения {@code WORKER_FILE}.
     * Если переменная не задана или пуста — используется {@code workers.xml}.
     *
     * @return имя файла
     */
    private String getStorageFile() {
        return Optional.ofNullable(System.getenv("WORKER_FILE"))
                .filter(s -> !s.trim().isEmpty())
                .orElse("workers.xml");
    }

    /**
     * Обрабатывает запрос от клиента.
     * 
     * <p>
     * Находит команду по типу из {@code request.getCommand()}
     * и вызывает {@link ServerCommand#execute(Request, CollectionManager)}.
     * Если команда не найдена или произошла ошибка — возвращает Response с ошибкой.
     * </p>
     *
     * @param request запрос от клиента
     * @return ответ сервера
     */
    public Response handleRequest(Request request) {
        if (request == null || request.getCommand() == null) {
            logger.warning("Invalid request received");
            return new Response(false, "Invalid request", null);
        }

        CommandType cmdType = request.getCommand();
        ServerCommand cmd = commandManager.getCommand(cmdType);
        if (cmd == null) {
            logger.warning("Unknown command: " + request.getCommand());
            return new Response(false, "Unknown command: " + request.getCommand(), null);
        }

        try {
            logger.fine("Executing command: " + cmdType + " (requestId=" + request.getRequestId() + ")");
            Response response = cmd.execute(request, collectionManager);
            logger.fine("Command " + cmdType + " executed successfully (requestId=" + request.getRequestId() + ")");
            return response;
        } catch (Exception e) {
            logger.log(Level.WARNING,
                    "Error executing command: " + cmdType + " (requestId=" + request.getRequestId() + ")", e);
            return new Response(false, "Server error: " + e.getMessage(), null);
        }
    }

    /**
     * Регистрирует все серверные команды в {@link ServerCommandManager}.
     * Каждая команда связывается со своим типом из {@link CommandType}.
     */
    private void registerServerCommands() {
        commandManager.register(CommandType.INFO, new InfoServerCommand());
        commandManager.register(CommandType.SHOW, new ShowServerCommand());
        commandManager.register(CommandType.ADD, new AddServerCommand());
        commandManager.register(CommandType.UPDATE, new UpdateServerCommand());
        commandManager.register(CommandType.REMOVE_BY_ID, new RemoveByIdServerCommand());
        commandManager.register(CommandType.CLEAR, new ClearServerCommand());
        commandManager.register(CommandType.INSERT_AT, new InsertAtServerCommand());
        commandManager.register(CommandType.ADD_IF_MAX, new AddIfMaxServerCommand());
        commandManager.register(CommandType.REMOVE_GREATER, new RemoveGreaterServerCommand());
        commandManager.register(CommandType.MAX_BY_SALARY, new MaxBySalaryServerCommand());
        commandManager.register(CommandType.COUNT_GREATER_THAN_START_DATE,
                new CountGreaterThanStartDateServerCommand());
        commandManager.register(CommandType.PRINT_UNIQUE_START_DATE, new PrintUniqueStartDateServerCommand());
    }
}