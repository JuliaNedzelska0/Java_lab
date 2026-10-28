import java.util.Objects;

/** Третій рівень: Ticket -> TrainTicket -> SleeperTrainTicket. final: підкласів немає. */
public final class SleeperTrainTicket extends TrainTicket {
    private final boolean upperBerth;

    public SleeperTrainTicket(int ticketNumber, double price, int place,
                              String from, String to, int carriage, boolean upperBerth) {
        super(ticketNumber, price, place, from, to, carriage);
        this.upperBerth = upperBerth;
        System.out.println("  [ctor] SleeperTrainTicket(верхня полиця: " + upperBerth + ")");
    }

    public boolean isUpperBerth() { return upperBerth; }

    @Override public String getTransportType() { return "Потяг (спальний)"; }

    @Override protected double getServiceFee() { return super.getServiceFee() + 40; } // + білизна

    @Override
    public String toString() {
        return super.toString() + "\nПолиця: " + (upperBerth ? "верхня" : "нижня");
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        return upperBerth == ((SleeperTrainTicket) obj).upperBerth;
    }

    @Override
    public int hashCode() { return Objects.hash(super.hashCode(), upperBerth); }
}
