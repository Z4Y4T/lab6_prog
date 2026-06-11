package server.main;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import common.parsers.Parser;

/**
 * Точка входа серверного приложения.
 * 
 * <p>
 * Разбирает аргументы командной строки:
 * </p>
 * <ul>
 * <li>{@code args[0]} — хост (по умолчанию {@code localhost})</li>
 * <li>{@code args[1]} — порт (по умолчанию {@code 8080})</li>
 * </ul>
 * 
 * <p>
 * Допустимое количество аргументов — от 0 до 2.
 * При некорректных аргументах используется значение по умолчанию,
 * выводится предупреждение.
 * </p>
 * 
 * <p>
 * Уровень логирования задаётся системным свойством
 * {@code java.util.logging.level} (по умолчанию {@code INFO}).
 * Для отладки: {@code java -Djava.util.logging.level=FINE -jar ServerMain.jar}.
 * </p>
 */
public class ServerMain {
    private static final Logger logger = Logger.getLogger(ServerMain.class.getName());

    /**
     * Запускает серверное приложение.
     *
     * @param args аргументы командной строки: [host] [port]
     */
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8080;

        configureLogger();

        if (args.length > 2) {
            System.err.println("Usage: java -jar ServerMain.jar [host] [port]");
            System.exit(1);
        }

        try {
            if (args.length >= 1) {
                host = Parser.parseHost(args[0], "Host");
            }
            if (args.length >= 2) {
                port = Parser.parsePort(args[1], "Port");
            }
        } catch (IllegalArgumentException e) {
            logger.warning(e.getMessage() + ". Using default host=" + host + " and port=" + port);
        }

        try {
            Server server = new Server(port, host);
            server.run();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to start server", e);
        }
    }

    /**
     * Настраивает глобальный логер.
     * 
     * <p>
     * Уровень логирования берётся из системного свойства
     * {@code java.util.logging.level}. Если свойство не задано —
     * используется {@code INFO}. Для отладки установите {@code FINE} или
     * {@code ALL}.
     * </p>
     * 
     * <p>
     * Все стандартные обработчики удаляются, добавляется один
     * {@link ConsoleHandler} с указанным уровнем.
     * </p>
     */
    private static void configureLogger() {
        String levelStr = System.getProperty("java.util.logging.level", "INFO");
        Level level;
        try {
            level = Level.parse(levelStr);
        } catch (IllegalArgumentException e) {
            level = Level.INFO;
        }

        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(level);

        Logger root = Logger.getLogger("");
        root.setLevel(level);
        for (Handler h : root.getHandlers()) {
            root.removeHandler(h);
        }
        root.addHandler(handler);
    }
}