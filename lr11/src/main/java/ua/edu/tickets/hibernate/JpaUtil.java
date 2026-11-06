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

    public <R> R read(Function<EntityManager, R> work) {
        try (EntityManager em = emf.createEntityManager()) {
            return work.apply(em);
        }
    }

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

    public Statistics statistics() {
        return emf.unwrap(SessionFactory.class).getStatistics();
    }

    public void close() {
        emf.close();
    }
}
