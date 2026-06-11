package server.commands.non_executable;

import common.net.Request;
import common.net.Response;
import server.managers.CollectionManager;

/**
 * Серверная команда {@code info}.
 * Выводит информацию о коллекции: тип, дату инициализации, количество
 * элементов.
 * 
 * <p>
 * Не требует аргументов. Вызывает {@link CollectionManager#toString()}.
 * </p>
 */
public class InfoServerCommand implements ServerCommand {

    @Override
    public Response execute(Request request, CollectionManager collectionManager) {
        return new Response(true, collectionManager.toString(), null);
    }
}