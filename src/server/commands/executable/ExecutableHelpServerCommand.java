package server.commands.executable;

import java.util.Map;
import java.util.logging.Logger;

import common.exceptions.InvalidCommandArgumentException;
import common.parsers.CommandArgumentParser;

/**
 * Консольная команда {@code help}.
 * Выводит список всех доступных консольных команд сервера с их описаниями.
 * 
 * <p>
 * Не принимает аргументов. Работает полностью на серверной стороне —
 * обходит все зарегистрированные команды и формирует справку.
 * </p>
 */
public class ExecutableHelpServerCommand implements ExecutableServerCommand {
    private final Map<String, ExecutableServerCommand> commandMap;
    private static final Logger logger = Logger.getLogger(ExecutableHelpServerCommand.class.getName());

    /**
     * Создаёт команду помощи.
     *
     * @param commandMap мапа всех зарегистрированных консольных команд
     */
    public ExecutableHelpServerCommand(Map<String, ExecutableServerCommand> commandMap) {
        this.commandMap = commandMap;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "displays help for available server commands";
    }

    /**
     * Выполняет команду: проверяет отсутствие аргументов и выводит справку.
     *
     * @param args должен быть пустым
     * @throws InvalidCommandArgumentException если переданы аргументы
     */
    @Override
    public void execute(String... args) throws InvalidCommandArgumentException {
        CommandArgumentParser.checkArgumentCount(args, 0);
        logger.info("Available server commands:");
        for (ExecutableServerCommand command : commandMap.values()) {
            logger.info(command.getName() + " - " + command.getDescription());
        }
    }
}