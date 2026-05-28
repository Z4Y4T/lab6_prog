package commands;

import managers.CollectionManager;

public class Info implements Command {
    private CollectionManager collectionManager;
    private final int numberOfArgs = 0;

    public Info(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public void execute(String... args) {
        System.out.println(collectionManager);
    }

    public String describe() {
        return "info - displays informatiom about collection(initialization time, number of elements, collection type)";
    }

    public int getNumberOfArgs() {
        return numberOfArgs;
    }
}
