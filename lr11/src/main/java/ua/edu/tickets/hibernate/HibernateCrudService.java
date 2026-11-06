package ua.edu.tickets.hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import ua.edu.tickets.BaseEntity;
import ua.edu.tickets.CRUDService;
import ua.edu.tickets.EntityNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class HibernateCrudService<T extends BaseEntity> implements CRUDService<T> {

    protected final JpaUtil jpa = JpaUtil.getInstance();
    private final Class<T> type;

    protected HibernateCrudService(Class<T> type) {
        this.type = type;
    }

    @Override
    public T findById(long id) throws EntityNotFoundException {
        T entity = jpa.read(em -> em.find(type, id));
        if (entity == null) {
            throw new EntityNotFoundException(id);
        }
        return entity;
    }

    @Override
    public T save(T entity) {
        Objects.requireNonNull(entity, "Сутність не може бути null");
        return jpa.inTransaction(em -> em.merge(entity));
    }

    @Override
    public boolean delete(long id) {
        return jpa.inTransaction(em -> {
            T entity = em.find(type, id);
            if (entity == null) {
                return false;
            }
            em.remove(entity);
            return true;
        });
    }

    @Override
    public List<T> findAll() {
        return select("order by e.id", q -> { });
    }

    @Override
    public List<T> findAllSortedByName() {
        return select("order by e.name, e.id", q -> { });
    }

    public List<T> findPage(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("page >= 0 і size > 0");
        }
        return select("order by e.id", q -> {
            q.setFirstResult(page * size);
            q.setMaxResults(size);
        });
    }

    public long count() {
        return jpa.read(em -> em.createQuery("select count(e) from " + entityName() + " e", Long.class)
                .getSingleResult());
    }

    public int deleteAll() {
        return jpa.inTransaction(em -> em.createQuery("delete from " + entityName()).executeUpdate());
    }

    protected List<T> select(String clauses, Consumer<TypedQuery<T>> binder) {
        return jpa.read(em -> {
            TypedQuery<T> query = em.createQuery("select e from " + entityName() + " e " + clauses, type);
            binder.accept(query);
            return query.getResultList();
        });
    }

    private String entityName() {
        return type.getSimpleName();
    }
}
