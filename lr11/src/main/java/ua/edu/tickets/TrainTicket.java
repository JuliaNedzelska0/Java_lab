package ua.edu.tickets;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TRAIN")
public class TrainTicket extends Ticket {
    @Column(name = "carriage")
    private int carriage;

    protected TrainTicket() {}

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
