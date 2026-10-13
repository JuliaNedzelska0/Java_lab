/** Ticket реалізує два інтерфейси: Comparable (сортування) і Payable (бізнес-поведінка). */
public class Ticket implements Comparable<Ticket>, Payable {
    private int ticketNumber;
    private double price;
    private int place;
    private String cityOfDeparture;
    private String cityOfArrival;

    public Ticket(int ticketNumber, double price, int place, String cityOfDeparture, String cityOfArrival)
            throws TicketException {
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
    }

    @Override
    public int compareTo(Ticket other) {
        return Double.compare(this.price, other.price);
    }

    @Override
    public String toString() {
        return "----------------------------------" + "\n" +
                "Квиток: №" + ticketNumber + "\n" +
                "Місце: " + place + "\n" +
                "Місто відправлення: " + cityOfDeparture + "\n" +
                "Місто прибуття: " + cityOfArrival + "\n" +
                "Ціна: " + price + "грн" + "\n" +
                "----------------------------------";
    }

    @Override
    public double getPrice() {
        return price;
    }

    public int getTicketNumber() {
        return ticketNumber;
    }

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
        return java.util.Objects.hash(ticketNumber, price, place,
                cityOfDeparture.toLowerCase(), cityOfArrival.toLowerCase());
    }
}
