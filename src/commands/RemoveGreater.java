package commands;

import exceptions.InvalidWorkerArgumentException;
import managers.CollectionManager;
import managers.FactoryManager;
import objects.Worker;

public class RemoveGreater implements Command {
    private final int numberOfArgs = 0;
    private CollectionManager collectionManager;
    private FactoryManager factoryManager;

    public RemoveGreater(CollectionManager collectionManager, FactoryManager factoryManager) {
        this.collectionManager = collectionManager;
        this.factoryManager = factoryManager;
    }

    public void execute(String... args) throws InvalidWorkerArgumentException {
        Worker worker = factoryManager.getFactory().createWorker();
        collectionManager.removeGreater(worker);
    }

    public String describe() {
        return "remove_greater {element} - removes all elements greater than given";
    }

    public int getNumberOfArgs() {
        return numberOfArgs;
    }
}
