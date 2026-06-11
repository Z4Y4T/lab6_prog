package server.utilities;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Генератор уникальных идентификаторов для новых элементов коллекции.
 * 
 * <p>
 * Потокобезопасный — использует {@link AtomicInteger}.
 * При старте сервера начальное значение устанавливается на 1 большим,
 * чем максимальный id в загруженной коллекции (вызов
 * {@link #setCurrentId(int)}).
 * </p>
 */
public class IdGenerator {
    private static final AtomicInteger currentId = new AtomicInteger(1);
    private static final int firstId = 1;

    /**
     * Возвращает следующий уникальный id и увеличивает счётчик.
     *
     * @return следующий id
     */
    public static Integer nextId() {
        return currentId.getAndIncrement();
    }

    /**
     * Устанавливает текущее значение счётчика.
     * Используется после загрузки коллекции из файла для синхронизации.
     *
     * @param id значение, с которого продолжать генерацию
     */
    public static void setCurrentId(Integer id) {
        currentId.set(id);
    }

    /**
     * Возвращает текущее значение счётчика (без увеличения).
     *
     * @return текущий id
     */
    public static Integer getCurrentId() {
        return currentId.get();
    }

    /**
     * Сбрасывает счётчик к начальному значению.
     */
    public static void reset() {
        currentId.set(firstId);
    }

    /**
     * Возвращает самое первое значение счётчика.
     *
     * @return firstId
     */
    public static Integer getFirstId() {
        return firstId;
    }
}