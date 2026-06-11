package server.commands.non_executable;

import common.net.Request;
import common.net.Response;
import server.managers.CollectionManager;

/**
 * Серверная команда {@code clear}.
 * Полностью очищает коллекцию.
 * 
 * <p>
 * Не требует аргументов. Вызывает {@link CollectionManager#clear()}.
 * </p>
 */
public class ClearServerCommand implements ServerCommand {

    @Override
    public Response execute(Request request, CollectionManager collectionManager) {
        String msg = collectionManager.clear();
        return new Response(true, msg, null);
    }
}