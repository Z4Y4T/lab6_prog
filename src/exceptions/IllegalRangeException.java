package exceptions;

public class IllegalRangeException extends Exception {
    public IllegalRangeException(String message) {
        super(message);
    }

    public IllegalRangeException(String message, Throwable cause) {
        super(message, cause);
    }
}
