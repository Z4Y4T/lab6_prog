package commands;

import exceptions.InvalidCommandArgumentsException;
import exceptions.InvalidWorkerArgumentException;
import managers.CollectionManager;
import managers.ExceptionManager;
import managers.ExceptionManager.BoundType;
import managers.FactoryManager;
import objects.Worker;

public class Insert implements Command {
    private CollectionManager collectionManager;
    private final int numberOfArgs = 1;
    private FactoryManager factoryManager;

    public Insert(CollectionManager collectionManager, FactoryManager factoryManager) {
        this.collectionManager = collectionManager;
        this.factoryManager = factoryManager;
    }

    public void execute(String... args) throws InvalidCommandArgumentsException, InvalidWorkerArgumentException {
        Integer index = ExceptionManager.parseInteger(args[0], "index");
        index = ExceptionManager.validateRange(index, 0.0, BoundType.INCLUSIVE,
                (double) collectionManager.getWorkerList().size(), BoundType.INCLUSIVE, "index");
        Worker worker = factoryManager.getFactory().createWorker();
        collectionManager.insert(index, worker);
    }

    public String describe() {
        return "insert_at index {element} - inserts new element in given index";
    }

    public int getNumberOfArgs() {
        return numberOfArgs;
    }
}
