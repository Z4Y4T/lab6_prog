package server.managers;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import common.domain.Worker;
import server.utilities.IdGenerator;
import server.utilities.WorkerComparator;

/**
 * Менеджер коллекции Worker'ов.
 * 
 * <p>
 * Хранит все элементы коллекции в {@link Vector} и предоставляет методы
 * для их изменения. Каждый метод выполняет одну операцию (добавление, удаление,
 * обновление, фильтрация) и возвращает строку с результатом.
 * </p>
 * 
 * <p>
 * При добавлении нового элемента ему автоматически присваивается id
 * (через {@link IdGenerator#nextId()}) и дата создания (текущее время),
 * если они не были заданы заранее.
 * </p>
 */
public class CollectionManager {
    /** Список всех Worker'ов в коллекции */
    private Vector<Worker> workerList;

    /** Дата и время создания коллекции (инициализации менеджера) */
    private final java.time.LocalDateTime creationDate;

    /**
     * Создаёт пустой менеджер коллекции.
     * Дата создания фиксируется в момент вызова конструктора.
     */
    public CollectionManager() {
        workerList = new Vector<>();
        creationDate = java.time.LocalDateTime.now();
    }

    /**
     * Добавляет нового Worker'а в коллекцию.
     * Присваивает id и дату создания, если они не заданы.
     *
     * @param worker новый Worker
     * @return сообщение об успешном добавлении
     */
    public String add(Worker worker) {
        workerList.add(initializeWorker(worker));
        return "Element added successfully";
    }

    /**
     * Обновляет Worker'а по id.
     * Существующий элемент заменяется новым с сохранением оригинальных id и
     * creationDate.
     *
     * @param id            идентификатор обновляемого элемента
     * @param updatedWorker новые данные Worker'а
     * @return сообщение о результате (успех или "не найден")
     */
    public String update(Integer id, Worker updatedWorker) {
        OptionalInt index = IntStream.range(0, workerList.size())
                .filter(i -> Objects.equals(workerList.get(i).getId(), id))
                .findFirst();

        if (!index.isPresent()) {
            return "Element with id " + id + " not found";
        }

        int i = index.getAsInt();
        Worker existing = workerList.get(i);
        workerList.set(i, buildUpdatedWorker(existing, updatedWorker, id));
        return "Element with id " + id + " updated successfully";
    }

    /**
     * Удаляет Worker'а из коллекции по id.
     *
     * @param id идентификатор удаляемого элемента
     * @return сообщение о результате (успех или "не найден")
     */
    public String remove(Integer id) {
        OptionalInt index = IntStream.range(0, workerList.size())
                .filter(i -> Objects.equals(workerList.get(i).getId(), id))
                .findFirst();

        if (!index.isPresent()) {
            return "Element with id " + id + " not found";
        }

        workerList.remove(index.getAsInt());
        return "Element removed successfully";
    }

    /**
     * Полностью очищает коллекцию.
     *
     * @return сообщение об успешной очистке
     */
    public String clear() {
        workerList.clear();
        return "WorkerList cleared successfully";
    }

    /**
     * Вставляет нового Worker'а на указанную позицию.
     *
     * @param index          индекс для вставки (от 0 до size())
     * @param insertedWorker новый Worker
     * @return сообщение об успешной вставке
     */
    public String insert(Integer index, Worker insertedWorker) {
        workerList.add(index, initializeWorker(insertedWorker));
        return "New element inserted at " + index + " successfully";
    }

    /**
     * Добавляет Worker'а, только если он больше максимального элемента коллекции.
     * Сравнение выполняется методом {@link Worker#compareTo(Worker)} (по имени,
     * затем по id).
     *
     * @param worker кандидат на добавление
     * @return сообщение о результате (добавлен или "не наибольший")
     */
    public String addIfMax(Worker worker) {
        Worker requestWorker = initializeWorker(worker);
        Worker candidate = workerList.stream()
                .max(Worker::compareTo)
                .orElse(null);

        if (candidate != null && requestWorker.compareTo(candidate) < 0) {
            return "Element has not been added (not greatest)";
        }

        workerList.add(requestWorker);
        return "New greatest element added successfully";
    }

    /**
     * Удаляет все элементы, превышающие заданный.
     * Сравнение выполняется методом {@link Worker#compareTo(Worker)}.
     *
     * @param killerWorker эталон для сравнения
     * @return сообщение с количеством удалённых элементов
     */
    public String removeGreater(Worker killerWorker) {
        Worker requestWorker = initializeWorker(killerWorker);
        Vector<Worker> removed = workerList.stream()
                .filter(w -> w.compareTo(requestWorker) > 0)
                .collect(Collectors.toCollection(Vector::new));

        workerList.removeAll(removed);
        return String.format("Removed %d elements", removed.size());
    }

