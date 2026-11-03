public class NotificationObserver implements TicketObserver {
    @Override
    public void onStatusChanged(Ticket t, TicketStatus oldS, TicketStatus newS) {
        if (newS == TicketStatus.PAID) {
            System.out.println("[SMS] Квиток №" + t.getTicketNumber() + " оплачено. Щасливої дороги!");
        } else if (newS == TicketStatus.CANCELLED) {
            System.out.println("[SMS] Квиток №" + t.getTicketNumber() + " скасовано.");
        }
    }
}
