package ua.edu.tickets;

import java.util.Comparator;
import java.util.List;

public class InMemoryTicketService extends InMemoryCrudService<Ticket> implements TicketService {

    @Override
    public List<Ticket> findAllSortedByNameAndPrice() {
        return sorted(Comparator.comparing(Ticket::getName, NameOrder.UKRAINIAN)
                .thenComparingDouble(Ticket::getPrice));
    }
}
