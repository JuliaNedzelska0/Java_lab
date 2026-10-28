import java.util.Arrays;
import java.util.List;

public class Main {

    /** Sealed + switch з pattern matching: без default, компілятор перевіряє повноту. */
    static String describe(Ticket t) {
        return switch (t) {
            case SleeperTrainTicket s -> "Спальний вагон " + s.getCarriage()
                    + (s.isUpperBerth() ? ", верхня полиця" : ", нижня полиця");
            case TrainTicket tr -> "Потяг, вагон " + tr.getCarriage();
            case BusTicket b    -> "Автобус, платформа " + b.getPlatform();
            case AirTicket a    -> "Літак, рейс " + a.getFlightNumber();
        };
    }

    public static void main(String[] args) {
        System.out.println("=== 1. Ланцюжок конструкторів (Ticket -> TrainTicket -> SleeperTrainTicket) ===");
        Ticket sleeper = new SleeperTrainTicket(101, 400, 12, "Чернівці", "Київ", 5, true);

        System.out.println("\n=== 2. Поліморфізм: список типу Ticket ===");
        List<Ticket> tickets = List.of(
                sleeper,
                new TrainTicket(102, 250, 5, "Львів", "Київ"),
                new BusTicket(103, 180, 7, "Чернівці", "Львів", 3),
                new AirTicket(104, 1500, 2, "Київ", "Варшава", "PS123", 28));
        System.out.println();
        for (Ticket t : tickets) {
            System.out.println(t);
            System.out.println("Разом із збором: " + t.getTotalPrice() + " грн\n");
        }

        System.out.println("=== 3. instanceof з pattern matching ===");
        for (Ticket t : tickets) {
            if (t instanceof TrainTicket tr) {
                System.out.println("№" + t.getTicketNumber() + ": вагон " + tr.getCarriage());
            }
        }

        System.out.println("\n=== 4. switch по sealed-ієрархії ===");
        for (Ticket t : tickets) {
            System.out.println("№" + t.getTicketNumber() + ": " + describe(t));
        }

        System.out.println("\n=== 5. equals/hashCode та симетрія ===");
        Ticket train = new TrainTicket(101, 400, 12, "Чернівці", "Київ", 5);
        System.out.println("train.equals(sleeper) = " + train.equals(sleeper));
        System.out.println("sleeper.equals(train) = " + sleeper.equals(train));
        Ticket train2 = new TrainTicket(101, 400, 12, "чернівці", "київ", 5);
        System.out.println("train.equals(train2)  = " + train.equals(train2)
                + ", hash збігається: " + (train.hashCode() == train2.hashCode()));

        System.out.println("\n=== 6. Знижки (Factory + Strategy, композиція) ===");
        for (String type : new String[]{"none", "student", "pensioner"}) {
            DiscountStrategy d = DiscountFactory.create(type);
            System.out.println(d.describe(sleeper.getPrice()));
        }

        System.out.println("\n=== 7. Comparable ===");
        Ticket[] arr = tickets.toArray(new Ticket[0]);
        Arrays.sort(arr);
        for (Ticket t : arr) {
            System.out.println("№" + t.getTicketNumber() + ": " + t.getPrice() + " грн, "
                    + "зі студентською знижкою: " + t.priceWith(DiscountFactory.create("student")) + " грн");
        }

        System.out.println("\n=== 8. Observer ===");
        sleeper.subscribe(new LoggerObserver());
        sleeper.subscribe(new NotificationObserver());
        sleeper.setStatus(TicketStatus.PAID);
        sleeper.setStatus(TicketStatus.CANCELLED);

        System.out.println("\n=== 9. Невідомий тип знижки ===");
        try {
            DiscountFactory.create("studnet");
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка: " + e.getMessage());
        }
    }
}
