package server.commands.non_executable;

import common.net.Request;
import common.net.Response;
import server.managers.CollectionManager;

/**
 * Серверная команда {@code update}.
 * Обновляет элемент коллекции по заданному id.
 * 
 * <p>
 * Требует аргумент: id в {@link common.ArgumentWrapper#getNum()}
 * и объект {@link common.domain.Worker} в {@link Request#getWorker()}
 * с новыми данными. Если что-то отсутствует — возвращает ошибку.
 * </p>
 */
public class UpdateServerCommand implements ServerCommand {

    @Override
    public Response execute(Request request, CollectionManager collectionManager) {
        if (request.getArgument() == null)
            return new Response(false, "Missing argument", null);
        if (request.getWorker() == null)
            return new Response(false, "Missing worker payload", null);
        Integer id = request.getArgument().getNum();
        if (id == null)
            return new Response(false, "Missing id in argument", null);
        String msg = collectionManager.update(id, request.getWorker());
        return new Response(true, msg, null);
    }
}