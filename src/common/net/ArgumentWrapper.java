package common.net;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Обёртка для аргумента команды.
 * 
 * <p>
 * Позволяет передавать в {@link common.net.Request} аргумент одного из двух
 * типов:
 * </p>
 * <ul>
 * <li>Целое число — для команд {@code remove_by_id}, {@code update},
 * {@code insert_at}</li>
 * <li>Дата и время — для команды {@code count_greater_than_start_date}</li>
 * </ul>
 * 
 * <p>
 * Использует два конструктора, каждый из которых принимает свой тип.
 * Неиспользуемое поле остаётся {@code null}. При получении сервер проверяет,
 * какое из полей не null, и использует его.
 * </p>
 */
public class ArgumentWrapper implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Integer num;
    private final LocalDateTime localDateTime;

    /**
     * Создаёт обёртку с целочисленным аргументом (id или индекс).
     *
     * @param num число (id или индекс)
     */
    public ArgumentWrapper(Integer num) {
        this.num = num;
        this.localDateTime = null;
    }

    /**
     * Создаёт обёртку с аргументом-датой.
     *
     * @param localDateTime дата и время
     */
    public ArgumentWrapper(LocalDateTime localDateTime) {
        this.num = null;
        this.localDateTime = localDateTime;
    }

    /**
     * Возвращает целочисленный аргумент.
     *
     * @return число, либо {@code null} если аргумент не числовой
     */
    public Integer getNum() {
        return num;
    }

    /**
     * Возвращает аргумент-дату.
     *
     * @return дата и время, либо {@code null} если аргумент не дата
     */
    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
}