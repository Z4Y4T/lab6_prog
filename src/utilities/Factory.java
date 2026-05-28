package utilities;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;

import exceptions.InvalidWorkerArgumentException;
import managers.ExceptionManager;
import objects.Address;
import objects.Coordinates;
import objects.Organization;
import objects.OrganizationType;
import objects.Position;
import objects.Status;
import objects.Worker;

public class Factory {

    private final Reader reader;

    public Factory(Reader reader) {
        this.reader = reader;
    }

    @FunctionalInterface
    private interface ThrowingFunction<T, R> {
        R apply(T t) throws InvalidWorkerArgumentException;
    }

    private String normalize(String input) {

        if (input == null) {
            return null;
        }

        String trimmed = input.trim();

        return trimmed.isEmpty()
                ? null
                : trimmed;
    }

    private <T> T ask(
            String message,
            ThrowingFunction<String, T> validator)
            throws InvalidWorkerArgumentException {

        if (reader.isInteractive()) {
            System.out.print(message);
        }

        String input = normalize(reader.readLine());

        return validator.apply(input);
    }

    private <T> T retryAsk(
            String message,
            ThrowingFunction<String, T> validator)
            throws InvalidWorkerArgumentException {

        while (true) {

            try {
                return ask(message, validator);

            } catch (InvalidWorkerArgumentException e) {

                ExceptionManager.printException(e);

                if (!reader.isInteractive()) {
                    throw e;
                }
            }
        }
    }

    private boolean isNegativeAnswer(String input) {

        if (input == null) {
            return true;
        }

        String normalized = input.trim().toLowerCase();

        return normalized.isEmpty()
                || normalized.equals("no")
                || normalized.equals("n");
    }

    private String askName()
            throws InvalidWorkerArgumentException {

        return retryAsk(
                "Enter worker's name: ",
                ExceptionManager::checkName);
    }

    private Coordinates askCoordinates()
            throws InvalidWorkerArgumentException {

        long x = retryAsk(
                "Enter x coordinate: ",
                ExceptionManager::checkX);

        Float y = retryAsk(
                "Enter y coordinate: ",
                ExceptionManager::checkY);

        return new Coordinates(x, y);
    }

    private Float askSalary()
            throws InvalidWorkerArgumentException {

        return retryAsk(
                "Enter worker's salary: ",
                ExceptionManager::checkSalary);
    }

    private LocalDateTime askStartDate()
            throws InvalidWorkerArgumentException {

        return retryAsk(
                "Enter worker's start time (yyyy-MM-dd HH:mm): ",
                ExceptionManager::checkStartDate);
    }

    private Position askPosition()
            throws InvalidWorkerArgumentException {

        if (reader.isInteractive()) {
            System.out.println(
                    "Available positions(case sensitive!): "
                            + Arrays.toString(Position.values()));
        }

        return retryAsk(
                "Enter position: ",
                ExceptionManager::checkPosition);
    }

    private Status askStatus()
            throws InvalidWorkerArgumentException {

        if (reader.isInteractive()) {
            System.out.println(
                    "Available statuses(case sensitive!): "
                            + Arrays.toString(Status.values()));
        }

        return retryAsk(
                "Enter status: ",
                ExceptionManager::checkStatus);
    }

    private Organization askOrganization()
            throws InvalidWorkerArgumentException {

        if (reader.isInteractive()) {
            System.out.print(
                    "Does worker have an organization? (yes/no, empty = no): ");
        }

        String answer = reader.readLine();

        if (isNegativeAnswer(answer)) {
            return null;
        }

        Integer employeesCount = retryAsk(
                "Enter organization's employees count: ",
                ExceptionManager::checkEmployeesCount);

        if (reader.isInteractive()) {
            System.out.println(
                    "Available organization types: "
                            + Arrays.toString(OrganizationType.values()));
        }

        OrganizationType type = retryAsk(
                "Enter organization's type(case sensitive!): ",
                ExceptionManager::checkOrganizationType);

        Address address = askAddress();

        return new Organization(
                employeesCount,
                type,
                address);
    }

    private Address askAddress()
            throws InvalidWorkerArgumentException {

        if (reader.isInteractive()) {
            System.out.print(
                    "Does organization have postal address? (yes/no, empty = no): ");
        }

        String answer = reader.readLine();

        if (isNegativeAnswer(answer)) {
            return null;
        }

        String zipCode = retryAsk(
                "Enter organization's zip code: ",
                ExceptionManager::checkZipCode);

        return new Address(zipCode);
    }

    public Worker createWorker()
            throws InvalidWorkerArgumentException {

        return buildWorker(
                IdGenerator.nextId(),
                new Date());
    }

    public Worker updateWorker(Worker oldWorker)
            throws InvalidWorkerArgumentException {

        return buildWorker(
                oldWorker.getId(),
                oldWorker.getCreationDate());
    }

    private Worker buildWorker(
            Integer id,
            Date creationDate)
            throws InvalidWorkerArgumentException {

        String name = askName();

        Coordinates coordinates = askCoordinates();

        float salary = askSalary();

        LocalDateTime startDate = askStartDate();

        Position position = askPosition();

        Status status = askStatus();

        Organization organization = askOrganization();

        return new Worker.WorkerBuilder(id)
                .name(name)
                .coordinates(coordinates)
                .creationDate(creationDate)
                .salary(salary)
                .startDate(startDate)
                .position(position)
                .status(status)
                .organization(organization)
                .build();
    }
}