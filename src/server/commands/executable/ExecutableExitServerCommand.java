package server.commands.executable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import common.exceptions.InvalidCommandArgumentException;
import common.parsers.CommandArgumentParser;
import server.main.ServerApplication;

/**
 * Консольная команда {@code exit}.
 * Сохраняет коллекцию и завершает работу сервера.
 * 
 * <p>
 * Не принимает аргументов. Вызывает
 * {@link ServerApplication#saveWorkersToFile()},
 * затем устанавливает флаг {@code running} в {@code false}, что приводит
 * к выходу из главного цикла и остановке сервера.
 * </p>
 */
public class ExecutableExitServerCommand implements ExecutableServerCommand {
    private final ServerApplication application;
    private final AtomicBoolean running;
    private static final Logger logger = Logger.getLogger(ExecutableExitServerCommand.class.getName());

    /**
     * Создаёт команду выхода.
     *
     * @param application серверное приложение
     * @param running     флаг работы сервера (общий с главным циклом)
     */
    public ExecutableExitServerCommand(ServerApplication application, AtomicBoolean running) {
        this.application = application;
        this.running = running;
    }

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public String getDescription() {
        return "saves the collection and shuts down the server";
    }

    /**
     * Выполняет команду: проверяет отсутствие аргументов, сохраняет коллекцию
     * и останавливает сервер.
     *
     * @param args должен быть пустым
     * @throws InvalidCommandArgumentException если переданы аргументы
     */
    @Override
    public void execute(String... args) throws InvalidCommandArgumentException {
        CommandArgumentParser.checkArgumentCount(args, 0);
        logger.info("Saving collection before exit...");
        application.saveWorkersToFile();
        running.set(false);
        logger.info("Server shutting down...");
    }
}