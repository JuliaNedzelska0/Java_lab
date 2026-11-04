import java.util.List;

public interface TicketService extends CRUDService<Ticket> {
    List<Ticket> findAllSortedByNameAndPrice();
}
