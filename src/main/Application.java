package main;

import java.util.Arrays;
import java.util.Vector;
import managers.*;
import utilities.*;
import objects.Worker;
import commands.*;

public class Application {
    private final CommandManager commandManager;
    private final ScriptManager scriptManager;
    private final CollectionManager collectionManager;
    private final FactoryManager fm;

    public Application() {
        commandManager = new CommandManager();
        Reader consoleReader = new Reader();
        Factory currentFactory = new Factory(consoleReader);
        fm = new FactoryManager(currentFactory);
        collectionManager = new CollectionManager();
        scriptManager = new ScriptManager(consoleReader);
        initCommands();
    }

    private void initCommands() {
        commandManager.register("help", new Help(commandManager.getCommandMap()));
        commandManager.register("info", new Info(collectionManager));
        commandManager.register("show", new Show(collectionManager));
        commandManager.register("add", new Add(collectionManager, fm));
        commandManager.register("remove_greater", new RemoveGreater(collectionManager, fm));
        commandManager.register("add_if_max", new AddIfMax(collectionManager, fm));
        commandManager.register("update", new Update(collectionManager, fm));
        commandManager.register("insert_at", new Insert(collectionManager, fm));
        commandManager.register("remove_by_id", new Remove(collectionManager));
        commandManager.register("clear", new Clear(collectionManager));
        commandManager.register("max_by_salary", new MaxBySalary(collectionManager));
        commandManager.register("count_greater_than_start_date", new CountGreaterThanStartDate(collectionManager));
        commandManager.register("print_unique_start_date", new PrintUniqueStartDate(collectionManager));
        commandManager.register("exit", new Exit());
        commandManager.register("execute_script", new Execute(this));
        commandManager.register("save", new Save(collectionManager));
    }

    public ScriptManager getScriptManager() {
        return scriptManager;
    }

    public CollectionManager getCollectionManager() {
        return collectionManager;
    }

    public void boot() {
        String filename = System.getenv("WORKER_FILE");
        System.out.println("Welcome to JAVA lab5, my king");

        if (filename != null && !filename.trim().isEmpty()) {
            System.out.println("Loading data from " + filename);
            try {
                Vector<Worker> workers = FromXMLInterpreter.loadFromFile(filename);
                collectionManager.setWorkers(workers);
                System.out.println("Loaded " + workers.size() + " elements");
            } catch (Exception e) {
                ExceptionManager.printException(e);
            }
        }
        run();
    }

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
                    setFactory(new Factory(scriptManager.getCurrentReader()));
                    continue;
                }
                break;
            }

            line = line.trim();
            if (line.isEmpty())
                continue;

            String[] splitted = line.split("\\s+");
            String commandStr = splitted[0];
            String[] arguments = Arrays.copyOfRange(splitted, 1, splitted.length);

            try {
                Command command = ExceptionManager.checkCommand(commandStr, commandManager.getCommandMap());
                ExceptionManager.checkCommandArgs(arguments.length, command.getNumberOfArgs());
                command.execute(arguments);
            } catch (Exception e) {
                ExceptionManager.printException(e);
                if (!reader.isInteractive()) {
                    scriptManager.popScript();
                }
            }
        }
    }

    public void setFactory(Factory factory) {
        fm.setFactory(factory);
    }
}