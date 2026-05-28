package commands;

import managers.CollectionManager;

public class MaxBySalary implements Command {
    private CollectionManager collectionManager;
    private final int numberOfArgs = 0;

    public MaxBySalary(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public void execute(String... args) {
        collectionManager.maxBySalary();
    }

    public String describe() {
        return "max_by_salary - displays one of the elements with greatest salary";
    }

    public int getNumberOfArgs() {
        return numberOfArgs;
    }
}
