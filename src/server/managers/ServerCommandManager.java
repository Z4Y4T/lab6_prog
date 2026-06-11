package server.managers;

import java.util.HashMap;

import common.net.CommandType;
import server.commands.non_executable.ServerCommand;

/**
 * Менеджер серверных команд.
 * 
 * <p>
 * Аналог {@link client.managers.ClientCommandManager} для серверной стороны.
 * Хранит зарегистрированные команды в {@link HashMap}, где ключом является
 * тип команды ({@link CommandType}), а значением — реализация
 * {@link ServerCommand}.
 * </p>
 * 
 * <p>
 * В отличие от клиентского менеджера, не выбрасывает исключение при отсутствии
 * команды — вместо этого возвращает {@code null}. Проверка на null выполняется
 * в {@link server.main.ServerApplication#handleRequest(common.net.Request)}.
 * </p>
 * 
 * <p>
 * Регистрация всех команд происходит в
 * {@link server.main.ServerApplication#registerServerCommands()} при запуске
 * сервера.
 * </p>
 */
public class ServerCommandManager {
    private final HashMap<CommandType, ServerCommand> commandMap = new HashMap<>();

    /**
     * Регистрирует команду под указанным типом.
     * Если команда с таким типом уже существует — перезаписывает.
     *
     * @param type    тип команды из {@link CommandType}
     * @param command реализация {@link ServerCommand}
     */
    public void register(CommandType type, ServerCommand command) {
        commandMap.put(type, command);
    }

    /**
     * Возвращает команду по типу.
     *
     * @param type тип команды из {@link CommandType}
     * @return реализация {@link ServerCommand}, либо {@code null} если не найдена
     */
    public ServerCommand getCommand(CommandType type) {
        return commandMap.get(type);
    }

    /**
     * Проверяет, зарегистрирована ли команда с указанным типом.
     *
     * @param type тип команды
     * @return {@code true} если команда существует
     */
    public boolean containsCommand(CommandType type) {
        return commandMap.containsKey(type);
    }

    /**
     * Возвращает ссылку на внутреннюю мапу всех команд.
     *
     * @return {@link HashMap} со всеми зарегистрированными командами
     */
    public HashMap<CommandType, ServerCommand> getCommandMap() {
        return commandMap;
    }
}