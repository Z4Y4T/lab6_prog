package server.utilities;

import java.util.Comparator;

import common.domain.Worker;

/**
 * Компараторы для сравнения {@link Worker} по разным полям.
 * 
 * <p>
 * Содержит статические константы — готовые компараторы для использования
 * в Stream API ({@code sorted()}, {@code max()}, {@code min()}):
 * </p>
 * <ul>
 * <li>{@link #BY_SALARY} — по зарплате</li>
 * <li>{@link #BY_START_DATE} — по дате начала работы</li>
 * </ul>
 * 
 * <p>
 * Закрытый конструктор предотвращает создание экземпляров.
 * </p>
 */
public final class WorkerComparator {

    private WorkerComparator() {
    }

    /** Компаратор по зарплате (от меньшей к большей) */
    public static final Comparator<Worker> BY_SALARY = (w1, w2) -> Double.compare(w1.getSalary(), w2.getSalary());

    /** Компаратор по дате начала работы (от более ранней к более поздней) */
    public static final Comparator<Worker> BY_START_DATE = (w1, w2) -> w1.getStartDate().compareTo(w2.getStartDate());
}