package client.managers;

import java.util.HashMap;

import client.commands.ClientCommand;
import common.exceptions.InvalidCommandException;

/**
 * Менеджер клиентских команд.
 * 
 * <p>
 * Хранит зарегистрированные команды в {@link HashMap}, где ключом является
 * строковое имя команды, а значением — реализация {@link ClientCommand}.
 * Предоставляет методы для регистрации, получения и проверки наличия команд.
 * </p>
 * 
 * <p>
 * Регистрация всех команд происходит в
 * {@link client.main.ClientApplication#initCommands()}
 * при запуске клиента.
 * </p>
 */
public class ClientCommandManager {
    private final HashMap<String, ClientCommand> commandMap = new HashMap<>();

    /**
     * Регистрирует команду под указанным именем.
     * Если команда с таким именем уже существует — перезаписывает.
     * 
     * @param name    строковое имя команды (например, "add", "show")
     * @param command реализация {@link ClientCommand}
     */
    public void register(String name, ClientCommand command) {
        commandMap.put(name, command);
    }

    /**
     * Возвращает команду по имени.
     * 
     * @param name строковое имя команды
     * @return реализация {@link ClientCommand}
     * @throws InvalidCommandException если команда с таким именем не
     *                                 зарегистрирована
     */
    public ClientCommand getCommand(String name) throws InvalidCommandException {
        ClientCommand command = commandMap.get(name);
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
    public HashMap<String, ClientCommand> getCommandMap() {
        return commandMap;
    }
}