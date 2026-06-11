package server.commands.non_executable;

import common.net.Request;
import common.net.Response;
import server.managers.CollectionManager;

/**
 * Серверная команда {@code print_unique_start_date}.
 * Выводит все уникальные значения дат начала работы.
 * 
 * <p>
 * Не требует аргументов. Вызывает
 * {@link CollectionManager#printUniqueStartDate()}.
 * </p>
 */
public class PrintUniqueStartDateServerCommand implements ServerCommand {

    @Override
    public Response execute(Request request, CollectionManager collectionManager) {
        String msg = collectionManager.printUniqueStartDate();
        return new Response(true, msg, null);
    }
}