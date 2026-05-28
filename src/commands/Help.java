package commands;

import java.util.HashMap;

public class Help implements Command {
    private HashMap<String, Command> commandMap;
    private final int numberOfArgs = 0;

    public Help(HashMap<String, Command> commandMap) {
        this.commandMap = commandMap;
    }

    @Override
    public void execute(String... args) {
        for (Command command : commandMap.values()) {
            System.out.println(command.describe());
        }
    }

    public String describe() {
        return "help - shows all commands with description";
    }

    public int getNumberOfArgs() {
        return numberOfArgs;
    }
}
