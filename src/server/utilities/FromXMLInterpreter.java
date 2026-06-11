package server.utilities;

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

import common.domain.Address;
import common.domain.Coordinates;
import common.domain.Organization;
import common.domain.OrganizationType;
import common.domain.Position;
import common.domain.Status;
import common.domain.Worker;
import common.exceptions.InvalidWorkerArgumentException;
import common.exceptions.XMLParsingException;
import common.parsers.WorkerParser;

/**
 * Парсер XML-файла для загрузки коллекции.
 * 
 * <p>
 * Читает XML-файл, извлекает блоки {@code <worker>} и парсит их
 * в объекты {@link Worker}. Использует регулярные выражения для разбора тегов.
 * </p>
 * 
 * <p>
 * Проверяет уникальность id — дубликаты вызывают {@link XMLParsingException}.
 * После загрузки синхронизирует {@link IdGenerator} с максимальным id в
 * коллекции.
 * </p>
 * 
 * <p>
 * В третьей лабораторной работе будет заменён на загрузку из базы данных.
 * </p>
 */
public class FromXMLInterpreter {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Загружает коллекцию Worker'ов из XML-файла.
     *
     * @param filename путь к XML-файлу
     * @return список Worker'ов
     * @throws FileNotFoundException если файл не найден
     * @throws SecurityException     если нет прав на чтение
     * @throws XMLParsingException   если XML повреждён или содержит дубликаты id
     */
    public static Vector<Worker> loadFromFile(String filename)
            throws FileNotFoundException, SecurityException, XMLParsingException {

        Vector<Worker> workers = new Vector<>();
        File file = new File(filename);

        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException(filename);
        }

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
                    throw new XMLParsingException("Duplicate worker id: " + worker.getId());
                }
                workers.add(worker);
            }
        } catch (InvalidWorkerArgumentException e) {
            throw new XMLParsingException("Failed to parse XML: " + e.getMessage(), e);
        }

        syncIdGenerator(workers);
        return workers;
    }

    /**
     * Парсит один блок {@code <worker>} в объект Worker.
     */
    private static Worker parseWorker(String block) throws InvalidWorkerArgumentException {
        try {
            Integer id = Integer.valueOf(extractRequiredTag(block, "id"));
            String name = WorkerParser.checkName(extractRequiredTag(block, "name"));
            String coordinatesBlock = extractRequiredBlock(block, "coordinates");
            long x = WorkerParser.checkX(extractRequiredTag(coordinatesBlock, "x"));
            Float y = WorkerParser.checkY(extractRequiredTag(coordinatesBlock, "y"));
            Coordinates coordinates = new Coordinates(x, y);
            java.util.Date creationDate = DATE_FORMAT.parse(extractRequiredTag(block, "creationDate"));
            float salary = WorkerParser.checkSalary(extractRequiredTag(block, "salary"));
            LocalDateTime startDate = WorkerParser.checkStartDate(extractRequiredTag(block, "startDate"));
            Position position = WorkerParser.checkPosition(extractOptionalTag(block, "position"));
            Status status = WorkerParser.checkStatus(extractRequiredTag(block, "status"));
            Organization organization = parseOrganization(block);

            return new Worker.WorkerBuilder()
                    .id(id)
                    .name(name)
                    .coordinates(coordinates)
                    .creationDate(creationDate)
                    .salary(salary)
                    .startDate(startDate)
                    .position(position)
                    .status(status)
                    .organization(organization)
                    .build();
        } catch (InvalidWorkerArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidWorkerArgumentException("Error parsing worker: " + e.getMessage(), e);
        }
    }

    /**
     * Парсит вложенный блок {@code <organization>}.
     */
    private static Organization parseOrganization(String block) throws InvalidWorkerArgumentException {
        try {
            String orgBlock = extractOptionalBlock(block, "organization");
            if (orgBlock == null || orgBlock.isEmpty()) {
                return null;
            }

            Integer employeesCount = WorkerParser.checkEmployeesCount(extractOptionalTag(orgBlock, "employeesCount"));
            String typeString = extractOptionalTag(orgBlock, "type");

            if (typeString == null || typeString.isEmpty()) {
                return null;
            }

            OrganizationType type = WorkerParser.checkOrganizationType(typeString);
            Address address = null;
            String addressBlock = extractOptionalBlock(orgBlock, "postalAddress");

            if (addressBlock != null) {
                String zipCode = WorkerParser.checkZipCode(extractRequiredTag(addressBlock, "zipCode"));
                address = new Address(zipCode);
            }

            return new Organization(employeesCount, type, address);
        } catch (InvalidWorkerArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidWorkerArgumentException("Error parsing organization: " + e.getMessage(), e);
        }
    }

    /**
     * Извлекает все блоки с указанным тегом из XML-строки.
     */
    private static List<String> extractBlocks(String xml, String tagName) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("<" + tagName + ">(.*?)</" + tagName + ">", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(xml);

        while (matcher.find()) {
            result.add(matcher.group(1).trim());
        }

        return result;
    }

    /**
     * Извлекает значение обязательного тега. Если тег отсутствует — ошибка.
     */
    private static String extractRequiredTag(String xml, String tagName) throws XMLParsingException {
        Pattern pattern = Pattern.compile("<" + tagName + ">(.*?)</" + tagName + ">", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(xml);

        if (!matcher.find()) {
            throw new XMLParsingException("Missing required tag: " + tagName);
        }

        return unescapeXml(matcher.group(1).trim());
    }

    /**
     * Извлекает значение опционального тега. Если отсутствует — null.
     */
    private static String extractOptionalTag(String xml, String tagName) {
        Pattern pattern = Pattern.compile("<" + tagName + ">(.*?)</" + tagName + ">", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(xml);

        if (!matcher.find()) {
            return null;
        }

        String value = matcher.group(1).trim();
        return value.isEmpty() ? null : unescapeXml(value);
    }

    /**
     * Извлекает содержимое обязательного блока (без внешнего тега).
     */
    private static String extractRequiredBlock(String xml, String tagName) throws XMLParsingException {
        Pattern pattern = Pattern.compile("<" + tagName + ">(.*?)</" + tagName + ">", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(xml);

        if (!matcher.find()) {
            throw new XMLParsingException("Missing required block: " + tagName);
        }

        return matcher.group(1).trim();
    }

    /**
     * Извлекает содержимое опционального блока.
     */
    private static String extractOptionalBlock(String xml, String tagName) {
        Pattern pattern = Pattern.compile("<" + tagName + ">(.*?)</" + tagName + ">", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(xml);

        return matcher.find() ? matcher.group(1).trim() : null;
    }

    /**
     * Заменяет XML-сущности на соответствующие символы.
     */
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

    /**
     * Синхронизирует {@link IdGenerator} с максимальным id в коллекции.
     */
    private static void syncIdGenerator(Vector<Worker> workers) {
        int maxId = workers.stream()
                .mapToInt(Worker::getId)
                .max()
                .orElse(0);

        IdGenerator.setCurrentId(maxId + 1);
    }
}