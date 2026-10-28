public class LoggerObserver implements TicketObserver {
    @Override
    public void onStatusChanged(Ticket t, TicketStatus oldS, TicketStatus newS) {
        System.out.println("[LOG] Квиток №" + t.getTicketNumber() + ": " + oldS + " -> " + newS);
    }
}
