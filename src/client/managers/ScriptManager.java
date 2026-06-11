package client.managers;

import java.nio.file.Path;
import java.util.Stack;

import common.exceptions.InvalidCommandArgumentException;
import client.utilities.Reader;

/**
 * Менеджер источников ввода.
 * 
 * <p>
 * Хранит стек {@link Reader}ов. Дно стека — консольный Reader (интерактивный
 * режим).
 * При выполнении скрипта (команда {@code execute_script}) на вершину стека
 * помещается
 * новый Reader, читающий из файла. По завершении скрипта (конец файла) вершина
 * стека
 * снимается, и ввод продолжается из предыдущего источника.
 * </p>
 * 
 * <p>
 * Отвечает за обнаружение рекурсивных вызовов скриптов: если файл, который
 * пытаются
 * выполнить, уже присутствует в стеке (тот же абсолютный путь), выбрасывается
 * исключение.
 * </p>
 */
public class ScriptManager {
    private Stack<Reader> readerStack = new Stack<>();

    /**
     * Создаёт менеджер, помещая в стек консольный Reader.
     * Консольный Reader никогда не снимается со стека — он всегда на дне.
     * 
     * @param consoleReader Reader для чтения с консоли (System.in)
     */
    public ScriptManager(Reader consoleReader) {
        readerStack.push(consoleReader);
    }

    /**
     * Помещает новый Reader на вершину стека — переключает ввод на файл скрипта.
     * Перед добавлением проверяет, нет ли уже этого файла в стеке (защита от
     * рекурсии).
     * 
     * @param newReader Reader, читающий из файла скрипта
     * @throws InvalidCommandArgumentException если обнаружена рекурсия
     */
    public void pushScript(Reader newReader) throws InvalidCommandArgumentException {
        if (checkForRecursion(newReader.getCurrentFile())) {
            throw new InvalidCommandArgumentException("Recursive script call detected.");
        }
        readerStack.push(newReader);
    }

    /**
     * Снимает текущий Reader с вершины стека (если он не единственный —
     * консольный).
     * Перед снятием закрывает Reader, освобождая файловые ресурсы.
     * Консольный Reader (дно стека) никогда не снимается и не закрывается.
     */
    public void popScript() {
        if (readerStack.size() > 1) {
            readerStack.peek().close();
            readerStack.pop();
        }
    }

    /**
     * Возвращает текущий активный Reader (вершина стека).
     * Именно из него читаются все команды.
     * 
     * @return текущий Reader (консольный или файловый)
     */
    public Reader getCurrentReader() {
        return readerStack.peek();
    }

    /**
     * Проверяет, нет ли указанного файла в стеке Reader'ов.
     * Рекурсия определяется по совпадению абсолютных путей файлов.
     * 
     * @param newFile путь к новому файлу скрипта
     * @return {@code true} если такой файл уже есть в стеке (рекурсия)
     */
    private boolean checkForRecursion(Path newFile) {
        for (Reader reader : readerStack) {
            Path current = reader.getCurrentFile();
            if (current != null && current.equals(newFile)) {
                return true;
            }
        }
        return false;
    }
}