package common.domain;

import java.io.Serializable;
import java.util.Locale;

/**
 * Координаты работника.
 * 
 * <p>
 * Поля:
 * </p>
 * <ul>
 * <li>{@code x} — целое число, не более 53</li>
 * <li>{@code y} — дробное число, больше -63</li>
 * </ul>
 */
public class Coordinates implements Serializable {
    private static final long serialVersionUID = 5L;
    private final long x;
    private final Float y;

    /**
     * Создаёт координаты.
     *
     * @param x координата X (не более 53)
     * @param y координата Y (больше -63)
     */
    public Coordinates(long x, Float y) {
        this.x = x;
        this.y = y;
    }

    public long getX() {
        return x;
    }

    public Float getY() {
        return y;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "(%d, %.4f)", x, y);
    }
}