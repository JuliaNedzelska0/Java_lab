package ua.edu.tickets;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BUS")
public class BusTicket extends Ticket {
    @Column(name = "platform")
    private int platform;

    protected BusTicket() {}

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
