package exceptions;

public class InvalidCommandArgumentsException extends Exception {
    public InvalidCommandArgumentsException(String message) {
        super(message);
    }

    public InvalidCommandArgumentsException(String message, Throwable cause) {
        super(message, cause);
    }
}
