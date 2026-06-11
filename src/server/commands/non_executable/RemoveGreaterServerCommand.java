package server.commands.non_executable;

import common.net.Request;
import common.net.Response;
import server.managers.CollectionManager;

/**
 * Серверная команда {@code remove_greater}.
 * Удаляет все элементы, превышающие заданный.
 * 
 * <p>
 * Требует объект {@link common.domain.Worker} в {@link Request#getWorker()}
 * в качестве эталона для сравнения.
 * Если Worker отсутствует — возвращает ошибку.
 * </p>
 */
public class RemoveGreaterServerCommand implements ServerCommand {

    @Override
    public Response execute(Request request, CollectionManager collectionManager) {
        if (request.getWorker() == null)
            return new Response(false, "Missing worker payload", null);
        String msg = collectionManager.removeGreater(request.getWorker());
        return new Response(true, msg, null);
    }
}