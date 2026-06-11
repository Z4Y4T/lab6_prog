package client.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Обёртка над {@link Scanner}, унифицирующая чтение из консоли и из файлов.
 * 
 * <p>
 * Используется для прозрачного переключения между интерактивным режимом
 * (ввод с клавиатуры) и режимом выполнения скрипта (чтение из файла).
 * При работе со скриптом хранит абсолютный путь к файлу — это необходимо
 * для обнаружения рекурсивных вызовов в {@link client.managers.ScriptManager}.
 * </p>
 * 
 * <p>
 * В интерактивном режиме {@link #close()} не делает ничего (поток System.in
 * не закрывается), в файловом режиме — закрывает Scanner и связанный с ним
 * файловый поток.
 * </p>
 */
public class Reader {
    private final Scanner source;
    private final boolean isInteractive;
    private final Path filePath;

    /**
     * Создаёт Reader для интерактивного режима (чтение с консоли System.in).
     * Поле {@code filePath} устанавливается в {@code null} — это признак того,
     * что источник не является файлом.
     */
    public Reader() {
        this.source = new Scanner(System.in);
        this.isInteractive = true;
        this.filePath = null;
    }

    /**
     * Создаёт Reader для выполнения скрипта (чтение из файла).
     * Сохраняет абсолютный путь к файлу для последующей проверки на рекурсию.
     * 
     * @param file файл скрипта, должен существовать и быть доступным для чтения
     * @throws FileNotFoundException если файл не найден
     */
    public Reader(File file) throws FileNotFoundException {
        this.source = new Scanner(file);
        this.isInteractive = false;
        this.filePath = file.toPath().toAbsolutePath().normalize();
    }

    /**
     * Возвращает {@code true}, если Reader читает с консоли (интерактивный режим).
     * В этом режиме пользователю выводятся подсказки и сообщения об ошибках.
     * 
     * @return {@code true} для консоли, {@code false} для файла
     */
    public boolean isInteractive() {
        return isInteractive;
    }

    /**
     * Читает следующую строку из источника.
     * 
     * @return строка без символа перевода строки, либо {@code null} если достигнут
     *         конец
     */
    public String readLine() {
        return source.hasNextLine() ? source.nextLine() : null;
    }

    /**
     * Проверяет, есть ли ещё строки для чтения.
     * 
     * @return {@code true} если есть следующая строка
     */
    public boolean hasNextLine() {
        return source.hasNextLine();
    }

    /**
     * Возвращает абсолютный путь к текущему файлу скрипта.
     * Используется {@link client.managers.ScriptManager} для проверки рекурсии.
     * 
     * @return путь к файлу, либо {@code null} если режим интерактивный
     */
    public Path getCurrentFile() {
        return filePath;
    }

    /**
     * Закрывает источник чтения, если он файловый.
     * В интерактивном режиме не делает ничего — System.in остаётся открытым.
     */
    public void close() {
        if (!isInteractive) {
            source.close();
        }
    }
}