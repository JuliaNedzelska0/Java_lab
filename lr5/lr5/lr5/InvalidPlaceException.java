public class InvalidPlaceException extends TicketException {
    private final int place;
    public InvalidPlaceException(String message, int place) { super(message); this.place = place; }
    public int getPlace() { return place; }
}
