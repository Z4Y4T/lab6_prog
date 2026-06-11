package common.parsers;

import java.time.LocalDateTime;

import common.domain.OrganizationType;
import common.domain.Position;
import common.domain.Status;
import common.exceptions.InvalidWorkerArgumentException;

/**
 * Парсер и валидатор полей {@link common.domain.Worker}.
 * 
 * <p>
 * Каждый метод проверяет одно поле Worker'а: обязательность (не null),
 * тип данных и диапазон допустимых значений. Использует {@link Parser}
 * для базовых операций парсинга.
 * </p>
 * 
 * <p>
 * Все методы бросают {@link InvalidWorkerArgumentException} —
 * checked-исключение, которое обрабатывается на клиенте
 * (повторный запрос ввода или ошибка в скрипте).
 * </p>
 */
public class WorkerParser {

	/**
	 * Проверяет имя: не может быть null.
	 */
	public static String checkName(String input) throws InvalidWorkerArgumentException {
		try {
			return Parser.requireNonNull(input, "Name");
		} catch (IllegalArgumentException e) {
			throw new InvalidWorkerArgumentException(e.getMessage());
		}
	}

	/**
	 * Проверяет координату X: целое число, не более 53.
	 */
	public static long checkX(String input) throws InvalidWorkerArgumentException {
		try {
			long x = Parser.parseLong(Parser.requireNonNull(input, "X"), "X");
			return Parser.validateRange(x, null, null, 53.0, Parser.BoundType.INCLUSIVE, "X");
		} catch (IllegalArgumentException e) {
			throw new InvalidWorkerArgumentException(e.getMessage());
		}
	}

	/**
	 * Проверяет координату Y: дробное число, больше -63.
	 */
	public static Float checkY(String input) throws InvalidWorkerArgumentException {
		try {
			Float y = Parser.parseFloat(Parser.requireNonNull(input, "Y"), "Y");
			return Parser.validateRange(y, -63.0, Parser.BoundType.EXCLUSIVE, null, null, "Y");
		} catch (IllegalArgumentException e) {
			throw new InvalidWorkerArgumentException(e.getMessage());
		}
	}

	/**
	 * Проверяет зарплату: дробное число, больше 0.
	 */
	public static float checkSalary(String input) throws InvalidWorkerArgumentException {
		try {
			Float salary = Parser.parseFloat(Parser.requireNonNull(input, "Salary"), "Salary");
			return Parser.validateRange(salary, 0.0, Parser.BoundType.EXCLUSIVE, null, null, "Salary");
		} catch (IllegalArgumentException e) {
			throw new InvalidWorkerArgumentException(e.getMessage());
		}
	}

	/**
	 * Проверяет дату начала: не null, формат "yyyy-MM-dd'T'HH:mm".
	 */
	public static LocalDateTime checkStartDate(String input) throws InvalidWorkerArgumentException {
		try {
			Parser.requireNonNull(input, "Start date");
			return Parser.parseDateTime(input, "Start date", "yyyy-MM-dd'T'HH:mm");
		} catch (IllegalArgumentException e) {
			throw new InvalidWorkerArgumentException(e.getMessage());
		}
	}

	/**
	 * Проверяет должность: может быть null или одной из констант {@link Position}.
	 */
	public static Position checkPosition(String input) throws InvalidWorkerArgumentException {
		try {
			if (input == null || input.trim().isEmpty()) {
				return null;
			}
			return Parser.parseEnum(input, Position.class, "Position");
		} catch (IllegalArgumentException e) {
			throw new InvalidWorkerArgumentException(e.getMessage());
		}
	}

	/**
	 * Проверяет статус: обязательное, одна из констант {@link Status}.
	 */
	public static Status checkStatus(String input) throws InvalidWorkerArgumentException {
		try {
			return Parser.parseEnum(Parser.requireNonNull(input, "Status"), Status.class, "Status");
		} catch (IllegalArgumentException e) {
			throw new InvalidWorkerArgumentException(e.getMessage());
		}
	}

	/**
	 * Проверяет количество сотрудников: может быть null или целым числом больше 0.
	 */
	public static Integer checkEmployeesCount(String input) throws InvalidWorkerArgumentException {
		try {
			if (input == null || input.trim().isEmpty()) {
				return null;
			}
			Integer count = Parser.parseInteger(input, "Employees count");
			return Parser.validateRange(count, 0.0, Parser.BoundType.EXCLUSIVE, null, null, "Employees count");
		} catch (IllegalArgumentException e) {
			throw new InvalidWorkerArgumentException(e.getMessage());
		}
	}

	/**
	 * Проверяет тип организации: обязательное, одна из констант
	 * {@link OrganizationType}.
	 */
	public static OrganizationType checkOrganizationType(String input) throws InvalidWorkerArgumentException {
		try {
			return Parser.parseEnum(Parser.requireNonNull(input, "Organization type"), OrganizationType.class,
					"Organization type");
		} catch (IllegalArgumentException e) {
			throw new InvalidWorkerArgumentException(e.getMessage());
		}
	}

	/**
	 * Проверяет почтовый индекс: не может быть null.
	 */
	public static String checkZipCode(String input) throws InvalidWorkerArgumentException {
		try {
			return Parser.requireNonNull(input, "Zip code");
		} catch (IllegalArgumentException e) {
			throw new InvalidWorkerArgumentException(e.getMessage());
		}
	}
}