package client.commands;

import java.io.IOException;

import common.domain.Worker;
import common.exceptions.InvalidCommandArgumentException;
import common.exceptions.InvalidWorkerArgumentException;
import common.net.ArgumentWrapper;
import common.net.CommandType;
import common.net.Request;
import common.net.Response;
import common.parsers.CommandArgumentParser;
import client.main.Client;
import client.managers.ScriptManager;
import client.utilities.WorkerFactory;

/**
 * Клиентская команда {@code update}.
 * Обновляет элемент коллекции по заданному идентификатору.
 * 
 * <p>Принимает один аргумент — целое положительное число (id элемента).
 * После валидации id запрашивает у пользователя новые значения всех полей
 * Worker'а через {@link WorkerFactory}.</p>
 * 
 * <p>На сервер отправляется и id (в {@link ArgumentWrapper}), и новый объект Worker.
 * Сервер заменяет существующий элемент с указанным id на новый, сохраняя
 * оригинальные id и creationDate.</p>
 */
public class UpdateClientCommand implements ClientCommand {
    private final WorkerFactory factory;
    private final ScriptManager scriptManager;
    private final Client client;

    public UpdateClientCommand(WorkerFactory factory, ScriptManager scriptManager, Client client) {
        this.factory = factory;
        this.scriptManager = scriptManager;
        this.client = client;
    }

    @Override
    public String getName() {
        return "update";
    }

    @Override
    public String getDescription() {
        return "updates the value of a collection item by its id";
    }

    /**
     * Выполняет команду: проверяет аргументы, парсит id, создаёт Worker
     * через опрос пользователя и отправляет запрос на сервер.
     * 
     * @param args массив из одного элемента — строковое представление id
     * @return Response с результатом обновления, либо с сообщением об ошибке
     *         (неверный id, неверные данные Worker'а, элемент не найден)
     */
    @Override
    public Response execute(String... args) {
        try {
            CommandArgumentParser.checkArgumentCount(args, 1);
            Integer id = CommandArgumentParser.parseId(args);
            Worker worker = factory.createWorker(scriptManager.getCurrentReader());
            return client.send(new Request(CommandType.UPDATE, new ArgumentWrapper(id), worker));
        } catch (InvalidCommandArgumentException e) {
            return new Response(false, "Invalid command arguments: " + e.getMessage(), null);
        } catch (InvalidWorkerArgumentException e) {
            return new Response(false, "Failed to create worker: " + e.getMessage(), null);
        } catch (IOException e) {
            return new Response(false, "Failed to send request: " + e.getMessage(), null, true);
        }
    }
}