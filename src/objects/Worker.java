package objects;

public class Worker implements Comparable<Worker> {
    private final Integer id;
    private final String name;
    private final Coordinates coordinates;
    private final java.util.Date creationDate;
    private final float salary;
    private final java.time.LocalDateTime startDate;
    private final Position position;
    private final Status status;
    private final Organization organization;

    private Worker(WorkerBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.coordinates = builder.coordinates;
        this.creationDate = builder.creationDate;
        this.salary = builder.salary;
        this.startDate = builder.startDate;
        this.position = builder.position;
        this.status = builder.status;
        this.organization = builder.organization;
    }

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

        public WorkerBuilder(Integer id) {
            this.id = id;
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

    @Override
    public String toString() {
        return String.format(
                "Worker: {id: %d; name: %s; coordinates: %s; creation date: %s; salary: %s; start date: %s; position: %s; status: %s; organization: %s}",
                id,
                name,
                coordinates,
                creationDate.toString(),
                salary,
                startDate.toString(),
                position != null ? position.name() : "null",
                status.name(),
                organization != null ? organization : "null");
    }

    @Override
    public int compareTo(Worker other) {
        int nameCompare = this.name.compareTo(other.name);
        if (nameCompare != 0)
            return nameCompare;
        return Integer.compare(this.id, other.id);
    }
}