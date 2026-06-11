package common.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Утилитный класс для сериализации и десериализации объектов.
 * 
 * <p>
 * Использует стандартную Java-сериализацию через {@link ObjectOutputStream}
 * и {@link ObjectInputStream}. Объекты превращаются в массивы байтов и обратно.
 * </p>
 * 
 * <p>
 * Все сериализуемые объекты должны реализовывать интерфейс
 * {@link java.io.Serializable}.
 * </p>
 */
public class Serializer {

    /**
     * Сериализует объект в массив байтов.
     *
     * @param obj объект для сериализации (должен быть Serializable)
     * @return массив байтов
     * @throws IOException если объект не сериализуем или произошла ошибка
     *                     ввода-вывода
     */
    public static byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            return baos.toByteArray();
        }
    }

    /**
     * Десериализует массив байтов обратно в объект.
     *
     * @param bytes массив байтов
     * @return восстановленный объект
     * @throws IOException если данные повреждены или класс не найден
     */
    public static Object deserialize(byte[] bytes) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = new ObjectInputStream(bais)) {
            return ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Failed to deserialize object: " + e.getMessage());
        }
    }
}