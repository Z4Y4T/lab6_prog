package commands;

public class Exit implements Command {
    private final int numberOfArgs = 0;

    public void execute(String... args) {
        System.exit(0);
    }

    public String describe() {
        return "exit - shuts down the programm (without saving data)";
    }

    public int getNumberOfArgs() {
        return numberOfArgs;
    }
}
