package commands;

import managers.CollectionManager;

public class Clear implements Command {
    private CollectionManager collectionManager;
    private final int numberOfArgs = 0;

    public Clear(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public void execute(String... args) {
        collectionManager.clear();
    }

    public String describe() {
        return "clear - clears collection";
    }

    public int getNumberOfArgs() {
        return numberOfArgs;
    }
}
