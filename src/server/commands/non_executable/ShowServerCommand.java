package server.commands.non_executable;

import java.util.List;

import common.domain.Worker;
import common.net.Request;
import common.net.Response;
import server.managers.CollectionManager;

/**
 * Серверная команда {@code show}.
 * Выводит одну страницу коллекции, отсортированную по имени.
 * 
 * <p>
 * Принимает аргумент — номер страницы в
 * {@link common.ArgumentWrapper#getNum()}.
 * Размер страницы — {@value #PAGE_SIZE} элементов.
 * </p>
 */
public class ShowServerCommand implements ServerCommand {

    private static final int PAGE_SIZE = 100;

    @Override
    public Response execute(Request request, CollectionManager collectionManager) {
        if (collectionManager.getWorkerList().isEmpty()) {
            return new Response(true, "Worker list is empty", null);
        }

        int page = request.getArgument() != null && request.getArgument().getNum() != null
                ? request.getArgument().getNum()
                : 0;

        List<Worker> pageList = collectionManager.getPage(page, PAGE_SIZE);

        if (pageList.isEmpty()) {
            int totalElements = collectionManager.getWorkerList().size();
            int totalPages = (int) Math.ceil((double) totalElements / PAGE_SIZE);
            return new Response(true,
                    "Page " + page + " is empty. Total pages: " + totalPages + ", elements: " + totalElements, null);
        }

        Worker[] workers = pageList.toArray(new Worker[0]);
        int totalElements = collectionManager.getWorkerList().size();
        int totalPages = (int) Math.ceil((double) totalElements / PAGE_SIZE);
        int fromIndex = page * PAGE_SIZE;
        String message = String.format("Page %d/%d (elements %d-%d of %d)",
                page + 1, totalPages, fromIndex + 1, fromIndex + pageList.size(), totalElements);

        return new Response(true, message, workers);
    }
}