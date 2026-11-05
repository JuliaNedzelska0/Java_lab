package ua.edu.tickets.jdbc;

import ua.edu.tickets.BusTicket;
import ua.edu.tickets.SleeperTrainTicket;
import ua.edu.tickets.Ticket;
import ua.edu.tickets.TicketService;
import ua.edu.tickets.TicketStatus;
import ua.edu.tickets.TrainTicket;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class JdbcTicketService extends AbstractJdbcCrudService<Ticket> implements TicketService {

    private static final String TRAIN = "TRAIN";
    private static final String SLEEPER = "SLEEPER";
    private static final String BUS = "BUS";

    @Override
    protected String tableName() { return "tickets"; }

    @Override
    protected String insertSql() {
        return "INSERT INTO tickets (name, ticket_type, price, place, city_from, city_to, status, "
                + "carriage, platform, upper_berth, passenger_id, id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String updateSql() {
        return "UPDATE tickets SET name = ?, ticket_type = ?, price = ?, place = ?, city_from = ?, city_to = ?, "
                + "status = ?, carriage = ?, platform = ?, upper_berth = ?, passenger_id = ? WHERE id = ?";
    }

    @Override
    protected Ticket mapRow(ResultSet rs) throws SQLException {
        int id = (int) rs.getLong("id");
        double price = rs.getBigDecimal("price").doubleValue();
        int place = rs.getInt("place");
        String from = rs.getString("city_from");
        String to = rs.getString("city_to");
        String type = rs.getString("ticket_type");

        Ticket ticket = switch (type) {
            case BUS -> new BusTicket(id, price, place, from, to, rs.getInt("platform"));
            case TRAIN -> new TrainTicket(id, price, place, from, to, rs.getInt("carriage"));
            case SLEEPER -> new SleeperTrainTicket(id, price, place, from, to,
                    rs.getInt("carriage"), rs.getBoolean("upper_berth"));
            default -> throw new IllegalStateException("Невідомий ticket_type у БД: " + type);
        };
        ticket.setStatus(TicketStatus.valueOf(rs.getString("status")));

        long passengerId = rs.getLong("passenger_id");
        ticket.setPassengerId(rs.wasNull() ? null : passengerId);   // 0 і NULL розрізняємо через wasNull
        return ticket;
    }

    @Override
    protected void setParameters(PreparedStatement ps, Ticket t) throws SQLException {
        String type = BUS;
        Integer carriage = null;
        Integer platform = null;
        Boolean upperBerth = null;
        if (t instanceof SleeperTrainTicket s) {
            type = SLEEPER;
            carriage = s.getCarriage();
            upperBerth = s.isUpperBerth();
        } else if (t instanceof TrainTicket tr) {
            type = TRAIN;
            carriage = tr.getCarriage();
        } else if (t instanceof BusTicket b) {
            platform = b.getPlatform();
        }

        ps.setString(1, t.getName());
        ps.setString(2, type);
        ps.setBigDecimal(3, BigDecimal.valueOf(t.getPrice()));
        ps.setInt(4, t.getPlace());
        ps.setString(5, t.getCityOfDeparture());
        ps.setString(6, t.getCityOfArrival());
        ps.setString(7, t.getStatus().name());
        setNullableInt(ps, 8, carriage);
        setNullableInt(ps, 9, platform);
        if (upperBerth == null) {
            ps.setNull(10, Types.BOOLEAN);
        } else {
            ps.setBoolean(10, upperBerth);
        }
        if (t.getPassengerId() == null) {
            ps.setNull(11, Types.BIGINT);
        } else {
            ps.setLong(11, t.getPassengerId());
        }
        ps.setLong(12, t.getId());
    }

    private static void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
    }

    @Override
    public List<Ticket> findAllSortedByNameAndPrice() {
        return query("SELECT * FROM tickets ORDER BY name, price, id", ps -> { });
    }

    public List<Ticket> findByPassengerId(long passengerId) {
        return query("SELECT * FROM tickets WHERE passenger_id = ? ORDER BY id",
                ps -> ps.setLong(1, passengerId));
    }
}
