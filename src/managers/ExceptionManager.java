package managers;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;

import commands.Command;
import exceptions.*;
import objects.*;

public class ExceptionManager {

    public enum BoundType {
        INCLUSIVE,
        EXCLUSIVE
    }

    public static String requireNonNull(String input, String field)
            throws InvalidWorkerArgumentException {

        if (input == null) {
            throw new InvalidWorkerArgumentException(field + " cannot be null");
        }

        return input;
    }

    public static Integer parseInteger(String input, String field)
            throws InvalidWorkerArgumentException {

        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new InvalidWorkerArgumentException(field + " must be Integer");
        }
    }

    public static Long parseLong(String input, String field)
            throws InvalidWorkerArgumentException {

        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            throw new InvalidWorkerArgumentException(field + " must be Long");
        }
    }

    public static Float parseFloat(String input, String field)
            throws InvalidWorkerArgumentException {

        try {
            String normalized = input.replace(',', '.');

            int dotIndex = normalized.indexOf('.');
            if (dotIndex != -1 && normalized.length() - dotIndex - 1 > 4) {
                normalized = normalized.substring(0, dotIndex + 5);
            }

            return Float.parseFloat(normalized);

        } catch (NumberFormatException e) {
            throw new InvalidWorkerArgumentException(field + " must be Float");
        }
    }

    public static <E extends Enum<E>> E parseEnum(
            String input,
            Class<E> enumClass,
            String field)
            throws InvalidWorkerArgumentException {

        try {
            return Enum.valueOf(enumClass, input);

        } catch (IllegalArgumentException e) {
            throw new InvalidWorkerArgumentException(
                    field + " must be one of " +
                            Arrays.toString(enumClass.getEnumConstants()));
        }
    }

    public static <T extends Number> T validateRange(
            T num,
            Double min,
            BoundType minType,
            Double max,
            BoundType maxType,
            String field)
            throws InvalidWorkerArgumentException {

        double val = num.doubleValue();

        if (min != null) {
            boolean invalid = (minType == BoundType.INCLUSIVE)
                    ? val < min
                    : val <= min;

            if (invalid) {
                String sign = (minType == BoundType.INCLUSIVE)
                        ? ">="
                        : ">";

                throw new InvalidWorkerArgumentException(
                        field + " must be " + sign + " " + min);
            }
        }

        if (max != null) {
            boolean invalid = (maxType == BoundType.INCLUSIVE)
                    ? val > max
                    : val >= max;

            if (invalid) {
                String sign = (maxType == BoundType.INCLUSIVE)
                        ? "<="
                        : "<";

                throw new InvalidWorkerArgumentException(
                        field + " must be " + sign + " " + max);
            }
        }

        return num;
    }


    public static Integer checkId(String input)
            throws InvalidWorkerArgumentException {

        Integer id = parseInteger(
                requireNonNull(input, "Id"),
                "Id");

        return validateRange(
                id,
                0.0,
                BoundType.EXCLUSIVE,
                null,
                null,
                "Id");
    }

    public static String checkName(String input)
            throws InvalidWorkerArgumentException {

        return requireNonNull(input, "Name");
    }

    public static long checkX(String input)
            throws InvalidWorkerArgumentException {

        long x = parseLong(
                requireNonNull(input, "X"),
                "X");

        return validateRange(
                x,
                null,
                null,
                53.0,
                BoundType.INCLUSIVE,
                "X");
    }

    public static Float checkY(String input)
            throws InvalidWorkerArgumentException {

        Float y = parseFloat(
                requireNonNull(input, "Y"),
                "Y");

        return validateRange(
                y,
                -63.0,
                BoundType.EXCLUSIVE,
                null,
                null,
                "Y");
    }

    public static float checkSalary(String input)
            throws InvalidWorkerArgumentException {

        Float salary = parseFloat(
                requireNonNull(input, "Salary"),
                "Salary");

        return validateRange(
                salary,
                0.0,
                BoundType.EXCLUSIVE,
                null,
                null,
                "Salary");
    }

    public static LocalDateTime checkStartDate(String input)
            throws InvalidWorkerArgumentException {

        requireNonNull(input, "Start date");

        try {
            return LocalDateTime.parse(
                    input,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        } catch (DateTimeParseException e) {
            throw new InvalidWorkerArgumentException(
                    "Start date must be yyyy-MM-dd HH:mm");
        }
    }

    public static Position checkPosition(String input)
            throws InvalidWorkerArgumentException {

        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        return parseEnum(input, Position.class, "Position");
    }

    public static Status checkStatus(String input)
            throws InvalidWorkerArgumentException {

        return parseEnum(
                requireNonNull(input, "Status"),
                Status.class,
                "Status");
    }

    public static Integer checkEmployeesCount(String input)
            throws InvalidWorkerArgumentException {

        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        Integer count = parseInteger(input, "Employees count");

        return validateRange(
                count,
                0.0,
                BoundType.EXCLUSIVE,
                null,
                null,
                "Employees count");
    }

    public static OrganizationType checkOrganizationType(String input)
            throws InvalidWorkerArgumentException {

        return parseEnum(
                requireNonNull(input, "Organization type"),
                OrganizationType.class,
                "Organization type");
    }

    public static String checkZipCode(String input)
            throws InvalidWorkerArgumentException {

        return requireNonNull(input, "Zip code");
    }


    public static Command checkCommand(
            String input,
            HashMap<String, Command> commandMap)
            throws InvalidCommandException {

        if (!commandMap.containsKey(input)) {
            throw new InvalidCommandException(
                    "Command does not exist");
        }

        return commandMap.get(input);
    }

    public static void checkCommandArgs(
            int given,
            int required)
            throws InvalidCommandException {

        if (given != required) {
            throw new InvalidCommandException(
                    "Expected " + required + " arguments");
        }
    }


    public static File checkFile(String filename)
            throws FileNotFoundException, SecurityException {

        File file = new File(filename);

        if (!file.exists()) {
            throw new FileNotFoundException(
                    "File does not exist");
        }

        if (!file.canRead()) {
            throw new SecurityException(
                    "Permission denied");
        }

        if (file.isDirectory()) {
            throw new SecurityException(
                    "Path is a directory");
        }

        return file;
    }

    public static void printException(Throwable exc) {

        Throwable current = exc;

        while (current != null) {
            System.out.println(
                    current.getClass().getSimpleName()
                            + ": "
                            + current.getMessage());

            current = current.getCause();
        }
    }
}