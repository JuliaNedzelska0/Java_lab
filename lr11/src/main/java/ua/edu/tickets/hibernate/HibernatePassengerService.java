package ua.edu.tickets.hibernate;

import ua.edu.tickets.EntityNotFoundException;
import ua.edu.tickets.Passenger;
import ua.edu.tickets.PassengerService;

import java.util.List;

public class HibernatePassengerService extends HibernateCrudService<Passenger> implements PassengerService {

    public HibernatePassengerService() {
        super(Passenger.class);
    }

    @Override
    public List<Passenger> findByName(String name) {
        return select("where e.name = :name order by e.id", q -> q.setParameter("name", name));
    }

    @Override
    public List<Passenger> findAllSortedByNameAndAge() {
        return select("order by e.name, e.age, e.id", q -> { });
    }

    /** JOIN FETCH: пасажир і його квитки одним запитом, колекція ініціалізована до закриття EntityManager. */
    public Passenger findByIdWithTickets(long id) throws EntityNotFoundException {
        List<Passenger> result = jpa.read(em -> em.createQuery(
                        "select p from Passenger p left join fetch p.tickets where p.id = :id", Passenger.class)
                .setParameter("id", id)
                .getResultList());
        if (result.isEmpty()) {
            throw new EntityNotFoundException(id);
        }
        return result.get(0);
    }
}
