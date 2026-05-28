package commands;

import exceptions.FileAccessException;
import managers.CollectionManager;
import managers.ExceptionManager;
import utilities.ToXMLInterpreter;

public class Save implements Command {
    private final CollectionManager collectionManager;
    private final int numberOfArgs = 0;

    public Save(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public void execute(String... args) {
        String filename = System.getenv("WORKER_FILE");
        if (filename == null || filename.trim().isEmpty()) {
            System.out.println("Error: WORKER_FILE environment variable not set");
            return;
        }

        try {
            ToXMLInterpreter.saveToFile(filename, collectionManager.getWorkerList());
            System.out.println("Collection saved to " + filename);
        } catch (Exception e) {
            ExceptionManager.printException(new FileAccessException("Error saving collection", e));
        }
    }

    public String describe() {
        return "save - saves collection to XML file";
    }

    public int getNumberOfArgs() {
        return numberOfArgs;
    }
}