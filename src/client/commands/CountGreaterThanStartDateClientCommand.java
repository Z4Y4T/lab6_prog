package client.commands;

import java.io.IOException;
import java.time.LocalDateTime;

import common.exceptions.InvalidCommandArgumentException;
import common.net.ArgumentWrapper;
import common.net.CommandType;
import common.net.Request;
import common.net.Response;
import common.parsers.CommandArgumentParser;
import client.main.Client;

/**
 * Клиентская команда {@code count_greater_than_start_date}.
 * Подсчитывает количество элементов коллекции, у которых дата начала
 * больше указанной.
 * 
 * <p>
 * Принимает один аргумент — дату в формате {@code yyyy-MM-dd'T'HH:mm}.
 * Если аргумент отсутствует или не соответствует формату — возвращает ошибку
 * и не отправляет запрос на сервер.
 * </p>
 * 
 * <p>
 * При успешной валидации формирует {@link ArgumentWrapper} с датой
 * и отправляет запрос на сервер. Подсчёт происходит на серверной стороне.
 * </p>
 */
public class CountGreaterThanStartDateClientCommand implements ClientCommand {
    private final Client client;

    public CountGreaterThanStartDateClientCommand(Client client) {
        this.client = client;
    }

    @Override
    public String getName() {
        return "count_greater_than_start_date";
    }

    @Override
    public String getDescription() {
        return "counts the number of items with a start date greater than the specified one";
    }

    /**
     * Выполняет команду: проверяет количество аргументов, парсит дату,
     * формирует запрос и отправляет на сервер.
     * 
     * @param args массив из одного элемента — строковое представление даты
     *             в формате {@code yyyy-MM-dd'T'HH:mm}
     * @return Response с количеством элементов, либо с сообщением об ошибке
     *         (неверный формат даты, ошибка соединения)
     */
    @Override
    public Response execute(String... args) {
        try {
            CommandArgumentParser.checkArgumentCount(args, 1);
            LocalDateTime startDate = CommandArgumentParser.parseStartDate(args);
            return client.send(
                    new Request(CommandType.COUNT_GREATER_THAN_START_DATE, new ArgumentWrapper(startDate), null));
        } catch (InvalidCommandArgumentException e) {
            return new Response(false, "Invalid command arguments: " + e.getMessage(), null);
        } catch (IOException e) {
            return new Response(false, "Failed to send request: " + e.getMessage(), null, true);
        }
    }
}