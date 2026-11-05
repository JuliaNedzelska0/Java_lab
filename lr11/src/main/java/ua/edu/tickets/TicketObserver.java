package ua.edu.tickets;

public interface TicketObserver {
    void onStatusChanged(Ticket ticket, TicketStatus oldStatus, TicketStatus newStatus);
}
