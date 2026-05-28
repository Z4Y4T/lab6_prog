package objects;

import java.util.Locale;

public class Coordinates {
    private final long x;
    private Float y;

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