package managers;

import java.util.HashMap;
import commands.Command;

public class CommandManager {
    private final HashMap<String, Command> commandMap = new HashMap<>();

    public void register(String name, Command command) {
        commandMap.put(name, command);
    }

    public Command getCommand(String name) {
        return commandMap.get(name);
    }

    public boolean containsCommand(String name) {
        return commandMap.containsKey(name);
    }

    public HashMap<String, Command> getCommandMap() {
        return commandMap;
    }
}