package common.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * Основной класс — работник.
 * Хранит все данные об элементе коллекции.
 * 
 * <p>
 * Поля:
 * </p>
 * <ul>
 * <li>{@code id} — уникальный идентификатор (присваивается сервером)</li>
 * <li>{@code name} — имя работника (обязательное, не может быть null)</li>
 * <li>{@code coordinates} — координаты (обязательное)</li>
 * <li>{@code creationDate} — дата создания записи (присваивается сервером)</li>
 * <li>{@code salary} — зарплата (обязательное, больше 0)</li>
 * <li>{@code startDate} — дата начала работы (обязательное)</li>
 * <li>{@code position} — должность (может быть null)</li>
 * <li>{@code status} — статус (обязательное)</li>
 * <li>{@code organization} — организация (может быть null)</li>
 * </ul>
 * 
 * <p>
 * Объекты создаются через внутренний класс {@link WorkerBuilder}.
 * Прямой конструктор закрыт — все объекты иммутабельны (нет сеттеров).
 * </p>
 * 
 * <p>
 * Сравнение по умолчанию ({@link #compareTo(Worker)}) — сначала по имени,
 * затем по id. Два Worker'а считаются равными ({@link #equals(Object)}),
 * если у них одинаковый id.
 * </p>
 */
public class Worker implements Comparable<Worker>, Serializable {
    private static final long serialVersionUID = 4L;

    private final Integer id;
    private final String name;
    private final Coordinates coordinates;
    private final java.util.Date creationDate;
    private final float salary;
    private final java.time.LocalDateTime startDate;
    private final Position position;
    private final Status status;
    private final Organization organization;

    /**
     * Закрытый конструктор — объекты создаются только через
     * {@link WorkerBuilder#build()}.
     */
    private Worker(WorkerBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.creationDate = builder.creationDate;
        this.coordinates = builder.coordinates;
        this.salary = builder.salary;
        this.startDate = builder.startDate;
        this.position = builder.position;
        this.status = builder.status;
        this.organization = builder.organization;
    }

    /**
     * Строитель (Builder) для пошагового создания объекта {@link Worker}.
     * 
     * <p>
     * Позволяет задавать поля выборочно — те, что не заданы явно,
     * останутся {@code null} (кроме примитивов). Сборка вызывается методом
     * {@link #build()}.
     * </p>
     * 
     * <p>
     * Может создаваться с нуля или на основе существующего Worker'а
     * (конструктор копирования).
     * </p>
     */
    public static class WorkerBuilder {
        private Integer id;
        private String name;
        private Coordinates coordinates;
        private java.util.Date creationDate;
        private float salary;
        private java.time.LocalDateTime startDate;
        private Position position = null;
        private Status status;
        private Organization organization = null;

        /** Создаёт пустой строитель */
        public WorkerBuilder() {
        }

        /**
         * Создаёт строитель, заполненный данными существующего Worker'а.
         * Используется для обновления (update) — копирует все поля, кроме id и
         * creationDate.
         */
        public WorkerBuilder(Worker worker) {
            this.id = worker.getId();
            this.name = worker.getName();
            this.coordinates = worker.getCoordinates();
            this.creationDate = worker.getCreationDate();
            this.salary = worker.getSalary();
            this.startDate = worker.getStartDate();
            this.position = worker.getPosition();
            this.status = worker.getStatus();
            this.organization = worker.getOrganization();
        }

        public WorkerBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public WorkerBuilder name(String name) {
            this.name = name;
            return this;
        }

        public WorkerBuilder coordinates(Coordinates coordinates) {
            this.coordinates = coordinates;
            return this;
        }

        public WorkerBuilder creationDate(java.util.Date creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public WorkerBuilder salary(float salary) {
            this.salary = salary;
            return this;
        }

        public WorkerBuilder startDate(java.time.LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public WorkerBuilder position(Position position) {
            this.position = position;
            return this;
        }

        public WorkerBuilder status(Status status) {
            this.status = status;
            return this;
        }

        public WorkerBuilder organization(Organization organization) {
            this.organization = organization;
            return this;
        }

        /**
         * Собирает объект {@link Worker} из заданных полей.
         *
         * @return новый иммутабельный Worker
         */
        public Worker build() {
            return new Worker(this);
        }
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public java.util.Date getCreationDate() {
        return creationDate;
    }

    public float getSalary() {
        return salary;
    }

    public java.time.LocalDateTime getStartDate() {
        return startDate;
    }

    public Position getPosition() {
        return position;
    }

    public Status getStatus() {
        return status;
    }

    public Organization getOrganization() {
        return organization;
    }

    /**
     * Сравнивает двух работников: сначала по имени (лексикографически),
     * при равенстве имён — по id.
     * 
     * <p>
     * Worker с {@code id == null} считается меньше Worker'а с ненулевым id.
     * </p>
     *
     * @param other другой Worker для сравнения
     * @return отрицательное, ноль или положительное число
     */
    @Override
    public int compareTo(Worker other) {
        int nameCompare = this.name.compareTo(other.name);
        if (nameCompare != 0)
            return nameCompare;
        if (this.id == null && other.id == null)
            return 0;
        if (this.id == null)
            return -1;
        if (other.id == null)
            return 1;
        return Integer.compare(this.id, other.id);
    }

    /**
     * Два Worker'а равны, если у них одинаковый id.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Worker worker = (Worker) o;
        return Objects.equals(id, worker.id);
    }

    /**
     * Хэш-код вычисляется по id.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Строковое представление Worker'а со всеми полями.
     *
     * @return строка вида "Worker: {id: 1; name: ...; ...}"
     */
    @Override
    public String toString() {
        return String.format(
                "Worker: {id: %d; name: %s; coordinates: %s; creation date: %s; salary: %s; start date: %s; position: %s; status: %s; organization: %s}",
                id,
                name,
                coordinates,
                creationDate != null ? creationDate.toString() : "null",
                salary,
                startDate != null ? startDate.toString() : "null",
                position != null ? position.name() : "null",
                status.name(),
                organization != null ? organization : "null");
    }
}