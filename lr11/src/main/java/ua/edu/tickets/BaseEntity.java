package ua.edu.tickets;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.Hibernate;

/**
 * Спільні поля сутностей (id, name). @MappedSuperclass: окремої таблиці немає,
 * стовпці потрапляють у таблиці нащадків.
 */
@MappedSuperclass
public abstract class BaseEntity {

    /** Ключ задається доменом (номер квитка, номер пасажира), тому @GeneratedValue не використовується. */
    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /** Конструктор без параметрів потрібен Hibernate (може бути protected). */
    protected BaseEntity() {}

    protected BaseEntity(long id, String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Ім'я не може бути null або порожнім");
        }
        this.id = id;
        this.name = name;
    }

    public long getId() { return id; }

    public String getName() { return name; }

    /**
     * Рівність за id (бізнес-ключ, що задається до збереження й не змінюється), тому hashCode стабільний.
     * Hibernate.getClass потрібен, бо ліниве посилання може бути проксі-підкласом, і getClass() зламав би порівняння.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || Hibernate.getClass(this) != Hibernate.getClass(obj)) return false;
        return id == ((BaseEntity) obj).getId();
    }

    @Override
    public int hashCode() { return Long.hashCode(id); }
}
