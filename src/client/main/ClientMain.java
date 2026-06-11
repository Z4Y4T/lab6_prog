package client.main;

import common.parsers.Parser;

/**
 * Точка входа клиентского приложения.
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
 * пользователю выводится предупреждение.
 * </p>
 */
public class ClientMain {

    /**
     * Запускает клиентское приложение.
     *
     * @param args аргументы командной строки: [host] [port]
     */
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8080;

        if (args.length > 2) {
            System.err.println("Usage: java -jar ClientMain.jar [host] [port]");
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
            System.err.println(e.getMessage() + ". Using default host and port.");
        }

        ClientApplication app = new ClientApplication(host, port);
        app.boot();
    }
}