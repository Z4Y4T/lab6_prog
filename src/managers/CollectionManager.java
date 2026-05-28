package managers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import objects.*;
import utilities.*;

public class CollectionManager {
    private Vector<Worker> workerList;
    private final java.time.LocalDateTime creationDate;

    public CollectionManager() {
        workerList = new Vector<Worker>();
        creationDate = LocalDateTime.now();
    }

    public String add(Worker worker){
        workerList.add(worker);
        return "Element added successfully";
    }

    public String update(Integer id, Worker updated_worker) {
        for (int i = 0; i < workerList.size(); i++) {
            Worker currentWorker = workerList.get(i);
            if (currentWorker.getId().equals(id)) {
                workerList.set(i, updated_worker);
                return "Element with id " + id + " updated successfully";
            }
        }
        return "Element with id " + id + " not found";
    }

    public String remove(Integer id) {
        for (int i = 0; i < workerList.size(); i++) {
            if (workerList.get(i).getId().equals(id)) {
                workerList.remove(i);
                return "Element removed successfully";
            }
        }
        return "Element with id " + id + " not found";
    }

    public String clear() {
        workerList.clear();
        return "WorkerList cleared successfully";
    }

    public String insert(Integer index, Worker inserted_worker) {
        workerList.add(index, inserted_worker);
        return "New element inserted at " + index + " successfully";
    }

    public String addIfMax(Worker worker) {
        if (workerList.size() > 0) {
            for (Worker w : workerList) {
                if (worker.compareTo(w) < 0) {
                    return "Element has not been added (not greatest)";
                }
            }
        }
        workerList.add(worker);
        return "New greatest element added successfully";
    }

    public String removeGreater(Worker killer_worker) {
        Iterator<Worker> iterator = workerList.iterator();
        int removed = 0;

        while (iterator.hasNext()) {
            Worker w = iterator.next();
            if (w.compareTo(killer_worker) > 0) {
                iterator.remove();
                removed++;
            }
        }
        return String.format("Removed %d elements", removed);
    }

    public String maxBySalary() {
        if (workerList.size() > 0) {
            Worker candidate = workerList.get(0);
            for (Worker w : workerList) {
                if (WorkerComparator.BY_SALARY.compare(w, candidate) > 0) {
                    candidate = w;
                }
            }
            return candidate.toString();
        } else {
            return "Worker list is empty";
        }
    }

    public String countGreaterThanStartDate(LocalDateTime date) {
        Integer count = 0;
        for (Worker w : workerList) {
            if (date.compareTo(w.getStartDate()) < 0) {
                count++;
            }
        }
        return String.format("Number of elements: %d", count);
    }

    public String printUniqueStartDate() {
        if (workerList.size() > 0) {
            HashSet<LocalDateTime> uniqueSet = new HashSet<LocalDateTime>();
            for (Worker w : workerList) {
                uniqueSet.add(w.getStartDate());
            }
            for (LocalDateTime date : uniqueSet) {
                System.out.println(date);
            }
            return "Unique start dates printed above";
        }
        return "Worker list is empty";
    }

    public void show() {
        if (workerList.size() > 0) {
            for (Worker w : workerList) {
                System.out.println(w);
            }
        } else {
            System.out.println("Worker list is empty");
        }
    }

    @Override
    public String toString() {
        return String.format("WorkerList: {type: %s; initDate: %s; numberOfElements: %d}",
                workerList.getClass().getSimpleName(),
                creationDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                workerList.size());
    }

    public Vector<Worker> getWorkerList() {
        return workerList;
    }

    public void setWorkers(Vector<Worker> workers) {
        this.workerList = workers;
    }
}