package server.main;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import common.exceptions.InvalidCommandArgumentException;
import common.exceptions.InvalidCommandException;
import server.commands.executable.ExecutableHelpServerCommand;
import server.commands.executable.ExecutableSaveServerCommand;
import server.commands.executable.ExecutableExitServerCommand;
import server.commands.executable.ExecutableServerCommand;
import server.managers.ExecutableCommandManager;

/**
 * Консольный интерфейс сервера для административных команд.
 * 
 * <p>
 * Работает в отдельном потоке, не блокируя главный цикл обработки UDP-запросов.
 * Позволяет администратору выполнять команды {@code save}, {@code exit},
 * {@code help}
 * напрямую с консоли сервера.
 * </p>
 * 
 * <p>
 * Устроена по тому же принципу, что и {@link client.main.ClientApplication}:
 * </p>
 * <ul>
 * <li>Регистрирует команды в {@link ExecutableCommandManager}</li>
 * <li>Запускает цикл чтения строк из {@link Scanner}</li>
 * <li>Разбирает строку на имя команды и аргументы</li>
 * <li>Выполняет команду и выводит результат</li>
 * </ul>
 */
public class ServerConsole implements Runnable {
    private final ExecutableCommandManager commandManager;
    private final Scanner scanner;

    /**
     * Создаёт консольный интерфейс сервера.
     *
     * @param application серверное приложение (для доступа к коллекции)
     * @param running     флаг работы сервера (для команды exit)
     */
    public ServerConsole(ServerApplication application, AtomicBoolean running) {
        this.commandManager = new ExecutableCommandManager();
        this.scanner = new Scanner(System.in);
        initCommands(application, running);
    }

    /**
     * Регистрирует все доступные консольные команды.
     */
    private void initCommands(ServerApplication application, AtomicBoolean running) {
        commandManager.register("help", new ExecutableHelpServerCommand(commandManager.getCommandMap()));
        commandManager.register("save", new ExecutableSaveServerCommand(application));
        commandManager.register("exit", new ExecutableExitServerCommand(application, running));
    }

    /**
     * Главный цикл консольного ввода.
     * Читает команды из {@link System#in}, пока сервер запущен.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            String line;
            try {
                System.out.print(" > ");
                line = scanner.nextLine();
            } catch (Exception e) {
                break;
            }

            if (line == null) {
                break;
            }

            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] splitted = line.split("\\s+");
            String commandStr = splitted[0];
            String[] argumentsStr = Arrays.copyOfRange(splitted, 1, splitted.length);

            try {
                ExecutableServerCommand command = commandManager.getCommand(commandStr);
                command.execute(argumentsStr);
            } catch (InvalidCommandArgumentException | InvalidCommandException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}