package server.managers;

import java.util.HashMap;

import common.exceptions.InvalidCommandException;
import server.commands.executable.ExecutableServerCommand;

/**
 * Менеджер консольных команд сервера.
 * 
 * <p>
 * Аналог {@link client.managers.ClientCommandManager} для серверной стороны.
 * Хранит зарегистрированные команды в {@link HashMap}, где ключом является
 * строковое имя команды, а значением — реализация
 * {@link ExecutableServerCommand}.
 * Предоставляет методы для регистрации, получения и проверки наличия команд.
 * </p>
 */
public class ExecutableCommandManager {
    private final HashMap<String, ExecutableServerCommand> commandMap = new HashMap<>();

    /**
     * Регистрирует команду под указанным именем.
     *
     * @param name    строковое имя команды (например, "save", "exit")
     * @param command реализация {@link ExecutableServerCommand}
     */
    public void register(String name, ExecutableServerCommand command) {
        commandMap.put(name, command);
    }

    /**
     * Возвращает команду по имени.
     *
     * @param name строковое имя команды
     * @return реализация {@link ExecutableServerCommand}
     * @throws InvalidCommandArgumentException если команда с таким именем не
     *                                         зарегистрирована
     */
    public ExecutableServerCommand getCommand(String name) throws InvalidCommandException {
        ExecutableServerCommand command = commandMap.get(name);
        if (command == null) {
            throw new InvalidCommandException("Unknown command. Type 'help' for a list of commands.");
        }
        return command;
    }

    /**
     * Проверяет, зарегистрирована ли команда с указанным именем.
     *
     * @param name строковое имя команды
     * @return {@code true} если команда существует
     */
    public boolean containsCommand(String name) {
        return commandMap.containsKey(name);
    }

    /**
     * Возвращает ссылку на внутреннюю мапу всех команд.
     * Используется командой {@code help} для вывода списка доступных команд.
     *
     * @return {@link HashMap} со всеми зарегистрированными командами
     */
    public HashMap<String, ExecutableServerCommand> getCommandMap() {
        return commandMap;
    }
}