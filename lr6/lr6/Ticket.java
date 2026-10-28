import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Абстрактний (квиток "взагалі" не існує) і sealed (підкласи закриті переліком)
public abstract sealed class Ticket implements Comparable<Ticket>, Payable
        permits TrainTicket, BusTicket {

    private final int ticketNumber;
    private final double price;
    private final int place;
    private final String cityOfDeparture;
    private final String cityOfArrival;

    private TicketStatus status = TicketStatus.BOOKED;
    private final List<TicketObserver> observers = new ArrayList<>();

    protected Ticket(int ticketNumber, double price, int place,
                     String cityOfDeparture, String cityOfArrival) {
        if (price <= 0) {
            throw new InvalidPriceException("Ціна має бути більшою за 0", price);
        }
        if (place <= 0) {
            throw new InvalidPlaceException("Номер місця має бути додатним", place);
        }
        this.ticketNumber = ticketNumber;
        this.price = price;
        this.place = place;
        this.cityOfDeparture = cityOfDeparture;
        this.cityOfArrival = cityOfArrival;
        System.out.println("  [ctor] Ticket");
    }

    // абстрактний: у кожного виду транспорту свій збір, спільної формули немає
    protected abstract double getServiceFee();

    // final: формула "ціна + збір" однакова для всіх, підкласи її не змінюють
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
        return "Квиток №" + ticketNumber + ", місце " + place + ", "
                + cityOfDeparture + " -> " + cityOfArrival + ", " + price + " грн";
    }

    @Override
    public double getPrice() { return price; }

    public int getTicketNumber() { return ticketNumber; }

    // getClass(): квитки різних класів ніколи не рівні (симетрія зберігається)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ticket other = (Ticket) obj;
        return ticketNumber == other.ticketNumber &&
                Double.compare(other.price, price) == 0 &&
                place == other.place &&
                cityOfDeparture.equalsIgnoreCase(other.cityOfDeparture) &&
                cityOfArrival.equalsIgnoreCase(other.cityOfArrival);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketNumber, price, place,
                cityOfDeparture.toLowerCase(), cityOfArrival.toLowerCase());
    }
}
