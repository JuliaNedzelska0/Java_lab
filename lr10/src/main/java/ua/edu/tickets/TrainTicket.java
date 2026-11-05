package ua.edu.tickets;

public sealed class TrainTicket extends Ticket permits SleeperTrainTicket {
    private final int carriage;

    public TrainTicket(int ticketNumber, double price, int place,
                       String from, String to, int carriage) {
        super(ticketNumber, price, place, from, to);
        this.carriage = carriage;
    }

    public TrainTicket(int ticketNumber, double price, int place, String from, String to) {
        this(ticketNumber, price, place, from, to, 1);
    }

    public int getCarriage() { return carriage; }

    @Override
    protected double getServiceFee() { return 15; }

    @Override
    public String toString() { return super.toString() + ", потяг, вагон " + carriage; }
}
