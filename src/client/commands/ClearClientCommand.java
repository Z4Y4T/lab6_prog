package client.commands;

import java.io.IOException;

import common.exceptions.InvalidCommandArgumentException;
import common.net.CommandType;
import common.net.Request;
import common.net.Response;
import common.parsers.CommandArgumentParser;
import client.main.Client;

/**
 * Клиентская команда {@code clear}.
 * Полностью очищает коллекцию.
 * 
 * <p>
 * Не принимает аргументов. Удаление всех элементов происходит на сервере.
 * </p>
 */
public class ClearClientCommand implements ClientCommand {
    private final Client client;

    public ClearClientCommand(Client client) {
        this.client = client;
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "clears the collection";
    }

    /**
     * Выполняет команду: проверяет отсутствие аргументов, отправляет запрос на
     * сервер.
     * 
     * @param args должен быть пустым
     * @return Response с результатом очистки, либо ошибкой
     */
    @Override
    public Response execute(String... args) {
        try {
            CommandArgumentParser.checkArgumentCount(args, 0);
            return client.send(new Request(CommandType.CLEAR, null, null));
        } catch (InvalidCommandArgumentException e) {
            return new Response(false, "Invalid command arguments: " + e.getMessage(), null);
        } catch (IOException e) {
            return new Response(false, "Failed to send request: " + e.getMessage(), null, true);
        }
    }
}