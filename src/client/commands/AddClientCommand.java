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
 * Клиентская команда {@code add}.
 * Добавляет новый элемент в коллекцию.
 * 
 * <p>
 * Не принимает аргументов командной строки. Вместо этого запрашивает
 * у пользователя все поля Worker'а через {@link WorkerFactory}.
 * В интерактивном режиме при ошибке валидации запрос повторяется,
 * в режиме скрипта — выполнение прерывается с ошибкой.
 * </p>
 * 
 * <p>
 * После успешного создания объекта отправляет его на сервер.
 * Поля id и creationDate не запрашиваются — они присваиваются сервером.
 * </p>
 */
public class AddClientCommand implements ClientCommand {
    private final WorkerFactory factory;
    private final ScriptManager scriptManager;
    private final Client client;

    public AddClientCommand(WorkerFactory factory, ScriptManager scriptManager, Client client) {
        this.factory = factory;
        this.scriptManager = scriptManager;
        this.client = client;
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "adds a new item to the collection";
    }

    /**
     * Выполняет команду: проверяет отсутствие аргументов, создаёт Worker
     * через опрос пользователя и отправляет запрос на сервер.
     * 
     * @param args должен быть пустым
     * @return Response с результатом добавления, либо с сообщением об ошибке
     *         (неверные данные Worker'а, ошибка соединения)
     */
    @Override
    public Response execute(String... args) {
        try {
            CommandArgumentParser.checkArgumentCount(args, 0);
            Worker worker = factory.createWorker(scriptManager.getCurrentReader());
            return client.send(new Request(CommandType.ADD, null, worker));
        } catch (InvalidCommandArgumentException e) {
            return new Response(false, "Invalid command arguments: " + e.getMessage(), null);
        } catch (InvalidWorkerArgumentException e) {
            return new Response(false, "Failed to create worker: " + e.getMessage(), null);
        } catch (IOException e) {
            return new Response(false, "Failed to send request: " + e.getMessage(), null, true);
        }
    }
}