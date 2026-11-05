package ua.edu.tickets;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "passengers")
public class Passenger extends BaseEntity {

    @Column(name = "age", nullable = false)
    private int age;

    /**
     * Зворотний (inverse) бік зв'язку: власник це Ticket.passenger, де лежить зовнішній ключ.
     * cascade PERSIST/MERGE: збереження пасажира зберігає його нові квитки.
     * Без REMOVE і orphanRemoval: квитки це документи, їх не можна втрачати разом із пасажиром.
     */
    @OneToMany(mappedBy = "passenger", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Ticket> tickets = new ArrayList<>();

    protected Passenger() {}

    public Passenger(long id, String fullName, int age) {
        super(id, fullName);
        if (age <= 0) {
            throw new IllegalArgumentException("Вік має бути додатним: " + age);
        }
        this.age = age;
    }

    public int getAge() { return age; }

    public List<Ticket> getTickets() { return Collections.unmodifiableList(tickets); }

    /** Підтримує обидві сторони зв'язку узгодженими. */
    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
        ticket.setPassenger(this);
    }

    public void removeTicket(Ticket ticket) {
        if (tickets.remove(ticket)) {
            ticket.setPassenger(null);
        }
    }

    /** Колекцію квитків навмисно не виводимо: двоспрямований зв'язок дав би нескінченну рекурсію. */
    @Override
    public String toString() { return "Пасажир №" + getId() + ", " + getName() + ", " + age + " р."; }
}
