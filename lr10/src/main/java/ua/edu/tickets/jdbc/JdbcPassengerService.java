package ua.edu.tickets.jdbc;

import ua.edu.tickets.Passenger;
import ua.edu.tickets.PassengerService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcPassengerService extends AbstractJdbcCrudService<Passenger> implements PassengerService {

    @Override
    protected String tableName() { return "passengers"; }

    @Override
    protected String insertSql() {
        return "INSERT INTO passengers (name, age, id) VALUES (?, ?, ?)";
    }

    @Override
    protected String updateSql() {
        return "UPDATE passengers SET name = ?, age = ? WHERE id = ?";
    }

    @Override
    protected Passenger mapRow(ResultSet rs) throws SQLException {
        return new Passenger(rs.getLong("id"), rs.getString("name"), rs.getInt("age"));
    }

    @Override
    protected void setParameters(PreparedStatement ps, Passenger p) throws SQLException {
        ps.setString(1, p.getName());
        ps.setInt(2, p.getAge());
        ps.setLong(3, p.getId());
    }

    @Override
    public List<Passenger> findByName(String name) {
        return query("SELECT * FROM passengers WHERE name = ? ORDER BY id", ps -> ps.setString(1, name));
    }

    @Override
    public List<Passenger> findAllSortedByNameAndAge() {
        return query("SELECT * FROM passengers ORDER BY name, age, id", ps -> { });
    }
}
