package utilities;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Vector;

import exceptions.FileAccessException;
import objects.Address;
import objects.Organization;
import objects.Worker;

public class ToXMLInterpreter {

    private static final String INDENT = "  ";

    public static void saveToFile(String filename, Vector<Worker> workers)
            throws FileAccessException {

        try (PrintWriter writer = new PrintWriter(
                new BufferedOutputStream(
                        new FileOutputStream(filename)))) {

            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.println("<workers>");

            for (Worker worker : workers) {
                writeWorker(writer, worker);
            }

            writer.println("</workers>");
            writer.flush();

        } catch (IOException e) {
            throw new FileAccessException(
                    "Error writing to file: " + filename,
                    e);
        }
    }

    private static void writeWorker(PrintWriter writer, Worker w) {

        openTag(writer, 1, "worker");

        writeSimpleTag(writer, 2, "id",
                String.valueOf(w.getId()));

        writeSimpleTag(writer, 2, "name",
                escapeXml(w.getName()));

        writeCoordinates(writer, w);

        writeSimpleTag(writer, 2, "creationDate",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(w.getCreationDate()));

        writeSimpleTag(writer, 2, "salary",
                String.format(Locale.US, "%.4f", w.getSalary()));

        writeSimpleTag(writer, 2, "startDate",
                w.getStartDate().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        writeSimpleTag(writer, 2, "position",
                w.getPosition() != null
                        ? w.getPosition().name()
                        : "");

        writeSimpleTag(writer, 2, "status",
                w.getStatus().name());

        writeOrganization(writer, w.getOrganization());

        closeTag(writer, 1, "worker");
    }

    private static void writeCoordinates(PrintWriter writer, Worker w) {

        openTag(writer, 2, "coordinates");

        writeSimpleTag(writer, 3, "x",
                String.valueOf(w.getCoordinates().getX()));

        writeSimpleTag(writer, 3, "y",
                String.format(Locale.US, "%.4f",
                        w.getCoordinates().getY()));

        closeTag(writer, 2, "coordinates");
    }

    private static void writeOrganization(PrintWriter writer,
            Organization org) {

        if (org == null) {
            writer.println(indent(2) + "<organization/>");
            return;
        }

        openTag(writer, 2, "organization");

        writeSimpleTag(writer, 3, "employeesCount",
                org.getEmployeesCount() != null
                        ? String.valueOf(org.getEmployeesCount())
                        : "");

        writeSimpleTag(writer, 3, "type",
                org.getType() != null
                        ? org.getType().name()
                        : "");

        writeAddress(writer, org.getPostalAddress());

        closeTag(writer, 2, "organization");
    }

    private static void writeAddress(PrintWriter writer,
            Address address) {

        if (address == null) {
            writer.println(indent(3) + "<postalAddress/>");
            return;
        }

        openTag(writer, 3, "postalAddress");

        writeSimpleTag(writer, 4, "zipCode",
                escapeXml(address.getZipCode()));

        closeTag(writer, 3, "postalAddress");
    }

    private static void writeSimpleTag(PrintWriter writer,
            int indentLevel,
            String tag,
            String value) {

        writer.println(
                indent(indentLevel)
                        + "<" + tag + ">"
                        + value
                        + "</" + tag + ">");
    }

    private static void openTag(PrintWriter writer,
            int indentLevel,
            String tag) {

        writer.println(
                indent(indentLevel)
                        + "<" + tag + ">");
    }

    private static void closeTag(PrintWriter writer,
            int indentLevel,
            String tag) {

        writer.println(
                indent(indentLevel)
                        + "</" + tag + ">");
    }

    private static String indent(int level) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < level; i++) {
            sb.append(INDENT);
        }

        return sb.toString();
    }

    private static String escapeXml(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}