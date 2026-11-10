public class NotificationObserver implements TicketObserver {
    private final NotificationSender sender;

    public NotificationObserver(NotificationSender sender) {
        this.sender = sender;
    }

    @Override
    public void onStatusChanged(Ticket t, TicketStatus oldS, TicketStatus newS) {
        if (newS == TicketStatus.PAID) {
            sender.send("Квиток №" + t.getTicketNumber() + " оплачено. Щасливої дороги!");
        } else if (newS == TicketStatus.CANCELLED) {
            sender.send("Квиток №" + t.getTicketNumber() + " скасовано.");
        }
    }
}
