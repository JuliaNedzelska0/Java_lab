package ua.edu.tickets;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tickets")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)   // уся ієрархія в одній таблиці tickets
@DiscriminatorColumn(name = "ticket_type", length = 10)
public abstract class Ticket extends BaseEntity implements Comparable<Ticket>, Payable {

    /** NUMERIC(10,2) в БД відповідає BigDecimal; double для грошей не використовується. */
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "place", nullable = false)
    private int place;

    @Column(name = "city_from", nullable = false, length = 100)
    private String cityOfDeparture;

    @Column(name = "city_to", nullable = false, length = 100)
    private String cityOfArrival;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "status", nullable = false, length = 10)
    private TicketStatus status = TicketStatus.BOOKED;

    /** Власник зв'язку: саме тут стовпець passenger_id (зовнішній ключ). null = квиток нікому не призначений. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;

    /** Спостерігачі це стан у пам'яті, у БД їх немає. */
    @Transient
    private final List<TicketObserver> observers = new ArrayList<>();

    protected Ticket() {}

    protected Ticket(int ticketNumber, double price, int place,
                     String cityOfDeparture, String cityOfArrival) {
        super(ticketNumber, routeName(cityOfDeparture, cityOfArrival));
        if (price <= 0) {
            throw new InvalidPriceException("Ціна має бути більшою за 0", price);
        }
        if (place <= 0) {
            throw new InvalidPlaceException("Номер місця має бути додатним", place);
        }
        this.price = BigDecimal.valueOf(price);
        this.place = place;
        this.cityOfDeparture = cityOfDeparture;
        this.cityOfArrival = cityOfArrival;
    }

    private static String routeName(String from, String to) {
        Objects.requireNonNull(from, "Місто відправлення не може бути null");
        Objects.requireNonNull(to, "Місто призначення не може бути null");
        return from + " → " + to;
    }

    protected abstract double getServiceFee();

    public double getTotalPrice() {
        return getPrice() + getServiceFee();
    }

    public void subscribe(TicketObserver o)   { observers.add(o); }
    public void unsubscribe(TicketObserver o) { observers.remove(o); }

    public void setStatus(TicketStatus newStatus) {
        TicketStatus old = this.status;
        if (old == newStatus) return;
        this.status = newStatus;
        for (TicketObserver o : observers) {
            o.onStatusChanged(this, old, newStatus);
        }
    }

    public TicketStatus getStatus() { return status; }

    public Passenger getPassenger() { return passenger; }

    /** Лише для Passenger.addTicket/removeTicket, щоб обидві сторони зв'язку лишались узгодженими. */
    void setPassenger(Passenger passenger) { this.passenger = passenger; }

    public int getPlace() { return place; }

    public String getCityOfDeparture() { return cityOfDeparture; }

    public String getCityOfArrival() { return cityOfArrival; }

    @Override
    public int compareTo(Ticket other) {
        return Double.compare(getPrice(), other.getPrice());
    }

    @Override
    public String toString() {
        return "Квиток №" + getId() + ", місце " + place + ", " + getName() + ", " + getPrice() + " грн";
    }

    @Override
    public double getPrice() { return price.doubleValue(); }

    public int getTicketNumber() { return (int) getId(); }
}
