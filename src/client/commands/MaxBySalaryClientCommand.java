package client.commands;

import java.io.IOException;

import common.exceptions.InvalidCommandArgumentException;
import common.net.CommandType;
import common.net.Request;
import common.net.Response;
import common.parsers.CommandArgumentParser;
import client.main.Client;

/**
 * Клиентская команда {@code max_by_salary}.
 * Выводит элемент коллекции с максимальной зарплатой.
 * 
 * <p>
 * Не принимает аргументов. Сравнение зарплат происходит на серверной стороне.
 * </p>
 */
public class MaxBySalaryClientCommand implements ClientCommand {
    private final Client client;

    public MaxBySalaryClientCommand(Client client) {
        this.client = client;
    }

    @Override
    public String getName() {
        return "max_by_salary";
    }

    @Override
    public String getDescription() {
        return "displays the item with the maximum salary";
    }

    /**
     * Выполняет команду: проверяет отсутствие аргументов, отправляет запрос на
     * сервер.
     * 
     * @param args должен быть пустым
     * @return Response с информацией об элементе с максимальной зарплатой, либо
     *         ошибкой
     */
    @Override
    public Response execute(String... args) {
        try {
            CommandArgumentParser.checkArgumentCount(args, 0);
            return client.send(new Request(CommandType.MAX_BY_SALARY, null, null));
        } catch (InvalidCommandArgumentException e) {
            return new Response(false, "Invalid command arguments: " + e.getMessage(), null);
        } catch (IOException e) {
            return new Response(false, "Failed to send request: " + e.getMessage(), null, true);
        }
    }
}