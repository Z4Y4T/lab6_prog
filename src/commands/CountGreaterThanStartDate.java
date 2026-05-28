package commands;

import java.time.LocalDateTime;
import exceptions.InvalidCommandArgumentsException;
import exceptions.InvalidWorkerArgumentException;
import managers.CollectionManager;
import managers.ExceptionManager;

public class CountGreaterThanStartDate implements Command {
    private CollectionManager collectionManager;
    private final int numberOfArgs = 2;

    public CountGreaterThanStartDate(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public void execute(String... args) throws InvalidCommandArgumentsException, InvalidWorkerArgumentException {
        String arg = args[0] + " " + args[1];
        LocalDateTime date = ExceptionManager.checkStartDate(arg);
        collectionManager.countGreaterThanStartDate(date);
    }

    public String describe() {
        return "count_greater_than_start_date start_date - counts all elements that have start date greater than given";
    }

    public int getNumberOfArgs() {
        return numberOfArgs;
    }
}
