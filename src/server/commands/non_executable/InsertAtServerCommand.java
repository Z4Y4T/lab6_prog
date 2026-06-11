package server.commands.non_executable;

import common.net.Request;
import common.net.Response;
import common.parsers.Parser;
import server.managers.CollectionManager;

/**
 * Серверная команда {@code insert_at}.
 * Вставляет новый элемент на указанную позицию.
 * 
 * <p>
 * Требует аргумент: индекс в {@link common.ArgumentWrapper#getNum()}
 * и объект {@link common.domain.Worker} в {@link Request#getWorker()}.
 * Индекс должен быть в диапазоне от 0 до {@code size() - 1} включительно.
 * Если что-то отсутствует или индекс вне диапазона — возвращает ошибку.
 * </p>
 */
public class InsertAtServerCommand implements ServerCommand {

    @Override
    public Response execute(Request request, CollectionManager collectionManager) {
        if (request.getArgument() == null)
            return new Response(false, "Missing argument", null);
        if (request.getWorker() == null)
            return new Response(false, "Missing worker payload", null);
        Integer idx = request.getArgument().getNum();
        if (idx == null)
            return new Response(false, "Missing index in argument", null);

        int maxIndex = Math.max(0, collectionManager.getWorkerList().size() - 1);
        try {
            Parser.validateRange(idx, 0.0, Parser.BoundType.INCLUSIVE,
                    (double) maxIndex, Parser.BoundType.INCLUSIVE, "Index");
        } catch (IllegalArgumentException e) {
            return new Response(false, e.getMessage(), null);
        }

        String msg = collectionManager.insert(idx, request.getWorker());
        return new Response(true, msg, null);
    }
}