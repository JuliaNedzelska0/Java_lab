// Порушено правило: номер місця має бути додатним
public class InvalidPlaceException extends TicketException {
    private final int invalidPlace;

    public InvalidPlaceException(String message, int invalidPlace) {
        super(message);
        this.invalidPlace = invalidPlace;
    }

    public int getInvalidPlace() {
        return invalidPlace;
    }
}
