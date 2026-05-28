package commands;

import java.io.FileNotFoundException;

import exceptions.InvalidCommandArgumentsException;
import exceptions.InvalidCommandException;
import main.Application;
import utilities.Factory;
import utilities.Reader;

public class Execute implements Command {
    private final int numberOfArgs = 1;
    private final Application app;

    public Execute(Application app) {
        this.app = app;
    }

    public void execute(String... args) throws InvalidCommandArgumentsException {
        try {
            Reader newreader = new Reader(args[0]);
            app.setFactory(new Factory(newreader));
            app.getScriptManager().pushScript(newreader);
        } catch (FileNotFoundException | SecurityException | InvalidCommandException e) {
            throw new InvalidCommandArgumentsException(e.getMessage(), e);
        }
    }

    public String describe() {
        return "execute_script file_name - executes script";
    }

    public int getNumberOfArgs() {
        return numberOfArgs;
    }
}