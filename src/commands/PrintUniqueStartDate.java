package commands;

import managers.CollectionManager;

public class PrintUniqueStartDate implements Command {
    private CollectionManager collectionManager;
    private final int numberOfArgs = 0;

    public PrintUniqueStartDate(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public void execute(String... args) {
        collectionManager.printUniqueStartDate();
    }

    public String describe() {
        return "print_unique_start_date - displays every unique start date in the collection";
    }

    public int getNumberOfArgs() {
        return numberOfArgs;
    }
}
