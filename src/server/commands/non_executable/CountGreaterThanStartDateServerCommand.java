package server.commands.non_executable;

import common.net.Request;
import common.net.Response;
import server.managers.CollectionManager;

/**
 * Серверная команда {@code count_greater_than_start_date}.
 * Подсчитывает количество элементов с датой начала позже указанной.
 * 
 * <p>
 * Требует аргумент: дата ({@link java.time.LocalDateTime}) в
 * {@link common.ArgumentWrapper#getLocalDateTime()}.
 * Если аргумент отсутствует — возвращает ошибку.
 * </p>
 */
public class CountGreaterThanStartDateServerCommand implements ServerCommand {

    @Override
    public Response execute(Request request, CollectionManager collectionManager) {
        if (request.getArgument() == null || request.getArgument().getLocalDateTime() == null)
            return new Response(false, "Missing date argument", null);
        String msg = collectionManager.countGreaterThanStartDate(request.getArgument().getLocalDateTime());
        return new Response(true, msg, null);
    }
}