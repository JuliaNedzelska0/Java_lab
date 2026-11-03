import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract sealed class Ticket extends BaseEntity implements Comparable<Ticket>, Payable
        permits TrainTicket, BusTicket {

    private final double price;
    private final int place;
    private final String cityOfDeparture;
    private final String cityOfArrival;

    private TicketStatus status = TicketStatus.BOOKED;
    private final List<TicketObserver> observers = new ArrayList<>();

    /** id квитка = номер квитка, назва = маршрут. */
    protected Ticket(int ticketNumber, double price, int place,
                     String cityOfDeparture, String cityOfArrival) {
        super(ticketNumber, routeName(cityOfDeparture, cityOfArrival));
        if (price <= 0) {
            throw new InvalidPriceException("Ціна має бути більшою за 0", price);
        }
        if (place <= 0) {
            throw new InvalidPlaceException("Номер місця має бути додатним", place);
        }
        this.price = price;
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

    public final double getTotalPrice() {
        return price + getServiceFee();
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

    @Override
    public int compareTo(Ticket other) {
        return Double.compare(this.price, other.price);
    }

    @Override
    public String toString() {
        return "Квиток №" + getId() + ", місце " + place + ", " + getName() + ", " + price + " грн";
    }

    @Override
    public double getPrice() { return price; }

    public int getTicketNumber() { return (int) getId(); }
}
