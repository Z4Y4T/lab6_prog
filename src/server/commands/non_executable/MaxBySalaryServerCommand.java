package server.commands.non_executable;

import common.net.Request;
import common.net.Response;
import server.managers.CollectionManager;

/**
 * Серверная команда {@code max_by_salary}.
 * Выводит элемент коллекции с максимальной зарплатой.
 * 
 * <p>
 * Не требует аргументов. Вызывает {@link CollectionManager#maxBySalary()}.
 * </p>
 */
public class MaxBySalaryServerCommand implements ServerCommand {

    @Override
    public Response execute(Request request, CollectionManager collectionManager) {
        String msg = collectionManager.maxBySalary();
        return new Response(true, msg, null);
    }
}