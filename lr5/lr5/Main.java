import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws TicketException {
        Ticket ticket = new Ticket(101, 400, 12, "Чернівці", "Київ");

        System.out.println("=== Знижки для білета за 400 грн (Factory) ===");
        String[] types = {"none", "student", "pensioner"};
        for (String type : types) {
            DiscountStrategy d = DiscountFactory.create(type);
            System.out.println(d.describe(ticket.getPrice()));
        }

        System.out.println("\n=== Comparable + Payable ===");
        Ticket[] tickets = {
                ticket,
                new Ticket(102, 250, 5, "Львів", "Київ"),
                new Ticket(103, 600, 7, "Одеса", "Київ")
        };
        Arrays.sort(tickets);
        for (Ticket t : tickets) {
            System.out.println("№" + t.getTicketNumber() + ": " + t.getPrice()
                    + " грн, зі студентською знижкою: "
                    + t.priceWith(DiscountFactory.create("student")) + " грн");
        }

        System.out.println("\n=== Observer: зміна статусу квитка ===");
        ticket.subscribe(new LoggerObserver());
        ticket.subscribe(new NotificationObserver());
        ticket.setStatus(TicketStatus.PAID);
        ticket.setStatus(TicketStatus.CANCELLED);

        System.out.println("\n=== Невідомий тип знижки ===");
        try {
            DiscountFactory.create("studnet");
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка: " + e.getMessage());
        }
    }
}
