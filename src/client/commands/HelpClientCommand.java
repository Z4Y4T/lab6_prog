package client.commands;

import java.util.Map;

import common.exceptions.InvalidCommandArgumentException;
import common.net.Response;
import common.parsers.CommandArgumentParser;

/**
 * Клиентская команда {@code help}.
 * Выводит список всех доступных команд с их кратким описанием.
 * 
 * <p>
 * Не принимает аргументов. Работает полностью на клиентской стороне:
 * обходит все зарегистрированные в {@link client.managers.ClientCommandManager}
 * команды и формирует строку с их названиями и описаниями.
 * </p>
 * 
 * <p>
 * Запрос на сервер не отправляется.
 * </p>
 */
public class HelpClientCommand implements ClientCommand {
    private Map<String, ClientCommand> commandMap;

    public HelpClientCommand(Map<String, ClientCommand> commandMap) {
        this.commandMap = commandMap;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "displays help information for available commands";
    }

    /**
     * Выполняет команду: проверяет отсутствие аргументов и формирует
     * справочное сообщение со списком всех команд и их описаний.
     * 
     * @param args должен быть пустым
     * @return Response с текстом справки, либо с ошибкой валидации аргументов
     */
    @Override
    public Response execute(String... args) {
        try {
            CommandArgumentParser.checkArgumentCount(args, 0);
        } catch (InvalidCommandArgumentException e) {
            return new Response(false, "Invalid command arguments: " + e.getMessage(), null);
        }
        StringBuilder helpMessage = new StringBuilder("Available commands:\n");
        for (ClientCommand command : commandMap.values()) {
            helpMessage.append(command.getName()).append(" - ").append(command.getDescription()).append("\n");
        }
        return new Response(true, helpMessage.toString(), null);
    }
}