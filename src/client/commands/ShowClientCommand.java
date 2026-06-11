package client.commands;

import java.io.IOException;

import common.exceptions.InvalidCommandArgumentException;
import common.net.ArgumentWrapper;
import common.net.CommandType;
import common.net.Request;
import common.net.Response;
import common.parsers.CommandArgumentParser;
import client.main.Client;

/**
 * Клиентская команда {@code show}.
 * Выводит страницу коллекции, отсортированную по имени.
 * 
 * <p>
 * Принимает один аргумент — номер страницы (целое число ≥ 0).
 * Сервер возвращает до 100 элементов указанной страницы. При большом
 * размере ответ разбивается на чанки.
 * </p>
 */
public class ShowClientCommand implements ClientCommand {
    private final Client client;

    public ShowClientCommand(Client client) {
        this.client = client;
    }

    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getDescription() {
        return "displays items of the given page";
    }

    /**
     * Выполняет команду: парсит номер страницы и отправляет запрос на сервер.
     * 
     * @param args массив из одного элемента — номер страницы
     * @return Response с массивом Worker'ов указанной страницы, либо ошибкой
     */
    @Override
    public Response execute(String... args) {
        try {
            CommandArgumentParser.checkArgumentCount(args, 1);
            Integer page = CommandArgumentParser.parseIndex(args) - 1;
            return client.send(new Request(CommandType.SHOW, new ArgumentWrapper(page), null));
        } catch (InvalidCommandArgumentException e) {
            return new Response(false, "Invalid command arguments: " + e.getMessage(), null);
        } catch (IOException e) {
            return new Response(false, "Failed to send request: " + e.getMessage(), null, true);
        }
    }
}