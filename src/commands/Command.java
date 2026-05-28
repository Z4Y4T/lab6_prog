package commands;

import exceptions.InvalidCommandArgumentsException;
import exceptions.InvalidWorkerArgumentException;

public interface Command {
    public String describe();

    public void execute(String... args) throws InvalidCommandArgumentsException, InvalidWorkerArgumentException;

    public int getNumberOfArgs();
}
