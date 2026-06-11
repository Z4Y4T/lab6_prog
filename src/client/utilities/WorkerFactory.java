package client.utilities;

import java.time.LocalDateTime;
import java.util.Arrays;

import common.domain.Address;
import common.domain.Coordinates;
import common.domain.Organization;
import common.domain.OrganizationType;
import common.domain.Position;
import common.domain.Status;
import common.domain.Worker;
import common.exceptions.InvalidWorkerArgumentException;
import common.parsers.Parser;
import common.parsers.WorkerParser;

/**
 * Утилитный класс для создания объектов {@link Worker} через опрос
 * пользователя.
 * 
 * <p>
 * Не имеет состояния (stateless) — все методы принимают источник ввода
 * {@link Reader} параметром. Может безопасно использоваться из нескольких
 * потоков.
 * </p>
 * 
 * <p>
 * Процесс создания Worker'а:
 * </p>
 * <ol>
 * <li>Клиентский код вызывает единственный публичный метод
 * {@link #createWorker(Reader)}</li>
 * <li>Приватный метод {@code buildWorker} последовательно запрашивает каждое
 * поле</li>
 * <li>Каждый запрос проходит через
 * {@link #ask(Reader, String, ThrowingFunction)} —
 * выводит подсказку (только в интерактиве), читает строку, валидирует через
 * парсер</li>
 * <li>При ошибке валидации в интерактивном режиме запрос повторяется
 * ({@link #retryAsk}),
 * в режиме скрипта — сразу выбрасывается
 * {@link InvalidWorkerArgumentException}</li>
 * <li>Собранные поля передаются в {@link Worker.WorkerBuilder#build()}</li>
 * </ol>
 * 
 * <p>
 * Опциональные поля (position, organization, employeesCount, postalAddress) —
 * пользователь может отказаться от ввода, тогда поле будет {@code null}.
 * </p>
 */
public class WorkerFactory {

    /**
     * Функциональный интерфейс, аналогичный {@link java.util.function.Function},
     * но позволяющий пробрасывать checked-исключение
     * {@link InvalidWorkerArgumentException}.
     * 
     * @param <T> тип входного значения (строка после нормализации)
     * @param <R> тип возвращаемого значения (результат валидации)
     */
    @FunctionalInterface
    private interface ThrowingFunction<T, R> {
        R apply(T t) throws InvalidWorkerArgumentException;
    }

