package common.exceptions;

/**
 * Исключение при ошибке в аргументах команды.
 * 
 * <p>
 * Возникает в {@link common.parsers.CommandArgumentParser}, когда:
 * </p>
 * <ul>
 * <li>Количество аргументов не совпадает с ожидаемым</li>
 * <li>Аргумент имеет неверный тип (например, буквы вместо числа)</li>
 * <li>Аргумент выходит за допустимый диапазон</li>
 * <li>Неверный формат даты</li>
 * <li>Указанный файл не существует или недоступен</li>
 * </ul>
 * 
 * <p>
 * Обрабатывается на клиенте — запрос на сервер не отправляется.
 * </p>
 */
public class InvalidCommandArgumentException extends Exception {

    /**
     * Создаёт исключение с сообщением.
     *
     * @param message описание ошибки
     */
    public InvalidCommandArgumentException(String message) {
        super(message);
    }
}