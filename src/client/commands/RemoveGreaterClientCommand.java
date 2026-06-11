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
 * Клиентская команда {@code remove_greater}.
 * Удаляет из коллекции все элементы, превышающие заданный.
 * 
 * <p>
 * Не принимает аргументов командной строки. Запрашивает у пользователя
 * все поля Worker'а через {@link WorkerFactory}. Полученный объект используется
 * как эталон для сравнения: все элементы коллекции, которые больше эталона
 * (по {@link Worker#compareTo(Worker)}), будут удалены на серверной стороне.
 * </p>
 */
public class RemoveGreaterClientCommand implements ClientCommand {
    private final WorkerFactory factory;
    private final ScriptManager scriptManager;
    private final Client client;

    public RemoveGreaterClientCommand(WorkerFactory factory, ScriptManager scriptManager, Client client) {
        this.factory = factory;
        this.scriptManager = scriptManager;
        this.client = client;
    }

    @Override
    public String getName() {
        return "remove_greater";
    }

    @Override
    public String getDescription() {
        return "removes all items greater than the specified one";
    }

    /**
     * Выполняет команду: проверяет отсутствие аргументов, создаёт Worker
     * через опрос пользователя и отправляет запрос на сервер.
     * Сервер удаляет все элементы, превышающие переданный.
     * 
     * @param args должен быть пустым
     * @return Response с количеством удалённых элементов, либо с ошибкой
     */
    @Override
    public Response execute(String... args) {
        try {
            CommandArgumentParser.checkArgumentCount(args, 0);
            Worker worker = factory.createWorker(scriptManager.getCurrentReader());
            return client.send(new Request(CommandType.REMOVE_GREATER, null, worker));
        } catch (InvalidCommandArgumentException e) {
            return new Response(false, "Invalid command arguments: " + e.getMessage(), null);
        } catch (InvalidWorkerArgumentException e) {
            return new Response(false, "Failed to create worker: " + e.getMessage(), null);
        } catch (IOException e) {
            return new Response(false, "Failed to send request: " + e.getMessage(), null, true);
        }
    }
}