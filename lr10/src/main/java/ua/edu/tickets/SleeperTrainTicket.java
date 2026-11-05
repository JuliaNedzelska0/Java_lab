package ua.edu.tickets;

public final class SleeperTrainTicket extends TrainTicket {
    private final boolean upperBerth;

    public SleeperTrainTicket(int ticketNumber, double price, int place,
                              String from, String to, int carriage, boolean upperBerth) {
        super(ticketNumber, price, place, from, to, carriage);
        this.upperBerth = upperBerth;
    }

    public boolean isUpperBerth() { return upperBerth; }

    @Override
    protected double getServiceFee() { return super.getServiceFee() + 40; }

    @Override
    public String toString() { return super.toString() + ", спальний, " + (upperBerth ? "верхня" : "нижня") + " полиця"; }
}
