package exceptions;

public class XMLParsingException extends Exception {
    public XMLParsingException(String message) {
        super(message);
    }

    public XMLParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
