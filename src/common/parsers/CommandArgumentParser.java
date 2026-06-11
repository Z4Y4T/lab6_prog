package common.parsers;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;

import common.exceptions.InvalidCommandArgumentException;

/**
 * Парсер аргументов команд.
 * 
 * <p>
 * Содержит статические методы для проверки количества аргументов
 * и их преобразования в типизированные значения: id, индекс, дата, файл.
 * Все методы бросают {@link InvalidCommandArgumentException}
 * при некорректных данных.
 * </p>
 */
public class CommandArgumentParser {

	/**
	 * Проверяет, что количество аргументов совпадает с ожидаемым.
	 *
	 * @param args          массив аргументов
	 * @param expectedCount ожидаемое количество
	 * @throws InvalidCommandArgumentException если количество не совпадает
	 */
	public static void checkArgumentCount(String[] args, int expectedCount)
			throws InvalidCommandArgumentException {
		if (args.length != expectedCount) {
			throw new InvalidCommandArgumentException(
					String.format("Expected %d arguments, but got %d", expectedCount, args.length));
		}
	}

	/**
	 * Парсит первый аргумент как id (целое число ≥ 0).
	 *
	 * @param args массив аргументов
	 * @return id
	 * @throws InvalidCommandArgumentException если аргумент невалиден
	 */
	public static Integer parseId(String[] args) throws InvalidCommandArgumentException {
		try {
			Integer id = Parser.parseInteger(args[0], "Id");
			return Parser.validateRange(id, 0.0, Parser.BoundType.INCLUSIVE, null, null, "Id");
		} catch (IllegalArgumentException e) {
			throw new InvalidCommandArgumentException(e.getMessage());
		}
	}

	/**
	 * Парсит первый аргумент как индекс (целое число ≥ 0).
	 *
	 * @param args массив аргументов
	 * @return индекс
	 * @throws InvalidCommandArgumentException если аргумент невалиден
	 */
	public static Integer parseIndex(String[] args) throws InvalidCommandArgumentException {
		try {
			Integer index = Parser.parseInteger(args[0], "Index");
			return Parser.validateRange(index, 0.0, Parser.BoundType.INCLUSIVE, null, null, "Index");
		} catch (IllegalArgumentException e) {
			throw new InvalidCommandArgumentException(e.getMessage());
		}
	}

	/**
	 * Парсит первый аргумент как дату в формате "yyyy-MM-dd'T'HH:mm".
	 *
	 * @param args массив аргументов
	 * @return дата и время
	 * @throws InvalidCommandArgumentException если формат неверный
	 */
	public static LocalDateTime parseStartDate(String[] args) throws InvalidCommandArgumentException {
		try {
			String input = args[0];
			Parser.requireNonNull(input, "Start date");
			return Parser.parseDateTime(input, "Start date", "yyyy-MM-dd'T'HH:mm");
		} catch (IllegalArgumentException e) {
			throw new InvalidCommandArgumentException(e.getMessage());
		}
	}

	/**
	 * Парсит первый аргумент как путь к файлу.
	 * Проверяет существование файла и права на чтение.
	 *
	 * @param args массив аргументов
	 * @return объект File
	 * @throws InvalidCommandArgumentException если файл не найден или нет прав
	 */
	public static File parseFile(String[] args) throws InvalidCommandArgumentException {
		try {
			return Parser.parseFile(args[0]);
		} catch (SecurityException | FileNotFoundException e) {
			throw new InvalidCommandArgumentException(e.getMessage());
		}
	}
}