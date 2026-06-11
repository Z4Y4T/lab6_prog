package client.commands;

import java.io.IOException;

import common.exceptions.InvalidCommandArgumentException;
import common.net.CommandType;
import common.net.Request;
import common.net.Response;
import common.parsers.CommandArgumentParser;
import client.main.Client;

/**
 * Клиентская команда {@code print_unique_start_date}.
 * Выводит все уникальные значения дат начала работы среди элементов коллекции.
 * 
 * <p>
 * Не принимает аргументов. Поиск уникальных дат происходит на серверной
 * стороне.
 * </p>
 */
public class PrintUniqueStartDateClientCommand implements ClientCommand {
    private final Client client;

    public PrintUniqueStartDateClientCommand(Client client) {
        this.client = client;
    }

    @Override
    public String getName() {
        return "print_unique_start_date";
    }

    @Override
    public String getDescription() {
        return "displays all unique start dates in the collection";
    }

    /**
     * Выполняет команду: проверяет отсутствие аргументов, отправляет запрос на
     * сервер.
     * 
     * @param args должен быть пустым
     * @return Response со списком уникальных дат, либо ошибкой
     */
    @Override
    public Response execute(String... args) {
        try {
            CommandArgumentParser.checkArgumentCount(args, 0);
            return client.send(new Request(CommandType.PRINT_UNIQUE_START_DATE, null, null));
        } catch (InvalidCommandArgumentException e) {
            return new Response(false, "Invalid command arguments: " + e.getMessage(), null);
        } catch (IOException e) {
            return new Response(false, "Failed to send request: " + e.getMessage(), null, true);
        }
    }
}