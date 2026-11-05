package ua.edu.tickets;

import java.util.List;

public interface CRUDService<T extends BaseEntity> {

    T findById(long id) throws EntityNotFoundException;

    T save(T entity);

    boolean delete(long id);

    List<T> findAll();

    List<T> findAllSortedByName();
}
