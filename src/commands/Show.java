package commands;

import managers.CollectionManager;

public class Show implements Command {
    private CollectionManager collectionManager;
    private final int numberOfArgs = 0;

    public Show(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public void execute(String... args) {
        collectionManager.show();
    }

    public String describe() {
        return "show - displays all collection elements";
    }

    public int getNumberOfArgs() {
        return numberOfArgs;
    }
}
