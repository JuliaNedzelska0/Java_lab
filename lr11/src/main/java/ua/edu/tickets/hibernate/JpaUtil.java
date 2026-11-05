package ua.edu.tickets.hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import ua.edu.tickets.jdbc.DBConnection;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Singleton: EntityManagerFactory коштовний, його створюють один раз на програму.
 * EntityManager легкий і не потокобезпечний, тому створюється на кожну операцію й закривається.
 * З'єднання береться з пулу DBConnection, тож параметри БД лишаються в application.properties.
 */
public final class JpaUtil {

    private final EntityManagerFactory emf;

    private JpaUtil() {
        Map<String, Object> overrides = new HashMap<>();
        overrides.put("jakarta.persistence.nonJtaDataSource", DBConnection.getInstance().getDataSource());
        this.emf = Persistence.createEntityManagerFactory("ticketsPU", overrides);
    }

    private static final class Holder {
        private static final JpaUtil INSTANCE = new JpaUtil();
    }

    public static JpaUtil getInstance() {
        return Holder.INSTANCE;
    }

    /** Читання без транзакції. Після повернення EntityManager закрито, а сутності стають detached. */
    public <R> R read(Function<EntityManager, R> work) {
        try (EntityManager em = emf.createEntityManager()) {
            return work.apply(em);
        }
    }

    /** Робота в транзакції: commit при успіху, rollback при будь-якому винятку. */
    public <R> R inTransaction(Function<EntityManager, R> work) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            R result = work.apply(em);
            em.getTransaction().commit();
            return result;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void runInTransaction(Consumer<EntityManager> work) {
        inTransaction(em -> {
            work.accept(em);
            return null;
        });
    }

    /** Статистика Hibernate (кількість SQL-запитів тощо). */
    public Statistics statistics() {
        return emf.unwrap(SessionFactory.class).getStatistics();
    }

    public void close() {
        emf.close();
    }
}
