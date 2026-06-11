package common.net;

import java.util.Map;

/**
 * Утилитный класс для разбиения данных на чанки и обратной сборки.
 * Используется как клиентом, так и сервером для отправки объектов,
 * превышающих максимальный размер UDP-пакета.
 * 
 * <p>
 * Логика работы:
 * </p>
 * <ol>
 * <li>Отправляющая сторона сериализует объект в {@code byte[]}</li>
 * <li>Вызывает {@link #chunkify(byte[], long, int)} — получает массив
 * {@link Chunk}</li>
 * <li>Каждый Chunk сериализуется отдельно и отправляется по UDP</li>
 * <li>Принимающая сторона складывает полученные чанки в
 * {@code Map<Integer, byte[]>}</li>
 * <li>Когда все чанки получены — вызывает {@link #assemble(Map, int)} для
 * сборки</li>
 * <li>Полученный {@code byte[]} десериализуется в исходный объект</li>
 * </ol>
 */
public class ChunkManager {

    /**
     * Разбивает массив байтов на чанки.
     *
     * @param data      исходный массив байтов (сериализованный объект)
     * @param requestId идентификатор запроса, к которому относятся чанки
     * @param chunkSize максимальный размер одного чанка в байтах
     * @return массив чанков, готовых к отправке
     */
    public static Chunk[] chunkify(byte[] data, long requestId, int chunkSize) {
        int totalChunks = (data.length + chunkSize - 1) / chunkSize;
        Chunk[] chunks = new Chunk[totalChunks];

        for (int i = 0; i < totalChunks; i++) {
            int start = i * chunkSize;
            int end = Math.min(data.length, start + chunkSize);
            byte[] payload = new byte[end - start];
            System.arraycopy(data, start, payload, 0, payload.length);
            chunks[i] = new Chunk(requestId, i, totalChunks, payload);
        }

        return chunks;
    }

    /**
     * Собирает чанки обратно в полный массив байтов.
     * Вызывает {@link IllegalStateException}, если какой-либо чанк отсутствует.
     *
     * @param chunkMap    мапа: индекс чанка -> payload
     * @param totalChunks ожидаемое количество чанков
     * @return полный массив байтов (готов к десериализации)
     * @throws IllegalStateException если не все чанки получены
     */
    public static byte[] assemble(Map<Integer, byte[]> chunkMap, int totalChunks) {
        int totalLength = 0;
        for (int i = 0; i < totalChunks; i++) {
            byte[] payload = chunkMap.get(i);
            if (payload == null) {
                throw new IllegalStateException("Missing chunk " + i + " of " + totalChunks);
            }
            totalLength += payload.length;
        }

        byte[] result = new byte[totalLength];
        int position = 0;
        for (int i = 0; i < totalChunks; i++) {
            byte[] payload = chunkMap.get(i);
            System.arraycopy(payload, 0, result, position, payload.length);
            position += payload.length;
        }

        return result;
    }
}