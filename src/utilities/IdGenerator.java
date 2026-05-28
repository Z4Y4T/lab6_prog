package utilities;

public class IdGenerator {
    private static Integer currentId = 67;
    private static Integer firstId = currentId;

    public static Integer nextId() {
        return currentId++;
    }

    public static void setCurrentId(Integer id) {
        currentId = id;
    }

    public static Integer getCurrentId() {
        return currentId;
    }

    public static void reset() {
        currentId = firstId;
    }

    public static Integer getFirstId() {
        return firstId;
    }
}