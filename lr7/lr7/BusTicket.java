public final class BusTicket extends Ticket {
    private final int platform;

    public BusTicket(int ticketNumber, double price, int place,
                     String from, String to, int platform) {
        super(ticketNumber, price, place, from, to);
        this.platform = platform;
    }

    public BusTicket(int ticketNumber, double price, int place, String from, String to) {
        this(ticketNumber, price, place, from, to, 1);
    }

    public int getPlatform() { return platform; }

    @Override
    protected double getServiceFee() { return 5; }

    @Override
    public String toString() { return super.toString() + ", автобус, платформа " + platform; }
}
