package common.exceptions;

/**
 * Исключение при попытке выполнить неизвестную команду.
 * 
 * <p>
 * Возникает в {@link client.managers.ClientCommandManager#getCommand(String)},
 * когда пользователь вводит имя команды, которое не зарегистрировано.
 * Не требует отправки запроса на сервер — обрабатывается на клиенте.
 * </p>
 */
public class InvalidCommandException extends Exception {

    /**
     * Создаёт исключение с сообщением.
     *
     * @param message сообщение для пользователя (обычно предлагает ввести 'help')
     */
    public InvalidCommandException(String message) {
        super(message);
    }
}