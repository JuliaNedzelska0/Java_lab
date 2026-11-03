import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InMemoryCrudService<T extends BaseEntity> implements CRUDService<T> {

    private final Map<Long, T> store = new LinkedHashMap<>();

    @Override
    public T findById(long id) throws EntityNotFoundException {
        T entity = store.get(id);
        if (entity == null) {
            throw new EntityNotFoundException(id);
        }
        return entity;
    }

    @Override
    public T save(T entity) {
        Objects.requireNonNull(entity, "Сутність не може бути null");
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public boolean delete(long id) {
        return store.remove(id) != null;
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<T> findAllSortedByName() {
        return sorted(Comparator.comparing(BaseEntity::getName, NameOrder.UKRAINIAN));
    }

    /** Сортує копію, сховище не змінюється. */
    protected List<T> sorted(Comparator<? super T> comparator) {
        List<T> copy = new ArrayList<>(store.values());
        copy.sort(comparator);
        return copy;
    }
}
