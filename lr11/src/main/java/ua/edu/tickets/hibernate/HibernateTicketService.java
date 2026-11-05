package ua.edu.tickets.hibernate;

import ua.edu.tickets.Ticket;
import ua.edu.tickets.TicketService;

import java.util.List;

public class HibernateTicketService extends HibernateCrudService<Ticket> implements TicketService {

    public HibernateTicketService() {
        super(Ticket.class);
    }

    @Override
    public List<Ticket> findAllSortedByNameAndPrice() {
        return select("order by e.name, e.price, e.id", q -> { });
    }

    /** Пошук за значенням поля з іменованим параметром. */
    public List<Ticket> findByCityOfDeparture(String city) {
        return select("where e.cityOfDeparture = :city order by e.id", q -> q.setParameter("city", city));
    }
}
