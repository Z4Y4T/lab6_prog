package commands;

import exceptions.InvalidWorkerArgumentException;
import managers.CollectionManager;
import managers.FactoryManager;
import objects.Worker;

public class AddIfMax implements Command {
    private final int numberOfArgs = 0;
    private CollectionManager collectionManager;
    private FactoryManager factoryManager;

    public AddIfMax(CollectionManager collectionManager, FactoryManager factoryManager) {
        this.collectionManager = collectionManager;
        this.factoryManager = factoryManager;
    }

    public void execute(String... args) throws InvalidWorkerArgumentException {
        Worker worker = factoryManager.getFactory().createWorker();
        collectionManager.addIfMax(worker);
    }

    public String describe() {
        return "add_if_max - adds new element in the collection if it's greater than the greatest element in the collection";
    }

    public int getNumberOfArgs() {
        return numberOfArgs;
    }
}
