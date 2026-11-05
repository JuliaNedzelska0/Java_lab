package ua.edu.tickets.hibernate;

import org.hibernate.LazyInitializationException;
import org.hibernate.stat.Statistics;
import ua.edu.tickets.EntityNotFoundException;
import ua.edu.tickets.Passenger;

import java.util.List;

/** Відтворення двох типових проблем Hibernate і їх виправлення. */
public final class ProblemsDemo {

    private ProblemsDemo() {}

    public static void lazyInitialization(HibernatePassengerService service, long passengerId)
            throws EntityNotFoundException {
        System.out.println("--- LazyInitializationException ---");
        Passenger detached = service.findById(passengerId);   // EntityManager уже закрито
        try {
            System.out.println("Квитків: " + detached.getTickets().size());
        } catch (LazyInitializationException e) {
            System.out.println("Помилка: " + e.getMessage());
        }
        Passenger fixed = service.findByIdWithTickets(passengerId);   // виправлення: JOIN FETCH
        System.out.println("Після JOIN FETCH квитків: " + fixed.getTickets().size());
    }

    public static void nPlusOne() {
        System.out.println("--- Проблема N+1 ---");
        JpaUtil jpa = JpaUtil.getInstance();
        Statistics stats = jpa.statistics();

        stats.clear();
        int tickets = jpa.read(em -> {
            List<Passenger> all = em.createQuery("select p from Passenger p order by p.id", Passenger.class)
                    .getResultList();
            int total = 0;
            for (Passenger p : all) {
                total += p.getTickets().size();    // на кожного пасажира окремий SELECT
            }
            return total;
        });
        System.out.println("Без виправлення: квитків " + tickets + ", SQL-запитів: " + stats.getPrepareStatementCount());

        stats.clear();
        tickets = jpa.read(em -> {
            List<Passenger> all = em.createQuery(
                    "select distinct p from Passenger p left join fetch p.tickets order by p.id", Passenger.class)
                    .getResultList();
            int total = 0;
            for (Passenger p : all) {
                total += p.getTickets().size();    // колекції вже завантажені
            }
            return total;
        });
        System.out.println("З JOIN FETCH:    квитків " + tickets + ", SQL-запитів: " + stats.getPrepareStatementCount());
    }
}
