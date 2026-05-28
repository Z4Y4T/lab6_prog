package exceptions;

public class FileAccessException extends Exception {
    public FileAccessException(String message) {
        super(message);
    }

    public FileAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
