// Порушено правило: ціна квитка має бути більшою за 0
public class InvalidPriceException extends TicketException {
    private final double invalidPrice;

    public InvalidPriceException(String message, double invalidPrice) {
        super(message);
        this.invalidPrice = invalidPrice;
    }

    public double getInvalidPrice() {
        return invalidPrice;
    }
}
