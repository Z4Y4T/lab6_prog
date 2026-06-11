package server.commands.non_executable;

import common.net.Request;
import common.net.Response;
import server.managers.CollectionManager;

/**
 * Серверная команда {@code remove_by_id}.
 * Удаляет элемент коллекции по заданному id.
 * 
 * <p>
 * Требует аргумент: id (целое число) в {@link common.ArgumentWrapper#getNum()}.
 * Если аргумент отсутствует — возвращает ошибку.
 * </p>
 */
public class RemoveByIdServerCommand implements ServerCommand {

    @Override
    public Response execute(Request request, CollectionManager collectionManager) {
        if (request.getArgument() == null)
            return new Response(false, "Missing argument", null);
        Integer id = request.getArgument().getNum();
        if (id == null)
            return new Response(false, "Missing id in argument", null);
        String msg = collectionManager.remove(id);
        return new Response(true, msg, null);
    }
}