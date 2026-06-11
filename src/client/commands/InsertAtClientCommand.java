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
 * Клиентская команда {@code insert_at}.
 * Вставляет новый элемент на указанную позицию в коллекции.
 * 
 * <p>
 * Принимает один аргумент — целое неотрицательное число (индекс позиции).
 * После валидации индекса запрашивает у пользователя все поля Worker'а
 * через {@link WorkerFactory}.
 * </p>
 * 
 * <p>
 * На сервер отправляется и индекс (в {@link ArgumentWrapper}), и новый объект
 * Worker.
 * Сервер проверяет, что индекс не выходит за границы коллекции, и выполняет
 * вставку.
 * </p>
 */
public class InsertAtClientCommand implements ClientCommand {
    private final WorkerFactory factory;
    private final ScriptManager scriptManager;
    private final Client client;

    public InsertAtClientCommand(WorkerFactory factory, ScriptManager scriptManager, Client client) {
        this.factory = factory;
        this.scriptManager = scriptManager;
        this.client = client;
    }

    @Override
    public String getName() {
        return "insert_at";
    }

    @Override
    public String getDescription() {
        return "inserts a new item at the specified index";
    }

    /**
     * Выполняет команду: проверяет аргументы, парсит индекс, создаёт Worker
     * через опрос пользователя и отправляет запрос на сервер.
     * 
     * @param args массив из одного элемента — строковое представление индекса
     * @return Response с результатом вставки, либо с сообщением об ошибке
     *         (неверный индекс, выход за границы, неверные данные Worker'а)
     */
    @Override
    public Response execute(String... args) {
        try {
            CommandArgumentParser.checkArgumentCount(args, 1);
            Integer index = CommandArgumentParser.parseIndex(args);
            Worker worker = factory.createWorker(scriptManager.getCurrentReader());
            return client.send(new Request(CommandType.INSERT_AT, new ArgumentWrapper(index), worker));
        } catch (InvalidCommandArgumentException e) {
            return new Response(false, "Invalid command arguments: " + e.getMessage(), null);
        } catch (InvalidWorkerArgumentException e) {
            return new Response(false, "Failed to create worker: " + e.getMessage(), null);
        } catch (IOException e) {
            return new Response(false, "Failed to send request: " + e.getMessage(), null, true);
        }
    }
}