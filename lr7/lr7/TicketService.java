import java.util.List;

public interface TicketService extends CRUDService<Ticket> {
    /** Спочатку за назвою (маршрут), при збігу назв за ціною. */
    List<Ticket> findAllSortedByNameAndPrice();
}
