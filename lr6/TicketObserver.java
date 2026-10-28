public interface TicketObserver {
    void onStatusChanged(Ticket ticket, TicketStatus oldStatus, TicketStatus newStatus);
}
