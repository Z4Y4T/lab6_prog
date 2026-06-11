package common.net;

import java.io.Serializable;

/**
 * Фрагмент (чанк) данных при передаче больших объектов по UDP.
 * 
 * <p>
 * Когда сериализованный объект превышает максимальный размер UDP-пакета,
 * он разбивается на части — чанки. Каждый чанк содержит:
 * </p>
 * <ul>
 * <li>{@code requestId} — идентификатор запроса, к которому относится чанк</li>
 * <li>{@code chunkIndex} — порядковый номер чанка (от 0)</li>
 * <li>{@code totalChunks} — общее количество чанков</li>
 * <li>{@code payload} — данные чанка (массив байтов)</li>
 * </ul>
 * 
 * <p>
 * Разбиением и сборкой занимается {@link ChunkManager}.
 * </p>
 */
public class Chunk implements Serializable {
    private static final long serialVersionUID = 1L;
    private final long requestId;
    private final int chunkIndex;
    private final int totalChunks;
    private final byte[] payload;

    /**
     * Создаёт чанк.
     *
     * @param requestId   идентификатор запроса
     * @param chunkIndex  номер чанка (от 0 до totalChunks-1)
     * @param totalChunks общее количество чанков
     * @param payload     данные чанка
     */
    public Chunk(long requestId, int chunkIndex, int totalChunks, byte[] payload) {
        this.requestId = requestId;
        this.chunkIndex = chunkIndex;
        this.totalChunks = totalChunks;
        this.payload = payload;
    }

    public long getRequestId() {
        return requestId;
    }

    public int getChunkIndex() {
        return chunkIndex;
    }

    public int getTotalChunks() {
        return totalChunks;
    }

    public byte[] getPayload() {
        return payload;
    }
}