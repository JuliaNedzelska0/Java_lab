package ua.edu.tickets;

public class Passenger extends BaseEntity {
    private final int age;

    public Passenger(long id, String fullName, int age) {
        super(id, fullName);
        if (age <= 0) {
            throw new IllegalArgumentException("Вік має бути додатним: " + age);
        }
        this.age = age;
    }

    public int getAge() { return age; }

    @Override
    public String toString() { return "Пасажир №" + getId() + ", " + getName() + ", " + age + " р."; }
}