    /**
     * Нормализует ввод: {@code null} остаётся {@code null},
     * пустая строка превращается в {@code null}.
     * Используется для опциональных полей.
     */
    private String normalize(String input) {
        if (input == null) {
            return null;
        }
        String trimmed = input.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * Запрашивает у пользователя значение поля.
     * В интерактивном режиме выводит сообщение-подсказку.
     * 
     * @param reader    источник ввода
     * @param message   подсказка (выводится только в интерактиве)
     * @param validator функция валидации (ссылка на метод {@link WorkerParser})
     * @return проверенное значение
     * @throws InvalidWorkerArgumentException если валидация не пройдена
     */
    private <T> T ask(Reader reader, String message, ThrowingFunction<String, T> validator)
            throws InvalidWorkerArgumentException {
        if (reader.isInteractive()) {
            System.out.print(message);
        }
        String input = normalize(reader.readLine());
        return validator.apply(input);
    }

    /**
     * Многократно вызывает {@link #ask}, пока не будет введено корректное значение.
     * В режиме скрипта при первой же ошибке выбрасывает исключение
     * (переспрашивать некого — источник данных не интерактивный).
     */
    private <T> T retryAsk(Reader reader, String message, ThrowingFunction<String, T> validator)
            throws InvalidWorkerArgumentException {
        while (true) {
            try {
                return ask(reader, message, validator);
            } catch (InvalidWorkerArgumentException e) {
                System.out.println(Parser.formatException(e));
                if (!reader.isInteractive()) {
                    throw e;
                }
            }
        }
    }

    /**
     * Проверяет, является ли ответ пользователя явным согласием.
     * Согласием считается только "yes" или "y" (без учёта регистра).
     * 
     * @param input строка ввода (может быть null)
     * @return {@code true} если явное "yes"/"y"
     */
    private boolean isPositiveAnswer(String input) {
        if (input == null) {
            return false;
        }
        String normalized = input.trim().toLowerCase();
        return normalized.equals("yes") || normalized.equals("y");
    }

    /**
     * Проверяет, является ли ответ пользователя явным отказом.
     * Отказом считается только "no" или "n" (без учёта регистра).
     * 
     * @param input строка ввода (может быть null)
     * @return {@code true} если явное "no"/"n"
     */
    private boolean isNegativeAnswer(String input) {
        if (input == null) {
            return false;
        }
        String normalized = input.trim().toLowerCase();
        return normalized.equals("no") || normalized.equals("n");
    }

    private String askName(Reader reader) throws InvalidWorkerArgumentException {
        return retryAsk(reader, "Enter worker's name: ", WorkerParser::checkName);
    }

    private Coordinates askCoordinates(Reader reader) throws InvalidWorkerArgumentException {
        long x = retryAsk(reader, "Enter x coordinate: ", WorkerParser::checkX);
        Float y = retryAsk(reader, "Enter y coordinate: ", WorkerParser::checkY);
        return new Coordinates(x, y);
    }

    private Float askSalary(Reader reader) throws InvalidWorkerArgumentException {
        return retryAsk(reader, "Enter worker's salary: ", WorkerParser::checkSalary);
    }

    private LocalDateTime askStartDate(Reader reader) throws InvalidWorkerArgumentException {
        return retryAsk(reader, "Enter worker's start time (yyyy-MM-dd'T'HH:mm): ", WorkerParser::checkStartDate);
    }

    private Position askPosition(Reader reader) throws InvalidWorkerArgumentException {
        if (reader.isInteractive()) {
            System.out.println("Available positions(case sensitive!): " + Arrays.toString(Position.values()));
        }
        return retryAsk(reader, "Enter position: ", WorkerParser::checkPosition);
    }

    private Status askStatus(Reader reader) throws InvalidWorkerArgumentException {
        if (reader.isInteractive()) {
            System.out.println("Available statuses(case sensitive!): " + Arrays.toString(Status.values()));
        }
        return retryAsk(reader, "Enter status: ", WorkerParser::checkStatus);
    }

    private Organization askOrganization(Reader reader) throws InvalidWorkerArgumentException {
        while (true) {
            if (reader.isInteractive()) {
                System.out.print("Does worker have an organization? (yes/no): ");
            }
            String answer = reader.readLine();

            if (isPositiveAnswer(answer)) {
                Integer employeesCount = retryAsk(reader, "Enter organization's employees count: ",
                        WorkerParser::checkEmployeesCount);
                if (reader.isInteractive()) {
                    System.out.println("Available organization types: " + Arrays.toString(OrganizationType.values()));
                }
                OrganizationType type = retryAsk(reader, "Enter organization's type(case sensitive!): ",
                        WorkerParser::checkOrganizationType);
                Address address = askAddress(reader);
                return new Organization(employeesCount, type, address);
            }

            if (isNegativeAnswer(answer)) {
                return null;
            }

            if (!reader.isInteractive()) {
                throw new InvalidWorkerArgumentException(
                        "Invalid answer for organization: '" + answer + "'. Expected yes/no");
            }
            System.out.println("Invalid input. Please type 'yes'/'y' or 'no'/'n'.");
        }
    }

    private Address askAddress(Reader reader) throws InvalidWorkerArgumentException {
        while (true) {
            if (reader.isInteractive()) {
                System.out.print("Does organization have postal address? (yes/no): ");
            }
            String answer = reader.readLine();

            if (isPositiveAnswer(answer)) {
                String zipCode = retryAsk(reader, "Enter organization's zip code: ", WorkerParser::checkZipCode);
                return new Address(zipCode);
            }

            if (isNegativeAnswer(answer)) {
                return null;
            }

            if (!reader.isInteractive()) {
                throw new InvalidWorkerArgumentException(
                        "Invalid answer for postal address: '" + answer + "'. Expected yes/no");
            }
            System.out.println("Invalid input. Please type 'yes'/'y' or 'no'/'n'.");
        }
    }

    /**
     * Единственный публичный метод — создаёт Worker, запрашивая все поля у
     * пользователя.
     * 
     * @param reader источник ввода (консоль или файл скрипта)
     * @return заполненный объект Worker (без id и creationDate — их назначит
     *         сервер)
     * @throws InvalidWorkerArgumentException если какое-либо поле не прошло
     *                                        валидацию
     *                                        (в режиме скрипта — сразу; в
     *                                        интерактиве — только если прерван
     *                                        ввод)
     */
    public Worker createWorker(Reader reader) throws InvalidWorkerArgumentException {
        return buildWorker(reader);
    }

    private Worker buildWorker(Reader reader) throws InvalidWorkerArgumentException {
        String name = askName(reader);
        Coordinates coordinates = askCoordinates(reader);
        float salary = askSalary(reader);
        LocalDateTime startDate = askStartDate(reader);
        Position position = askPosition(reader);
        Status status = askStatus(reader);
        Organization organization = askOrganization(reader);
        return new Worker.WorkerBuilder()
                .name(name)
                .coordinates(coordinates)
                .salary(salary)
                .startDate(startDate)
                .position(position)
                .status(status)
                .organization(organization)
                .build();
    }
}