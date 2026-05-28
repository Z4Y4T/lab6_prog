package utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exceptions.InvalidWorkerArgumentException;
import exceptions.XMLParsingException;
import managers.ExceptionManager;
import objects.*;

public class FromXMLInterpreter {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Vector<Worker> loadFromFile(String filename)
            throws FileNotFoundException, SecurityException, XMLParsingException {

        Vector<Worker> workers = new Vector<>();
        File file = ExceptionManager.checkFile(filename);

        try (Scanner scanner = new Scanner(file)) {

            scanner.useDelimiter("\\Z");

            if (!scanner.hasNext()) {
                return workers;
            }

            String content = scanner.next();

            List<String> workerBlocks = extractBlocks(content, "worker");

            Set<Integer> seenIds = new HashSet<>();

            for (String block : workerBlocks) {

                Worker worker = parseWorker(block);

                if (!seenIds.add(worker.getId())) {
                    throw new XMLParsingException(
                            "Duplicate worker id: " + worker.getId());
                }

                workers.add(worker);
            }

        } catch (XMLParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new XMLParsingException("Failed to parse XML", e);
        }

        syncIdGenerator(workers);

        return workers;
    }

    private static Worker parseWorker(String block)
            throws InvalidWorkerArgumentException {

        try {

            Integer id = ExceptionManager.checkId(
                    extractRequiredTag(block, "id"));

            String name = ExceptionManager.checkName(
                    extractRequiredTag(block, "name"));

            String coordinatesBlock = extractRequiredBlock(block, "coordinates");

            long x = ExceptionManager.checkX(
                    extractRequiredTag(coordinatesBlock, "x"));

            Float y = ExceptionManager.checkY(
                    extractRequiredTag(coordinatesBlock, "y"));

            Coordinates coordinates = new Coordinates(x, y);

            java.util.Date creationDate = DATE_FORMAT.parse(
                    extractRequiredTag(block, "creationDate"));

            float salary = ExceptionManager.checkSalary(
                    extractRequiredTag(block, "salary"));

            LocalDateTime startDate = ExceptionManager.checkStartDate(
                    extractRequiredTag(block, "startDate"));

            Position position = ExceptionManager.checkPosition(
                    extractOptionalTag(block, "position"));

            Status status = ExceptionManager.checkStatus(
                    extractRequiredTag(block, "status"));

            Organization organization = parseOrganization(block);

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

        } catch (Exception e) {
            throw new InvalidWorkerArgumentException(
                    "Error parsing worker: " + e.getMessage(), e);
        }
    }

    private static Organization parseOrganization(String block)
            throws InvalidWorkerArgumentException {

        try {

            String orgBlock = extractOptionalBlock(block, "organization");

            if (orgBlock == null || orgBlock.isEmpty()) {
                return null;
            }

            Integer employeesCount = ExceptionManager.checkEmployeesCount(
                    extractOptionalTag(orgBlock, "employeesCount"));

            String typeString = extractOptionalTag(orgBlock, "type");

            if (typeString == null || typeString.isEmpty()) {
                return null;
            }

            OrganizationType type = ExceptionManager.checkOrganizationType(typeString);

            Address address = null;

            String addressBlock = extractOptionalBlock(orgBlock, "postalAddress");

            if (addressBlock != null) {

                String zipCode = ExceptionManager.checkZipCode(
                        extractRequiredTag(addressBlock, "zipCode"));

                address = new Address(zipCode);
            }

            return new Organization(
                    employeesCount,
                    type,
                    address);

        } catch (Exception e) {
            throw new InvalidWorkerArgumentException(
                    "Error parsing organization: " + e.getMessage(), e);
        }
    }

    private static List<String> extractBlocks(
            String xml,
            String tagName) {

        List<String> result = new ArrayList<>();

        Pattern pattern = Pattern.compile(
                "<" + tagName + ">(.*?)</" + tagName + ">",
                Pattern.DOTALL);

        Matcher matcher = pattern.matcher(xml);

        while (matcher.find()) {
            result.add(matcher.group(1).trim());
        }

        return result;
    }

    private static String extractRequiredTag(
            String xml,
            String tagName)
            throws XMLParsingException {

        Pattern pattern = Pattern.compile(
                "<" + tagName + ">(.*?)</" + tagName + ">",
                Pattern.DOTALL);

        Matcher matcher = pattern.matcher(xml);

        if (!matcher.find()) {
            throw new XMLParsingException(
                    "Missing required tag: " + tagName);
        }

        return unescapeXml(matcher.group(1).trim());
    }

    private static String extractOptionalTag(
            String xml,
            String tagName) {

        Pattern pattern = Pattern.compile(
                "<" + tagName + ">(.*?)</" + tagName + ">",
                Pattern.DOTALL);

        Matcher matcher = pattern.matcher(xml);

        if (!matcher.find()) {
            return null;
        }

        String value = matcher.group(1).trim();

        if (value.isEmpty()) {
            return null;
        }

        return unescapeXml(value);
    }

    private static String extractRequiredBlock(
            String xml,
            String tagName)
            throws XMLParsingException {

        Pattern pattern = Pattern.compile(
                "<" + tagName + ">(.*?)</" + tagName + ">",
                Pattern.DOTALL);

        Matcher matcher = pattern.matcher(xml);

        if (!matcher.find()) {
            throw new XMLParsingException(
                    "Missing required block: " + tagName);
        }

        return matcher.group(1).trim();
    }

    private static String extractOptionalBlock(
            String xml,
            String tagName) {

        Pattern pattern = Pattern.compile(
                "<" + tagName + ">(.*?)</" + tagName + ">",
                Pattern.DOTALL);

        Matcher matcher = pattern.matcher(xml);

        if (!matcher.find()) {
            return null;
        }

        return matcher.group(1).trim();
    }

    private static String unescapeXml(String text) {

        if (text == null) {
            return null;
        }

        return text.replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&apos;", "'")
                .replace("&amp;", "&");
    }

    private static void syncIdGenerator(Vector<Worker> workers) {

        int maxId = workers.stream()
                .mapToInt(Worker::getId)
                .max()
                .orElse(0);

        IdGenerator.setCurrentId(maxId + 1);
    }
}