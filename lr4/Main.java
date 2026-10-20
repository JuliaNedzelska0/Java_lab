import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws TicketException {
        Ticket ticket = new Ticket(101, 400, 12, "Чернівці", "Київ");

        DiscountStrategy[] discounts = {
                new NoDiscount(),
                new StudentDiscount(),
                new PensionerDiscount()
        };

        System.out.println("=== Знижки для білета за 400 грн ===");
        for (DiscountStrategy d : discounts) {
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
                    + " грн, зі студентською знижкою: " + t.priceWith(new StudentDiscount()) + " грн"); // Payable
        }
    }
}
