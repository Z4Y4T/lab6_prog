package common.exceptions;

/**
 * Исключение при ошибке парсинга XML-файла с коллекцией.
 * 
 * <p>
 * Возникает при загрузке коллекции из файла, если:
 * </p>
 * <ul>
 * <li>Нарушена структура XML (отсутствуют обязательные теги)</li>
 * <li>Данные внутри тегов не проходят валидацию</li>
 * <li>Обнаружен дублирующийся id</li>
 * </ul>
 */
public class XMLParsingException extends Exception {

    /**
     * Создаёт исключение с сообщением.
     *
     * @param message описание ошибки
     */
    public XMLParsingException(String message) {
        super(message);
    }

    /**
     * Создаёт исключение с сообщением и причиной.
     *
     * @param message описание ошибки
     * @param cause   исходное исключение, вызвавшее ошибку
     */
    public XMLParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}