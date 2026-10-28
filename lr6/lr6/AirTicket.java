import java.util.Objects;

public final class AirTicket extends Ticket {
    private static final int FREE_BAGGAGE_KG = 20;

    private final String flightNumber;
    private final int baggageKg;

    public AirTicket(int ticketNumber, double price, int place,
                     String from, String to, String flightNumber, int baggageKg) {
        super(ticketNumber, price, place, from, to);
        this.flightNumber = flightNumber;
        this.baggageKg = baggageKg;
    }

    public AirTicket(int ticketNumber, double price, int place,
                     String from, String to, String flightNumber) {
        this(ticketNumber, price, place, from, to, flightNumber, FREE_BAGGAGE_KG);
    }

    public String getFlightNumber() { return flightNumber; }

    @Override public String getTransportType() { return "Літак"; }

    /** Аеропортовий збір 100 + 10 грн за кожен кг понад безкоштовні 20. */
    @Override
    protected double getServiceFee() {
        return 100 + Math.max(0, baggageKg - FREE_BAGGAGE_KG) * 10;
    }

    @Override
    public String toString() {
        return super.toString() + "\nРейс: " + flightNumber + ", багаж: " + baggageKg + " кг";
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        AirTicket o = (AirTicket) obj;
        return baggageKg == o.baggageKg && flightNumber.equals(o.flightNumber);
    }

    @Override
    public int hashCode() { return Objects.hash(super.hashCode(), flightNumber, baggageKg); }
}
