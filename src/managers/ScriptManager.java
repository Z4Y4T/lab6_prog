package managers;

import java.util.Stack;
import utilities.*;
import exceptions.InvalidCommandException;

public class ScriptManager {
    private Stack<Reader> readerStack = new Stack<>();
    private Reader currentReader;

    public ScriptManager(Reader consoleReader) {
        readerStack.push(consoleReader);
        this.currentReader = consoleReader;
    }

    public void pushScript(Reader newReader) throws InvalidCommandException {
        if (checkForRecursion(newReader.getCurrentFile())) {
            throw new InvalidCommandException("Recursive script call detected");
        }
        readerStack.push(newReader);
        currentReader = newReader;
    }


    public void popScript() {
        if (!readerStack.isEmpty()) {
            currentReader.close();
            readerStack.pop();
            if (!readerStack.isEmpty()) {
                currentReader = readerStack.peek();
            }
        }
    }

    public Reader getCurrentReader() {
        return currentReader;
    }

    private boolean checkForRecursion(String newFilename) {
        if (currentReader.getCurrentFile() != null &&
                currentReader.getCurrentFile().equals(newFilename)) {
            return true;
        }
        for (Reader r : readerStack) {
            String f = r.getCurrentFile();
            if (f != null && f.equals(newFilename)) {
                return true;
            }
        }
        return false;
    }
}