package server.commands.non_executable;

import common.net.Request;
import common.net.Response;
import server.managers.CollectionManager;

/**
 * Интерфейс серверной команды, выполняемой по запросу от клиента.
 * 
 * <p>
 * Каждая реализация обрабатывает {@link Request}, взаимодействует
 * с {@link CollectionManager} и возвращает {@link Response}.
 * В отличие от {@link client.commands.ClientCommand}, не занимается
 * валидацией аргументов — она уже выполнена на клиенте.
 * Сервер только проверяет наличие необходимых данных в запросе.
 * </p>
 */
public interface ServerCommand {

    /**
     * Выполняет команду.
     *
     * @param request           запрос от клиента (содержит команду, аргументы и
     *                          Worker)
     * @param collectionManager менеджер коллекции для выполнения операций
     * @return ответ с результатом выполнения
     */
    Response execute(Request request, CollectionManager collectionManager);
}