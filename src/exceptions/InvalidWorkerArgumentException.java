package exceptions;

public class InvalidWorkerArgumentException extends Exception {
    public InvalidWorkerArgumentException(String message) {
        super(message);
    }

    public InvalidWorkerArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
