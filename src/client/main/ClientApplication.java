package client.main;

import java.io.IOException;
import java.util.Arrays;

import client.commands.*;
import common.domain.Worker;
import common.exceptions.InvalidCommandException;
import common.net.Response;
import client.managers.ClientCommandManager;
import client.managers.ScriptManager;
import client.utilities.WorkerFactory;
import client.utilities.Reader;

/**
 * Основной класс клиентского приложения.
 * 
 * <p>
 * Связывает воедино все компоненты клиента:
 * </p>
 * <ul>
 * <li>{@link Reader} — источник ввода (консоль или файл скрипта)</li>
 * <li>{@link WorkerFactory} — создание объектов {@link Worker} через опрос
 * пользователя</li>
 * <li>{@link ScriptManager} — управление стеком источников ввода при выполнении
 * скриптов</li>
 * <li>{@link ClientCommandManager} — регистрация и поиск команд по имени</li>
 * <li>{@link Client} — сетевое взаимодействие с сервером</li>
 * </ul>
 * 
 * <p>
 * Жизненный цикл приложения:
 * </p>
 * <ol>
 * <li>Создание всех менеджеров и подключение к серверу</li>
 * <li>Регистрация команд в {@link #initCommands()}</li>
 * <li>Запуск главного цикла {@link #run()} — чтение и выполнение команд</li>
 * <li>Закрытие соединения при выходе</li>
 * </ol>
 */
public class ClientApplication {
    private final ScriptManager scriptManager;
    private final WorkerFactory factory;
    private final Client client;
    private final ClientCommandManager commandManager;

    /**
     * Создаёт клиентское приложение и подключается к серверу.
     * Если подключиться не удалось — завершает работу с кодом 1.
     *
     * @param host адрес сервера
     * @param port порт сервера
     */
    public ClientApplication(String host, int port) {
        Reader consoleReader = new Reader();
        this.factory = new WorkerFactory();
        this.scriptManager = new ScriptManager(consoleReader);
        this.commandManager = new ClientCommandManager();

        Client tmpClient = null;
        try {
            tmpClient = new Client(host, port);
        } catch (IOException e) {
            System.err.println("Failed to start client: " + e.getMessage());
            System.exit(1);
        }
        this.client = tmpClient;
    }

    /**
     * Регистрирует все доступные команды в {@link ClientCommandManager}.
     * Каждая команда связывается со своим строковым именем.
     */
    private void initCommands() {
        commandManager.register("help", new HelpClientCommand(commandManager.getCommandMap()));
        commandManager.register("info", new InfoClientCommand(client));
        commandManager.register("show", new ShowClientCommand(client));
        commandManager.register("add", new AddClientCommand(factory, scriptManager, client));
        commandManager.register("remove_greater", new RemoveGreaterClientCommand(factory, scriptManager, client));
        commandManager.register("add_if_max", new AddIfMaxClientCommand(factory, scriptManager, client));
        commandManager.register("update", new UpdateClientCommand(factory, scriptManager, client));
        commandManager.register("insert_at", new InsertAtClientCommand(factory, scriptManager, client));
        commandManager.register("remove_by_id", new RemoveByIdClientCommand(client));
        commandManager.register("clear", new ClearClientCommand(client));
        commandManager.register("max_by_salary", new MaxBySalaryClientCommand(client));
        commandManager.register("count_greater_than_start_date",
                new CountGreaterThanStartDateClientCommand(client));
        commandManager.register("print_unique_start_date", new PrintUniqueStartDateClientCommand(client));
        commandManager.register("exit", new ExitClientCommand(client));
        commandManager.register("execute_script", new ExecuteScriptClientCommand(scriptManager));
    }

    /**
     * Возвращает менеджер скриптов.
     *
     * @return {@link ScriptManager}, управляющий стеком источников ввода
     */
    public ScriptManager getScriptManager() {
        return scriptManager;
    }

    /**
     * Запускает приложение: регистрирует команды и входит в главный цикл.
     * При любом выходе из цикла закрывает соединение с сервером.
     */
    public void boot() {
        System.out.println("Welcome to JAVA lab6, my king");
        initCommands();
        try {
            run();
        } finally {
            closeClient();
        }
    }

    /**
     * Закрывает сетевое соединение с сервером.
     * Вызывается в блоке {@code finally} после выхода из главного цикла.
     */
    private void closeClient() {
        try {
            client.close();
        } catch (IOException e) {
            System.err.println("Failed to close client: " + e.getMessage());
        }
    }

    /**
     * Главный цикл приложения: читает команды из текущего источника ввода
     * и выполняет их.
     * 
     * <p>
     * Логика работы:
     * </p>
     * <ol>
     * <li>Получает текущий {@link Reader} из {@link ScriptManager}</li>
     * <li>Выводит приглашение, если режим интерактивный</li>
     * <li>Читает строку; если конец ввода в скрипте — снимает верхний Reader со
     * стека</li>
     * <li>Разбивает строку на имя команды и аргументы</li>
     * <li>Находит команду в {@link ClientCommandManager} и выполняет её</li>
     * <li>Выводит результат: сообщение и/или массив Worker'ов</li>
     * </ol>
     */
    private void run() {
        while (true) {
            Reader reader = scriptManager.getCurrentReader();

            if (reader.isInteractive()) {
                System.out.print("Enter a command > ");
            }

            String line = reader.readLine();

            if (line == null) {
                if (!reader.isInteractive()) {
                    scriptManager.popScript();
                    continue;
                }
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
                ClientCommand command = commandManager.getCommand(commandStr);
                Response response = command.execute(argumentsStr);

                if (response == null) {
                    System.err.println("No response from command.");
                    continue;
                }

                if (response.isConnectionError()) {
                    System.err.println(response.getMessage());
                    continue;
                }

                if (response.isSuccessful()) {
                    Worker[] workers = response.getWorkers();
                    if (workers != null && workers.length > 0) {
                        for (Worker worker : workers) {
                            System.out.println(worker);
                        }
                    }
                    System.out.println(response.getMessage());
                } else {
                    System.err.println(response.getMessage());
                }
            } catch (InvalidCommandException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}