package client.commands;

import java.io.File;
import java.io.FileNotFoundException;

import common.exceptions.InvalidCommandArgumentException;
import common.net.Response;
import common.parsers.CommandArgumentParser;
import client.managers.ScriptManager;
import client.utilities.Reader;

/**
 * Клиентская команда {@code execute_script}.
 * Выполняет команды из указанного файла-скрипта.
 * 
 * <p>
 * Принимает один аргумент — путь к файлу. Файл должен существовать
 * и быть доступным для чтения. Команды из файла выполняются последовательно,
 * каждая строка интерпретируется как отдельная команда.
 * </p>
 * 
 * <p>
 * При обнаружении рекурсивного вызова (скрипт вызывает сам себя напрямую
 * или через цепочку других скриптов) выполнение прерывается с ошибкой.
 * Проверка на рекурсию выполняется в {@link ScriptManager#pushScript(Reader)}.
 * </p>
 * 
 * <p>
 * Запрос на сервер не отправляется — команда только переключает источник ввода
 * на файл. Дальнейшие команды читаются из файла, пока он не закончится.
 * </p>
 */
public class ExecuteScriptClientCommand implements ClientCommand {
    private final ScriptManager scriptManager;

    public ExecuteScriptClientCommand(ScriptManager scriptManager) {
        this.scriptManager = scriptManager;
    }

    @Override
    public String getName() {
        return "execute_script";
    }

    @Override
    public String getDescription() {
        return "executes script";
    }

    /**
     * Выполняет команду: проверяет аргументы, открывает файл скрипта
     * и переключает источник ввода на него.
     * 
     * @param args массив из одного элемента — путь к файлу скрипта
     * @return Response с подтверждением начала выполнения скрипта,
     *         либо с ошибкой (файл не найден, рекурсия)
     */
    @Override
    public Response execute(String... args) {
        try {
            CommandArgumentParser.checkArgumentCount(args, 1);
            File file = CommandArgumentParser.parseFile(args);
            Reader newReader = new Reader(file);
            scriptManager.pushScript(newReader);
            return new Response(true, "Starting script...", null);
        } catch (InvalidCommandArgumentException | FileNotFoundException e) {
            return new Response(false, "Failed to execute script: " + e.getMessage(), null);
        }
    }
}