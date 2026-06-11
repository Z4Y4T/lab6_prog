package common.net;

import java.io.Serializable;

import common.domain.Worker;

/**
 * Запрос от клиента к серверу.
 * 
 * <p>
 * Содержит всю информацию, необходимую серверу для выполнения команды:
 * </p>
 * <ul>
 * <li>{@code requestId} — уникальный идентификатор запроса (присваивается
 * клиентом)</li>
 * <li>{@code command} — тип команды из {@link CommandType}</li>
 * <li>{@code argument} — аргумент команды (id, дата, индекс), упакованный в
 * {@link ArgumentWrapper}</li>
 * <li>{@code worker} — объект Worker (для команд add, update, insert_at и
 * др.)</li>
 * </ul>
 * 
 * <p>
 * Клиентские команды создают Request без requestId. Клиент присваивает id
 * через {@link #withRequestId(long)} перед отправкой.
 * </p>
 */
public class Request implements Serializable {
    private static final long serialVersionUID = 2L;
    private final long requestId;
    private final CommandType command;
    private final ArgumentWrapper argument;
    private final Worker worker;

    /**
     * Создаёт запрос без указания requestId (id = 0).
     * Используется клиентскими командами. Настоящий id присваивается в
     * {@link #withRequestId(long)}.
     *
     * @param command  тип команды
     * @param argument аргумент команды (может быть null)
     * @param worker   объект Worker (может быть null)
     */
    public Request(CommandType command, ArgumentWrapper argument, Worker worker) {
        this(0L, command, argument, worker);
    }

    /**
     * Создаёт запрос с указанием всех полей, включая requestId.
     * Вызывается из {@link #withRequestId(long)} при присвоении id.
     *
     * @param requestId уникальный идентификатор запроса
     * @param command   тип команды
     * @param argument  аргумент команды (может быть null)
     * @param worker    объект Worker (может быть null)
     */
    public Request(long requestId, CommandType command, ArgumentWrapper argument, Worker worker) {
        this.requestId = requestId;
        this.command = command;
        this.argument = argument;
        this.worker = worker;
    }

    public long getRequestId() {
        return requestId;
    }

    /**
     * Создаёт копию запроса с новым requestId.
     * Используется в {@link client.main.Client#send(Request)}.
     *
     * @param requestId новый идентификатор
     * @return новый Request с заданным id
     */
    public Request withRequestId(long requestId) {
        return new Request(requestId, command, argument, worker);
    }

    public CommandType getCommand() {
        return command;
    }

    public ArgumentWrapper getArgument() {
        return argument;
    }

    public Worker getWorker() {
        return worker;
    }
}