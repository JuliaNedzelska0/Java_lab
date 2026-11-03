import java.util.List;

public interface CRUDService<T extends BaseEntity> {

    /** Кидає перевірений виняток, якщо сутності з таким id немає. */
    T findById(long id) throws EntityNotFoundException;

    /** Додає нову сутність або замінює наявну з тим самим id. Повертає збережену сутність. */
    T save(T entity);

    /** @return true, якщо сутність була і її видалено; false, якщо такого id не було */
    boolean delete(long id);

    /** Копія вмісту сховища в порядку додавання. */
    List<T> findAll();

    /** Нова відсортована за назвою колекція; сховище не змінюється. */
    List<T> findAllSortedByName();
}
