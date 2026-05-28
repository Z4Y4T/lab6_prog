package commands;

import exceptions.InvalidCommandArgumentsException;
import exceptions.InvalidWorkerArgumentException;
import managers.*;
import objects.Worker;

public class Update implements Command {
    private CollectionManager collectionManager;
    private FactoryManager factoryManager;
    private final int numberOfArgs = 1;

    public Update(CollectionManager collectionManager, FactoryManager factoryManager) {
        this.collectionManager = collectionManager;
        this.factoryManager = factoryManager;
    }

    public void execute(String... args) throws InvalidCommandArgumentsException, InvalidWorkerArgumentException {
        Integer id = ExceptionManager.checkId(args[0]);
        for (Worker w : collectionManager.getWorkerList()) {
            if (w.getId().equals(id)) {
                Worker updatedWorker = factoryManager.getFactory().updateWorker(w);
                collectionManager.update(id, updatedWorker);
                return;
            }
        }
        throw new InvalidCommandArgumentsException("Worker with id " + id + " not found");
    }

    public String describe() {
        return "update id {element} - updates element's properties(refers to id)";
    }

    public int getNumberOfArgs() {
        return numberOfArgs;
    }
}