    /**
     * Находит элемент с максимальной зарплатой.
     * Сравнение через {@link WorkerComparator#BY_SALARY}.
     *
     * @return строковое представление Worker'а с максимальной зарплатой,
     *         либо сообщение о пустой коллекции
     */
    public String maxBySalary() {
        return workerList.stream()
                .max(WorkerComparator.BY_SALARY)
                .map(Worker::toString)
                .orElse("Worker list is empty");
    }

    /**
     * Подсчитывает количество элементов с датой начала позже указанной.
     *
     * @param date дата для сравнения
     * @return сообщение с количеством найденных элементов
     */
    public String countGreaterThanStartDate(java.time.LocalDateTime date) {
        long count = workerList.stream()
                .filter(w -> date.compareTo(w.getStartDate()) < 0)
                .count();
        return String.format("Number of elements: %d", count);
    }

    /**
     * Выводит все уникальные значения дат начала работы.
     * Даты собираются в {@link LinkedHashSet} для сохранения порядка и
     * уникальности.
     *
     * @return строку с уникальными датами, разделёнными переносом строки,
     *         либо сообщение о пустой коллекции
     */
    public String printUniqueStartDate() {
        LinkedHashSet<java.time.LocalDateTime> unique = workerList.stream()
                .map(Worker::getStartDate)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (unique.isEmpty()) {
            return "Worker list is empty";
        }

        return unique.stream()
                .map(d -> d.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")))
                .collect(Collectors.joining("\n"));
    }

    /**
     * Возвращает одну страницу коллекции, отсортированную по имени.
     *
     * @param page     номер страницы (от 0)
     * @param pageSize размер страницы
     * @return список Worker'ов указанной страницы
     */
    public List<Worker> getPage(int page, int pageSize) {
        List<Worker> sorted = workerList.stream()
                .sorted(Worker::compareTo)
                .collect(Collectors.toList());
        int fromIndex = page * pageSize;
        if (fromIndex >= sorted.size()) {
            return new Vector<>();
        }
        int toIndex = Math.min(fromIndex + pageSize, sorted.size());
        return new Vector<>(sorted.subList(fromIndex, toIndex));
    }

    /**
     * Возвращает информацию о коллекции: тип, дата инициализации, количество
     * элементов.
     *
     * @return строку с информацией
     */
    @Override
    public String toString() {
        return String.format("WorkerList: {type: %s; initDate: %s; numberOfElements: %d}",
                workerList.getClass().getSimpleName(),
                creationDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")),
                workerList.size());
    }

    /**
     * Возвращает список всех Worker'ов.
     *
     * @return Vector с элементами коллекции
     */
    public Vector<Worker> getWorkerList() {
        return workerList;
    }

    /**
     * Заменяет всю коллекцию новым списком.
     * Используется при загрузке из XML-файла.
     *
     * @param workers новый список Worker'ов
     */
    public void setWorkers(Vector<Worker> workers) {
        this.workerList = workers;
    }

    /**
     * Подготавливает Worker'а к добавлению: присваивает id и дату создания,
     * если они не были заданы.
     *
     * @param worker исходный Worker
     * @return новый Worker с заполненными id и creationDate
     */
    private Worker initializeWorker(Worker worker) {
        if (worker == null) {
            throw new IllegalArgumentException("Worker must not be null");
        }

        Worker.WorkerBuilder builder = new Worker.WorkerBuilder(worker);

        if (worker.getId() == null) {
            builder.id(IdGenerator.nextId());
        }

        if (worker.getCreationDate() == null) {
            builder.creationDate(new Date());
        }

        return builder.build();
    }

    /**
     * Создаёт обновлённого Worker'а: сохраняет оригинальные id и creationDate
     * из существующего элемента, остальные поля берёт из обновлённого.
     *
     * @param existing      существующий Worker
     * @param updatedWorker новые данные
     * @param id            идентификатор (должен совпадать с existing)
     * @return новый Worker с обновлёнными полями
     */
    private Worker buildUpdatedWorker(Worker existing, Worker updatedWorker, Integer id) {
        Worker.WorkerBuilder builder = new Worker.WorkerBuilder(updatedWorker)
                .id(id);

        if (updatedWorker.getCreationDate() == null) {
            builder.creationDate(existing.getCreationDate());
        }

        return builder.build();
    }
}