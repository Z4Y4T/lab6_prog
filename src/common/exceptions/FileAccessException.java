package common.exceptions;

/**
 * Исключение при ошибке доступа к файлу.
 * 
 * <p>
 * Возникает при сохранении коллекции в XML-файл, если:
 * </p>
 * <ul>
 * <li>Нет прав на запись в директорию</li>
 * <li>Диск переполнен</li>
 * <li>Ошибка ввода-вывода при записи</li>
 * </ul>
 * 
 * <p>
 * Используется только на серверной стороне.
 * </p>
 */
public class FileAccessException extends Exception {

    /**
     * Создаёт исключение с сообщением.
     *
     * @param message описание ошибки
     */
    public FileAccessException(String message) {
        super(message);
    }

    /**
     * Создаёт исключение с сообщением и причиной.
     *
     * @param message описание ошибки
     * @param cause   исходное исключение ({@link java.io.IOException})
     */
    public FileAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}