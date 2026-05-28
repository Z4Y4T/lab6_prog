package commands;

import exceptions.InvalidWorkerArgumentException;
import managers.CollectionManager;
import managers.FactoryManager;
import objects.Worker;

public class Add implements Command {
    private final int numberOfArgs = 0;
    private CollectionManager collectionManager;
    private FactoryManager factoryManager;

    public Add(CollectionManager collectionManager, FactoryManager factoryManager) {
        this.collectionManager = collectionManager;
        this.factoryManager = factoryManager;
    }

    public void execute(String... args) throws InvalidWorkerArgumentException {
        Worker newWorker = factoryManager.getFactory().createWorker();
        collectionManager.add(newWorker);
    }

    public String describe() {
        return "add - adds new element in the collection";
    }

    public int getNumberOfArgs() {
        return numberOfArgs;
    }
}
