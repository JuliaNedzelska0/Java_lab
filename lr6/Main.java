import java.util.List;

public class Main {

    static String describe(Ticket t) {
        return switch (t) {
            case SleeperTrainTicket s -> "спальний вагон " + s.getCarriage();
            case TrainTicket tr       -> "потяг, вагон " + tr.getCarriage();
            case BusTicket b          -> "автобус, платформа " + b.getPlatform();
        };
    }

    public static void main(String[] args) {
        System.out.println("=== 1. Ланцюжок конструкторів ===");
        Ticket sleeper = new SleeperTrainTicket(101, 400, 12, "Чернівці", "Київ", 5, true);

        System.out.println("\n=== 2. Поліморфізм ===");
        List<Ticket> tickets = List.of(
                sleeper,
                new TrainTicket(102, 250, 5, "Львів", "Київ"),
                new BusTicket(103, 180, 7, "Чернівці", "Львів", 3));
        for (Ticket t : tickets) {
            System.out.println(t);
            System.out.println("Разом із збором: " + t.getTotalPrice() + " грн");
        }

        System.out.println("\n=== 3. instanceof ===");
        for (Ticket t : tickets) {
            if (t instanceof TrainTicket tr) {
                System.out.println("№" + t.getTicketNumber() + ": вагон " + tr.getCarriage());
            }
        }

        System.out.println("\n=== 4. switch по sealed ===");
        for (Ticket t : tickets) {
            System.out.println("№" + t.getTicketNumber() + ": " + describe(t));
        }

        System.out.println("\n=== 5. equals ===");
        Ticket train = new TrainTicket(101, 400, 12, "Чернівці", "Київ", 5);
        System.out.println("train.equals(sleeper) = " + train.equals(sleeper));
        System.out.println("sleeper.equals(train) = " + sleeper.equals(train));

        System.out.println("\n=== 6. Знижки (композиція) ===");
        for (String type : new String[]{"none", "student", "pensioner"}) {
            System.out.println(DiscountFactory.create(type).describe(sleeper.getPrice()));
        }

        System.out.println("\n=== 7. Observer ===");
        sleeper.subscribe(new LoggerObserver());
        sleeper.subscribe(new NotificationObserver());
        sleeper.setStatus(TicketStatus.PAID);
        sleeper.setStatus(TicketStatus.CANCELLED);
    }
}
