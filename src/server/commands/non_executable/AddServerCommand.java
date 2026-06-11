package server.commands.non_executable;

import common.domain.Worker;
import common.net.Request;
import common.net.Response;
import server.managers.CollectionManager;

/**
 * Серверная команда {@code add}.
 * Добавляет новый элемент в коллекцию.
 * 
 * <p>
 * Требует объект {@link Worker} в {@link Request#getWorker()}.
 * Если Worker отсутствует — возвращает ошибку.
 * Id и дата создания присваиваются автоматически.
 * </p>
 */
public class AddServerCommand implements ServerCommand {

    @Override
    public Response execute(Request request, CollectionManager collectionManager) {
        Worker worker = request.getWorker();
        if (worker == null)
            return new Response(false, "Missing worker payload", null);
        String msg = collectionManager.add(worker);
        return new Response(true, msg, null);
    }
}