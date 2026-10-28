import java.util.Objects;

public sealed class TrainTicket extends Ticket permits SleeperTrainTicket {
    private final int carriage;

    public TrainTicket(int ticketNumber, double price, int place,
                       String from, String to, int carriage) {
        super(ticketNumber, price, place, from, to);
        this.carriage = carriage;
        System.out.println("  [ctor] TrainTicket");
    }

    public TrainTicket(int ticketNumber, double price, int place, String from, String to) {
        this(ticketNumber, price, place, from, to, 1);
    }

    public int getCarriage() { return carriage; }

    @Override
    protected double getServiceFee() { return 15; }

    @Override
    public String toString() { return super.toString() + ", потяг, вагон " + carriage; }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && carriage == ((TrainTicket) obj).carriage;
    }

    @Override
    public int hashCode() { return Objects.hash(super.hashCode(), carriage); }
}
