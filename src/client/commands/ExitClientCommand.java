package client.commands;

import java.io.IOException;

import common.exceptions.InvalidCommandArgumentException;
import common.net.Response;
import common.parsers.CommandArgumentParser;
import client.main.Client;

/**
 * Клиентская команда {@code exit}.
 * Завершает работу клиентского приложения.
 * 
 * <p>
 * Не принимает аргументов. Закрывает сетевое соединение с сервером
 * через {@link Client#close()} и завершает JVM вызовом {@code System.exit(0)}.
 * </p>
 * 
 * <p>
 * Важно: коллекция на сервере не сохраняется. Для сохранения необходимо
 * использовать штатное завершение сервера (Ctrl+C), которое вызывает shutdown
 * hook.
 * </p>
 * 
 * <p>
 * Запрос на сервер не отправляется.
 * </p>
 */
public class ExitClientCommand implements ClientCommand {
    private final Client client;

    public ExitClientCommand(Client client) {
        this.client = client;
    }

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public String getDescription() {
        return "shuts down the program (without saving)";
    }

    /**
     * Выполняет команду: проверяет отсутствие аргументов, закрывает соединение
     * с сервером и завершает программу.
     * 
     * @param args должен быть пустым
     * @return {@code null} — метод не возвращает управление,
     *         так как вызывает {@code System.exit(0)}
     */
    @Override
    public Response execute(String... args) {
        try {
            CommandArgumentParser.checkArgumentCount(args, 0);
            client.close();
        } catch (IOException | InvalidCommandArgumentException e) {
            return new Response(false, "Failed to close client: " + e.getMessage(), null);
        }
        System.exit(0);
        return null;
    }
}