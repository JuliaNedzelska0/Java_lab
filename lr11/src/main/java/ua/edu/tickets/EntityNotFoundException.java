package ua.edu.tickets;

public class EntityNotFoundException extends Exception {
    private final long id;

    public EntityNotFoundException(long id) {
        super("Сутність з id=" + id + " не знайдено");
        this.id = id;
    }

    public long getId() { return id; }
}
