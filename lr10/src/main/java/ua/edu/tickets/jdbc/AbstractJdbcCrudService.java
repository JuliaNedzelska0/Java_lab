package ua.edu.tickets.jdbc;

import ua.edu.tickets.BaseEntity;
import ua.edu.tickets.CRUDService;
import ua.edu.tickets.EntityNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractJdbcCrudService<T extends BaseEntity> implements CRUDService<T> {

    @FunctionalInterface
    protected interface ParamSetter {
        void set(PreparedStatement ps) throws SQLException;
    }

    protected abstract String tableName();

    protected abstract String insertSql();

    protected abstract String updateSql();

    protected abstract T mapRow(ResultSet rs) throws SQLException;

    protected abstract void setParameters(PreparedStatement ps, T entity) throws SQLException;

    protected String sortedByNameSql() {
        return "SELECT * FROM " + tableName() + " ORDER BY name, id";
    }


    @Override
    public T findById(long id) throws EntityNotFoundException {
        String sql = "SELECT * FROM " + tableName() + " WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("findById(" + id + ") у " + tableName(), e);
        }
        throw new EntityNotFoundException(id);
    }

    @Override
    public T save(T entity) {
        Objects.requireNonNull(entity, "Сутність не може бути null");
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            return save(conn, entity);
        } catch (SQLException e) {
            throw new DataAccessException("save(" + entity.getId() + ") у " + tableName(), e);
        }
    }

    public T save(Connection conn, T entity) throws SQLException {
        Objects.requireNonNull(entity, "Сутність не може бути null");
        try (PreparedStatement update = conn.prepareStatement(updateSql())) {
            setParameters(update, entity);
            if (update.executeUpdate() > 0) {
                return entity;
            }
        }
        try (PreparedStatement insert = conn.prepareStatement(insertSql())) {
            setParameters(insert, entity);
            insert.executeUpdate();
        }
        return entity;
    }

    public void insert(T entity) {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql())) {
            setParameters(ps, entity);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("insert(" + entity.getId() + ") у " + tableName(), e);
        }
    }

    @Override
    public boolean delete(long id) {
        String sql = "DELETE FROM " + tableName() + " WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("delete(" + id + ") у " + tableName(), e);
        }
    }

    public int deleteAll() {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM " + tableName())) {
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("deleteAll() у " + tableName(), e);
        }
    }

    @Override
    public List<T> findAll() {
        return query("SELECT * FROM " + tableName() + " ORDER BY id", ps -> { });
    }

    @Override
    public List<T> findAllSortedByName() {
        return query(sortedByNameSql(), ps -> { });
    }

    public List<T> findPage(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("page >= 0 і size > 0, а отримано page=" + page + ", size=" + size);
        }
        String sql = "SELECT * FROM " + tableName() + " ORDER BY id LIMIT ? OFFSET ?";
        return query(sql, ps -> {
            ps.setInt(1, size);
            ps.setLong(2, (long) page * size);
        });
    }

    public long count() {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM " + tableName());
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new DataAccessException("count() у " + tableName(), e);
        }
    }

    public void insertAllBatch(List<T> entities, int batchSize) {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(insertSql())) {
                int inBatch = 0;
                for (T entity : entities) {
                    setParameters(ps, entity);
                    ps.addBatch();
                    if (++inBatch % batchSize == 0) {
                        ps.executeBatch();
                    }
                }
                ps.executeBatch();
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DataAccessException("insertAllBatch у " + tableName(), e);
        }
    }

    protected List<T> query(String sql, ParamSetter setter) {
        List<T> result = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setter.set(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Помилка запиту до " + tableName(), e);
        }
        return result;
    }
}
