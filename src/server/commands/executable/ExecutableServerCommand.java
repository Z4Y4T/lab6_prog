package server.commands.executable;

import common.exceptions.InvalidCommandArgumentException;

/**
 * Интерфейс консольной команды сервера.
 * 
 * <p>
 * Аналог {@link client.commands.ClientCommand} для серверной стороны.
 * В отличие от {@link ServerCommand}, которая обрабатывает запросы от клиентов,
 * реализации этого интерфейса выполняются администратором напрямую через
 * консоль.
 * </p>
 * 
 * <p>
 * Каждая команда предоставляет:
 * </p>
 * <ul>
 * <li>Название и описание — для вывода справки (команда {@code help})</li>
 * <li>Метод {@code execute} — принимает массив строк-аргументов и выполняет
 * команду</li>
 * </ul>
 */
public interface ExecutableServerCommand {

    /**
     * Возвращает название команды, по которому она вызывается из консоли.
     *
     * @return строка с именем команды (например, "save", "exit")
     */
    String getName();

    /**
     * Возвращает краткое описание того, что делает команда.
     * Используется командой {@code help} для вывода списка доступных команд.
     *
     * @return строка с описанием команды
     */
    String getDescription();

    /**
     * Выполняет команду: проверяет аргументы и выполняет действие.
     *
     * @param args массив строк-аргументов, полученных из командной строки
     *             (уже без названия самой команды)
     * @throws InvalidCommandArgumentException если аргументы неверны (количество
     *                                         или тип)
     */
    void execute(String... args) throws InvalidCommandArgumentException;
}