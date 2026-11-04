import java.util.List;

public class Main {

    public static void main(String[] args) {
        TicketService tickets = new InMemoryTicketService();

        System.out.println("=== 1. save: додавання ===");
        tickets.save(new SleeperTrainTicket(101, 400, 12, "Чернівці", "Київ", 5, true));
        tickets.save(new TrainTicket(102, 250, 5, "Львів", "Київ"));
        tickets.save(new BusTicket(103, 180, 7, "Чернівці", "Львів", 3));
        tickets.save(new BusTicket(104, 300, 9, "Чернівці", "Київ"));
        System.out.println("Записів: " + tickets.findAll().size());

        System.out.println("\n=== 2. save: оновлення наявного id ===");
        tickets.save(new TrainTicket(102, 270, 5, "Львів", "Київ"));
        System.out.println("Записів: " + tickets.findAll().size() + " (мало лишитись 4)");

        System.out.println("\n=== 3. delete ===");
        System.out.println("delete(103) = " + tickets.delete(103));
        System.out.println("delete(103) = " + tickets.delete(103));
        tickets.save(new BusTicket(103, 180, 7, "Чернівці", "Львів", 3));

        System.out.println("\n=== 4. findById ===");
        try {
            System.out.println("Знайдено: " + tickets.findById(101));
            tickets.findById(999);
        } catch (EntityNotFoundException e) {
            System.out.println("Помилка: " + e.getMessage() + " (id=" + e.getId() + ")");
        }

        System.out.println("\n=== 5. Сортування (сховище до) ===");
        print(tickets.findAll());

        System.out.println("findAllSortedByName():");
        print(tickets.findAllSortedByName());

        System.out.println("findAllSortedByNameAndPrice():");
        print(tickets.findAllSortedByNameAndPrice());

        System.out.println("Сховище після (порядок не змінився):");
        print(tickets.findAll());

        System.out.println("\n=== 6. Той самий InMemoryCrudService для Passenger ===");
        CRUDService<Passenger> passengers = new InMemoryCrudService<>();
        passengers.save(new Passenger(1, "Петренко Олена", 34));
        passengers.save(new Passenger(2, "Іваненко Андрій", 20));
        passengers.save(new Passenger(3, "Єрмоленко Ірина", 67));
        passengers.save(new Passenger(2, "Іваненко Андрій", 21));
        System.out.println("Записів: " + passengers.findAll().size() + " (мало бути 3)");
        System.out.println("За іменем (Collator для uk):");
        print(passengers.findAllSortedByName());

        System.out.println("\n=== 7. save(null) ===");
        try {
            passengers.save(null);
        } catch (NullPointerException e) {
            System.out.println("NPE: " + e.getMessage());
        }

    }

    private static void print(List<?> items) {
        for (Object e : items) {
            System.out.println("  " + e);
        }
    }
}
