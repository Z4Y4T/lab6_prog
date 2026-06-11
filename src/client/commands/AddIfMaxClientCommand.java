package client.commands;

import java.io.IOException;

import common.domain.Worker;
import common.exceptions.InvalidCommandArgumentException;
import common.exceptions.InvalidWorkerArgumentException;
import common.net.CommandType;
import common.net.Request;
import common.net.Response;
import common.parsers.CommandArgumentParser;
import client.main.Client;
import client.managers.ScriptManager;
import client.utilities.WorkerFactory;

/**
 * Клиентская команда {@code add_if_max}.
 * Добавляет новый элемент в коллекцию, только если его значение
 * превышает значение наибольшего элемента в коллекции.
 * 
 * <p>
 * Не принимает аргументов командной строки. Запрашивает у пользователя
 * все поля Worker'а через {@link WorkerFactory}. Сравнение происходит
 * на серверной стороне: если новый элемент меньше или равен максимальному,
 * сервер возвращает отказ.
 * </p>
 * 
 * <p>
 * Сравнение выполняется методом {@link Worker#compareTo(Worker)} —
 * сначала по имени, затем по id.
 * </p>
 */
public class AddIfMaxClientCommand implements ClientCommand {
    private final WorkerFactory factory;
    private final ScriptManager scriptManager;
    private final Client client;

    public AddIfMaxClientCommand(WorkerFactory factory, ScriptManager scriptManager, Client client) {
        this.factory = factory;
        this.scriptManager = scriptManager;
        this.client = client;
    }

    @Override
    public String getName() {
        return "add_if_max";
    }

    @Override
    public String getDescription() {
        return "adds a new item to the collection if its value exceeds the value of the largest item in this collection";
    }

    /**
     * Выполняет команду: проверяет отсутствие аргументов, создаёт Worker
     * через опрос пользователя и отправляет запрос на сервер.
     * Сервер решает, добавлять ли элемент.
     * 
     * @param args должен быть пустым
     * @return Response с результатом: добавлен, либо отказано (не наибольший),
     *         либо ошибка
     */
    @Override
    public Response execute(String... args) {
        try {
            CommandArgumentParser.checkArgumentCount(args, 0);
            Worker worker = factory.createWorker(scriptManager.getCurrentReader());
            return client.send(new Request(CommandType.ADD_IF_MAX, null, worker));
        } catch (InvalidCommandArgumentException e) {
            return new Response(false, "Invalid command arguments: " + e.getMessage(), null);
        } catch (InvalidWorkerArgumentException e) {
            return new Response(false, "Failed to create worker: " + e.getMessage(), null);
        } catch (IOException e) {
            return new Response(false, "Failed to send request: " + e.getMessage(), null, true);
        }
    }
}