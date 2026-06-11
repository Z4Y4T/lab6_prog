package client.commands;

import java.io.IOException;

import common.exceptions.InvalidCommandArgumentException;
import common.net.CommandType;
import common.net.Request;
import common.net.Response;
import common.parsers.CommandArgumentParser;
import client.main.Client;

/**
 * Клиентская команда {@code info}.
 * Выводит информацию о коллекции: тип, дату инициализации, количество
 * элементов.
 * 
 * <p>
 * Не принимает аргументов. Отправляет на сервер пустой запрос —
 * вся логика получения информации находится на серверной стороне.
 * </p>
 */
public class InfoClientCommand implements ClientCommand {
    private final Client client;

    public InfoClientCommand(Client client) {
        this.client = client;
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "displays information about the collection";
    }

    /**
     * Выполняет команду: проверяет отсутствие аргументов, отправляет запрос на
     * сервер.
     * 
     * @param args должен быть пустым
     * @return Response с информацией о коллекции, либо ошибкой
     */
    @Override
    public Response execute(String... args) {
        try {
            CommandArgumentParser.checkArgumentCount(args, 0);
            return client.send(new Request(CommandType.INFO, null, null));
        } catch (InvalidCommandArgumentException e) {
            return new Response(false, "Invalid command arguments: " + e.getMessage(), null);
        } catch (IOException e) {
            return new Response(false, "Failed to send request: " + e.getMessage(), null, true);
        }
    }
}