package utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import managers.ExceptionManager;

public class Reader {
    private final Scanner source;
    private final boolean isInteractive;
    private final String fileName;

    public Reader() {
        source = new Scanner(System.in);
        isInteractive = true;
        fileName = null;
    }

    public Reader(String filename) throws FileNotFoundException, SecurityException {
        File file = ExceptionManager.checkFile(filename);
        source = new Scanner(file);
        isInteractive = false;
        this.fileName = filename;
    }

    public boolean isInteractive() {
        return isInteractive;
    }

    public String readLine() {
        return source.hasNextLine() ? source.nextLine() : null;
    }

    public boolean hasNextLine() {
        return source.hasNextLine();
    }

    public String getCurrentFile() {
        return fileName;
    }

    public void close() {
        if (source != null) {
            source.close();
        }
    }
}