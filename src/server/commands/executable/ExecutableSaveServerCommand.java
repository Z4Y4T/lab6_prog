package server.commands.executable;

import java.util.logging.Logger;

import common.exceptions.InvalidCommandArgumentException;
import common.parsers.CommandArgumentParser;
import server.main.ServerApplication;

/**
 * Консольная команда {@code save}.
 * Сохраняет текущее состояние коллекции в файл.
 * 
 * <p>
 * Не принимает аргументов. Вызывает
 * {@link ServerApplication#saveWorkersToFile()}.
 * Аналог клиентской команды — выполняется без отправки запроса по сети.
 * </p>
 */
public class ExecutableSaveServerCommand implements ExecutableServerCommand {
    private final ServerApplication application;
    private static final Logger logger = Logger.getLogger(ExecutableSaveServerCommand.class.getName());

    /**
     * Создаёт команду сохранения.
     *
     * @param application серверное приложение с доступом к коллекции
     */
    public ExecutableSaveServerCommand(ServerApplication application) {
        this.application = application;
    }

    @Override
    public String getName() {
        return "save";
    }

    @Override
    public String getDescription() {
        return "saves the collection to file";
    }

    /**
     * Выполняет команду: проверяет отсутствие аргументов и сохраняет коллекцию.
     *
     * @param args должен быть пустым
     * @throws InvalidCommandArgumentException если переданы аргументы
     */
    @Override
    public void execute(String... args) throws InvalidCommandArgumentException {
        CommandArgumentParser.checkArgumentCount(args, 0);
        application.saveWorkersToFile();
        logger.info("Collection saved successfully.");
    }
}