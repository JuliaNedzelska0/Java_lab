public class InvalidPriceException extends TicketException {
    private final double price;
    public InvalidPriceException(String message, double price) { super(message); this.price = price; }
    public double getPrice() { return price; }
}
