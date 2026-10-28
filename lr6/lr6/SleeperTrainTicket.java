import java.util.Objects;

public final class SleeperTrainTicket extends TrainTicket {
    private final boolean upperBerth;

    public SleeperTrainTicket(int ticketNumber, double price, int place,
                              String from, String to, int carriage, boolean upperBerth) {
        super(ticketNumber, price, place, from, to, carriage);
        this.upperBerth = upperBerth;
        System.out.println("  [ctor] SleeperTrainTicket");
    }

    public boolean isUpperBerth() { return upperBerth; }

    @Override
    protected double getServiceFee() { return super.getServiceFee() + 40; } // білизна

    @Override
    public String toString() { return super.toString() + ", спальний, " + (upperBerth ? "верхня" : "нижня") + " полиця"; }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && upperBerth == ((SleeperTrainTicket) obj).upperBerth;
    }

    @Override
    public int hashCode() { return Objects.hash(super.hashCode(), upperBerth); }
}
