package common.parsers;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

/**
 * Утилитный класс для парсинга и валидации данных.
 * 
 * <p>
 * Содержит статические методы для преобразования строк в типизированные
 * значения
 * и проверки ограничений (диапазоны, не-null). Используется как
 * {@link WorkerParser}
 * (для полей Worker'а), так и {@link CommandArgumentParser} (для аргументов
 * команд).
 * </p>
 * 
 * <p>
 * Все методы бросают {@link IllegalArgumentException} при некорректных данных.
 * Вызывающий код оборачивает их в соответствующие checked-исключения.
 * </p>
 */
public class Parser {

    /**
     * Тип границы диапазона — включительная или исключительная.
     */
    public enum BoundType {
        INCLUSIVE,
        EXCLUSIVE
    }

    /**
     * Проверяет, что строка не null.
     *
     * @param input входная строка
     * @param field имя поля (для сообщения об ошибке)
     * @return та же строка
     * @throws IllegalArgumentException если строка null
     */
    public static String requireNonNull(String input, String field) throws IllegalArgumentException {
        if (input == null) {
            throw new IllegalArgumentException(field + " cannot be null");
        }
        return input;
    }

    /**
     * Парсит строку в Integer.
     *
     * @param input строковое представление числа
     * @param field имя поля
     * @return целое число
     * @throws IllegalArgumentException если строка не является целым числом
     */
    public static Integer parseInteger(String input, String field) throws IllegalArgumentException {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(field + " must be Integer");
        }
    }

    /**
     * Парсит строку в Long.
     *
     * @param input строковое представление числа
     * @param field имя поля
     * @return длинное целое
     * @throws IllegalArgumentException если строка не является числом
     */
    public static Long parseLong(String input, String field) throws IllegalArgumentException {
        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(field + " must be Long");
        }
    }

    /**
     * Парсит строку в Float.
     * Поддерживает запятую как десятичный разделитель.
     * Ограничивает точность четырьмя знаками после запятой.
     *
     * @param input строковое представление числа
     * @param field имя поля
     * @return дробное число
     * @throws IllegalArgumentException если строка не является числом
     */
    public static Float parseFloat(String input, String field) throws IllegalArgumentException {
        try {
            String normalized = input.replace(',', '.');
            int dotIndex = normalized.indexOf('.');
            if (dotIndex != -1 && normalized.length() - dotIndex - 1 > 4) {
                normalized = normalized.substring(0, dotIndex + 5);
            }
            return Float.parseFloat(normalized);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(field + " must be Float");
        }
    }

    /**
     * Парсит строку в значение перечисления.
     *
     * @param input     строковое представление константы
     * @param enumClass класс перечисления
     * @param field     имя поля
     * @param <E>       тип перечисления
     * @return значение перечисления
     * @throws IllegalArgumentException если такой константы нет
     */
    public static <E extends Enum<E>> E parseEnum(String input, Class<E> enumClass, String field)
            throws IllegalArgumentException {
        try {
            return Enum.valueOf(enumClass, input);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    field + " must be one of " + Arrays.toString(enumClass.getEnumConstants()));
        }
    }

    /**
     * Парсит и валидирует хост.
     *
     * @param input строковое представление хоста
     * @param field имя поля
     * @return строка хоста без пробелов по краям
     * @throws IllegalArgumentException если строка null или пустая
     */
    public static String parseHost(String input, String field) throws IllegalArgumentException {
        String host = requireNonNull(input, field).trim();
        if (host.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be empty");
        }
        return host;
    }

    /**
     * Парсит строку в порт и проверяет диапазон (1-65535).
     *
     * @param input строковое представление порта
     * @param field имя поля
     * @return номер порта
     * @throws IllegalArgumentException если не число или вне диапазона
     */
    public static Integer parsePort(String input, String field) throws IllegalArgumentException {
        Integer port = parseInteger(input, field);
        return validateRange(port, 1.0, BoundType.INCLUSIVE, 65535.0, BoundType.INCLUSIVE, field);
    }

    /**
     * Проверяет, что число попадает в заданный диапазон.
     *
     * @param num     проверяемое число
     * @param min     нижняя граница (null — без ограничения)
     * @param minType тип нижней границы
     * @param max     верхняя граница (null — без ограничения)
     * @param maxType тип верхней границы
     * @param field   имя поля
     * @param <T>     тип числа
     * @return то же число, если проверка пройдена
     * @throws IllegalArgumentException если число вне диапазона
     */
    public static <T extends Number> T validateRange(T num, Double min, BoundType minType, Double max,
            BoundType maxType, String field) throws IllegalArgumentException {
        double val = num.doubleValue();
        if (min != null) {
            boolean invalid = (minType == BoundType.INCLUSIVE) ? val < min : val <= min;
            if (invalid) {
                String sign = (minType == BoundType.INCLUSIVE) ? ">=" : ">";
                throw new IllegalArgumentException(field + " must be " + sign + " " + min);
            }
        }
        if (max != null) {
            boolean invalid = (maxType == BoundType.INCLUSIVE) ? val > max : val >= max;
            if (invalid) {
                String sign = (maxType == BoundType.INCLUSIVE) ? "<=" : "<";
                throw new IllegalArgumentException(field + " must be " + sign + " " + max);
            }
        }
        return num;
    }

    /**
     * Парсит строку в дату и время по указанному формату.
     *
     * @param input  строковое представление даты
     * @param field  имя поля
     * @param format формат (например, "yyyy-MM-dd'T'HH:mm")
     * @return дата и время
     * @throws IllegalArgumentException если не соответствует формату
     */
    public static LocalDateTime parseDateTime(String input, String field, String format)
            throws IllegalArgumentException {
        try {
            return LocalDateTime.parse(input, DateTimeFormatter.ofPattern(format));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(field + " must be in format " + format);
        }
    }

    /**
     * Проверяет существование и доступность файла.
     *
     * @param filename путь к файлу
     * @return объект File
     * @throws FileNotFoundException если файл не существует
     * @throws SecurityException     если нет прав на чтение или путь указывает на
     *                               директорию
     */
    public static File parseFile(String filename) throws FileNotFoundException, SecurityException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new FileNotFoundException("File does not exist");
        }
        if (!file.canRead()) {
            throw new SecurityException("Permission denied");
        }
        if (file.isDirectory()) {
            throw new SecurityException("Path is a directory");
        }
        return file;
    }

    /**
     * Возвращает краткое описание исключения: имя класса + сообщение.
     *
     * @param exc исключение
     */
    public static String formatException(Throwable exc) {
        return exc.getClass().getSimpleName() + ": " + exc.getMessage();
    }
}