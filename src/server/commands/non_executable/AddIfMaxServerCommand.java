package server.commands.non_executable;

import common.net.Request;
import common.net.Response;
import server.managers.CollectionManager;

/**
 * Серверная команда {@code add_if_max}.
 * Добавляет элемент, только если он больше максимального в коллекции.
 * 
 * <p>
 * Требует объект {@link common.domain.Worker} в {@link Request#getWorker()}.
 * Если Worker отсутствует — возвращает ошибку.
 * Сравнение через {@link common.domain.Worker#compareTo(common.domain.Worker)}
 * (сначала по имени, затем по id).
 * </p>
 */
public class AddIfMaxServerCommand implements ServerCommand {

    @Override
    public Response execute(Request request, CollectionManager collectionManager) {
        if (request.getWorker() == null)
            return new Response(false, "Missing worker payload", null);
        String msg = collectionManager.addIfMax(request.getWorker());
        return new Response(true, msg, null);
    }
}