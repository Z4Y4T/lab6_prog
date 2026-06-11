package common.net;

import java.io.Serializable;

import common.domain.Worker;

/**
 * Ответ от сервера клиенту.
 * 
 * <p>
 * Содержит результат выполнения команды:
 * </p>
 * <ul>
 * <li>{@code status} — успешность операции (true/false)</li>
 * <li>{@code message} — текстовое сообщение (результат или описание
 * ошибки)</li>
 * <li>{@code workers} — массив Worker'ов (для команды show, иначе null)</li>
 * <li>{@code connectionError} — признак ошибки соединения (не
 * сериализуется)</li>
 * </ul>
 */
public class Response implements Serializable {
    private static final long serialVersionUID = 3L;
    private final boolean status;
    private final String message;
    private final Worker[] workers;
    private final transient boolean connectionError;

    /**
     * Создаёт ответ сервера (без ошибки соединения).
     *
     * @param status  успешность операции
     * @param message текстовое сообщение
     * @param workers массив Worker'ов (может быть null)
     */
    public Response(boolean status, String message, Worker[] workers) {
        this(status, message, workers, false);
    }

    /**
     * Создаёт ответ сервера с явным указанием признака ошибки соединения.
     *
     * @param status          успешность операции
     * @param message         текстовое сообщение
     * @param workers         массив Worker'ов (может быть null)
     * @param connectionError true, если ошибка связана с сетью
     */
    public Response(boolean status, String message, Worker[] workers, boolean connectionError) {
        this.status = status;
        this.message = message;
        this.workers = workers;
        this.connectionError = connectionError;
    }

    public boolean isSuccessful() {
        return status;
    }

    /**
     * Проверяет, вызван ли ответ ошибкой соединения (таймаут, разрыв).
     * Используется клиентом для отдельной обработки сетевых ошибок.
     *
     * @return true, если ошибка соединения
     */
    public boolean isConnectionError() {
        return connectionError;
    }

    public String getMessage() {
        return message;
    }

    public Worker[] getWorkers() {
        return workers;
    }
}