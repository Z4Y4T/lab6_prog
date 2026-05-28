package commands;

import exceptions.InvalidCommandArgumentsException;
import exceptions.InvalidWorkerArgumentException;
import managers.CollectionManager;
import managers.ExceptionManager;

public class Remove implements Command {
    private CollectionManager collectionManager;
    private final int numberOfArgs = 1;

    public Remove(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public void execute(String... args) throws InvalidCommandArgumentsException, InvalidWorkerArgumentException {
        Integer id = ExceptionManager.checkId(args[0]);
        collectionManager.remove(id);
    }

    public String describe() {
        return "remove_by_id id - removes element (refers to id)";
    }

    public int getNumberOfArgs() {
        return numberOfArgs;
    }
}
