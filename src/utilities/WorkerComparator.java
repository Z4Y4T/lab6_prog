package utilities;

import objects.*;
import java.util.Comparator;

public final class WorkerComparator {

    private WorkerComparator() {
    }

    public static final Comparator<Worker> BY_SALARY = (w1, w2) -> Double.compare(w1.getSalary(), w2.getSalary());

    public static final Comparator<Worker> BY_START_DATE = (w1, w2) -> w1.getStartDate().compareTo(w2.getStartDate());
}