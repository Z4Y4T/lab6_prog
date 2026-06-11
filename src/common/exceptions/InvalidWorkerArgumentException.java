package common.exceptions;

/**
 * Исключение при ошибке валидации полей {@link common.domain.Worker}.
 * 
 * <p>
 * Возникает, когда пользователь вводит некорректные данные:
 * </p>
 * <ul>
 * <li>Числовое поле содержит буквы</li>
 * <li>Значение выходит за допустимый диапазон</li>
 * <li>Поле перечисления содержит несуществующее значение</li>
 * <li>Обязательное поле оставлено пустым</li>
 * <li>Неверный формат даты</li>
 * </ul>
 * 
 * <p>
 * В интерактивном режиме клиент даёт пользователю повторную попытку ввода.
 * В режиме скрипта — немедленно прерывает выполнение с ошибкой.
 * </p>
 */
public class InvalidWorkerArgumentException extends Exception {

    /**
     * Создаёт исключение с сообщением.
     *
     * @param message описание ошибки валидации
     */
    public InvalidWorkerArgumentException(String message) {
        super(message);
    }

    /**
     * Создаёт исключение с сообщением и причиной.
     *
     * @param message описание ошибки
     * @param cause   исходное исключение
     */
    public InvalidWorkerArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}