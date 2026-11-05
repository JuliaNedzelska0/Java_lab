package ua.edu.tickets.jdbc;

import ua.edu.tickets.Passenger;
import ua.edu.tickets.Ticket;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BookingService {

    private final JdbcPassengerService passengers;
    private final JdbcTicketService tickets;

    public BookingService(JdbcPassengerService passengers, JdbcTicketService tickets) {
        this.passengers = passengers;
        this.tickets = tickets;
    }

    public void registerPassengerWithTickets(Passenger passenger, List<Ticket> newTickets) {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                passengers.save(conn, passenger);
                for (Ticket ticket : newTickets) {
                    ticket.setPassengerId(passenger.getId());
                    tickets.save(conn, ticket);
                }
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();   // скасовуємо і пасажира, і вже вставлені квитки
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Транзакцію реєстрації пасажира №" + passenger.getId()
                    + " скасовано: " + e.getMessage(), e);
        }
    }
}
