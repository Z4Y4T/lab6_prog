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
 * Клиентская команда {@code remove_by_id}.
 * Удаляет элемент коллекции по заданному идентификатору.
 * 
 * <p>
 * Принимает один аргумент — целое положительное число (id элемента).
 * Если аргумент отсутствует, не является числом или отрицательный —
 * возвращает ошибку и не отправляет запрос на сервер.
 * </p>
 * 
 * <p>
 * При успешной валидации формирует {@link ArgumentWrapper} с id
 * и отправляет запрос через {@link Client#send(Request)}.
 * </p>
 */
public class RemoveByIdClientCommand implements ClientCommand {
    private final Client client;

    public RemoveByIdClientCommand(Client client) {
        this.client = client;
    }

    @Override
    public String getName() {
        return "remove_by_id";
    }

    @Override
    public String getDescription() {
        return "removes a collection item by its id";
    }

    /**
     * Выполняет команду: проверяет количество аргументов, парсит id,
     * формирует запрос и отправляет на сервер.
     * 
     * @param args массив из одного элемента — строковое представление id
     * @return Response с результатом удаления, либо с сообщением об ошибке
     *         (неверный аргумент, ошибка соединения)
     */
    @Override
    public Response execute(String... args) {
        try {
            CommandArgumentParser.checkArgumentCount(args, 1);
            Integer id = CommandArgumentParser.parseId(args);
            return client.send(new Request(CommandType.REMOVE_BY_ID, new ArgumentWrapper(id), null));
        } catch (InvalidCommandArgumentException e) {
            return new Response(false, "Invalid command arguments: " + e.getMessage(), null);
        } catch (IOException e) {
            return new Response(false, "Failed to send request: " + e.getMessage(), null, true);
        }
    }
}