package ua.edu.tickets;

import ua.edu.tickets.jdbc.BookingService;
import ua.edu.tickets.jdbc.DBConnection;
import ua.edu.tickets.jdbc.DataAccessException;
import ua.edu.tickets.jdbc.JdbcPassengerService;
import ua.edu.tickets.jdbc.JdbcTicketService;
import ua.edu.tickets.jdbc.SchemaInitializer;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        SchemaInitializer.init();        

        JdbcTicketService ticketDao = new JdbcTicketService();
        JdbcPassengerService passengerDao = new JdbcPassengerService();

        ticketDao.deleteAll();
        passengerDao.deleteAll();

        try {
            TicketService tickets = ticketDao;
            CRUDService<Passenger> passengers = passengerDao;

            runLab7Demo(tickets, passengers);
            runForeignKeyDemo(ticketDao, passengerDao);
            runTransactionDemo(ticketDao, passengerDao);
            runInjectionDemo(passengerDao);
            runBatchAndPagingDemo(ticketDao, passengerDao);
        } finally {
            ticketDao.deleteAll();
            passengerDao.deleteAll();
            DBConnection.getInstance().close();
        }
    }

    private static void runLab7Demo(TicketService tickets, CRUDService<Passenger> passengers) {
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

        System.out.println("\n=== 5. Сортування (ORDER BY у SQL) ===");
        System.out.println("findAll():");
        print(tickets.findAll());
        System.out.println("findAllSortedByName():");
        print(tickets.findAllSortedByName());
        System.out.println("findAllSortedByNameAndPrice():");
        print(tickets.findAllSortedByNameAndPrice());

        System.out.println("\n=== 6. Той самий інтерфейс CRUDService для Passenger ===");
        passengers.save(new Passenger(1, "Петренко Олена", 34));
        passengers.save(new Passenger(2, "Іваненко Андрій", 20));
        passengers.save(new Passenger(3, "Єрмоленко Ірина", 67));
        passengers.save(new Passenger(2, "Іваненко Андрій", 21));
        System.out.println("Записів: " + passengers.findAll().size() + " (мало бути 3)");
        System.out.println("За іменем:");
        print(passengers.findAllSortedByName());

        System.out.println("\n=== 7. save(null) ===");
        try {
            passengers.save(null);
        } catch (NullPointerException e) {
            System.out.println("NPE: " + e.getMessage());
        }
    }

    private static void runForeignKeyDemo(JdbcTicketService ticketDao, JdbcPassengerService passengerDao) {
        System.out.println("\n=== 8. Зовнішній ключ tickets.passenger_id -> passengers.id (ON DELETE RESTRICT) ===");
        Ticket ticket = new TrainTicket(102, 270, 5, "Львів", "Київ");
        ticket.setPassengerId(1L);
        ticketDao.save(ticket);
        System.out.println("Квитки пасажира №1: " + ticketDao.findByPassengerId(1));

        try {
            ticketDao.save(withPassenger(new BusTicket(105, 150, 1, "Київ", "Одеса"), 777L));
        } catch (DataAccessException e) {
            System.out.println("Квиток неіснуючого пасажира №777 відхилено: " + rootMessage(e));
        }
        try {
            passengerDao.delete(1);
        } catch (DataAccessException e) {
            System.out.println("delete(пасажир №1) заборонено, бо є квитки: " + rootMessage(e));
        }
        ticketDao.delete(102);
        System.out.println("Після видалення квитка delete(пасажир №1) = " + passengerDao.delete(1));
        passengerDao.save(new Passenger(1, "Петренко Олена", 34));
    }

    private static void runTransactionDemo(JdbcTicketService ticketDao, JdbcPassengerService passengerDao) {
        System.out.println("\n=== 9. Транзакція: пасажир + квитки (commit / rollback) ===");
        BookingService booking = new BookingService(passengerDao, ticketDao);

        booking.registerPassengerWithTickets(new Passenger(10, "Коваленко Марія", 29), List.of(
                new TrainTicket(201, 320, 21, "Київ", "Харків"),
                new BusTicket(202, 140, 4, "Київ", "Житомир")));
        System.out.println("COMMIT: пасажир №10 і його квитки: " + ticketDao.findByPassengerId(10).size() + " шт.");

        long before = passengerDao.count();
        try {
            booking.registerPassengerWithTickets(new Passenger(11, "Шевчук Тарас", 41), List.of(
                    new TrainTicket(203, 320, 22, "Київ", "Харків"),
                    new TrainTicket(204, 320, 21, "Київ", "Харків")));
        } catch (DataAccessException e) {
            System.out.println("ROLLBACK: " + rootMessage(e));
        }
        System.out.println("Пасажирів до: " + before + ", після: " + passengerDao.count()
                + " (пасажир №11 не збережений)");
        try {
            ticketDao.findById(203);
        } catch (EntityNotFoundException e) {
            System.out.println("Квиток №203, вставлений до помилки, теж відкотився: " + e.getMessage());
        }
    }

    private static void runInjectionDemo(JdbcPassengerService passengerDao) {
        System.out.println("\n=== 10. SQL-ін'єкція проти PreparedStatement ===");
        String attack = "x' OR '1'='1";
        System.out.println("Рядок атаки: " + attack);
        List<Passenger> found = passengerDao.findByName(attack);
        System.out.println("findByName (PreparedStatement) знайшов: " + found.size() + " записів");
        System.out.println("Для порівняння, звичайний пошук: "
                + passengerDao.findByName("Петренко Олена").size() + " запис");
    }

    private static void runBatchAndPagingDemo(JdbcTicketService ticketDao, JdbcPassengerService passengerDao) {
        System.out.println("\n=== 11. Пакетна вставка проти вставки по одному ===");
        final int n = 5000;
        ticketDao.deleteAll();    
        passengerDao.deleteAll();
        List<Passenger> batch1 = generate(1, n);
        List<Passenger> batch2 = generate(n + 1, n);

        long t0 = System.nanoTime();
        for (Passenger p : batch1) {
            passengerDao.insert(p);
        }
        long oneByOneMs = (System.nanoTime() - t0) / 1_000_000;

        long t1 = System.nanoTime();
        passengerDao.insertAllBatch(batch2, 1000);
        long batchMs = (System.nanoTime() - t1) / 1_000_000;

        System.out.println(n + " записів по одному (окреме з'єднання й commit на кожен): " + oneByOneMs + " мс");
        System.out.println(n + " записів пакетом (addBatch/executeBatch по 1000, один commit): " + batchMs + " мс");
        System.out.printf("Пакетна вставка швидша в %.1f раза%n", (double) oneByOneMs / Math.max(1, batchMs));

        System.out.println("\n=== 12. Пагінація (LIMIT/OFFSET) ===");
        System.out.println("Усього записів: " + passengerDao.count());
        for (int page : new int[]{0, 1, 1999}) {
            List<Passenger> p = passengerDao.findPage(page, 5);
            System.out.println("Сторінка " + page + " (розмір 5): id " + p.get(0).getId() + ".." + p.get(p.size() - 1).getId());
        }
    }

    private static List<Passenger> generate(int firstId, int count) {
        List<Passenger> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(new Passenger(firstId + i, "Пасажир " + (firstId + i), 18 + (i % 60)));
        }
        return list;
    }

    private static Ticket withPassenger(Ticket t, long passengerId) {
        t.setPassengerId(passengerId);
        return t;
    }

    private static String rootMessage(Throwable e) {
        Throwable cur = e;
        while (cur.getCause() != null) {
            cur = cur.getCause();
        }
        String msg = String.valueOf(cur.getMessage());
        int q = msg.indexOf('"');
        if (q < 0) {
            return msg;
        }
        String rest = msg.substring(q + 1);
        int end = 0;
        while (end < rest.length() && rest.charAt(end) != ':' && rest.charAt(end) != ' ') {
            end++;
        }
        return msg.substring(0, q).trim().replaceAll(":$", "") + " (" + rest.substring(0, end) + ")";
    }

    private static void print(List<?> items) {
        for (Object e : items) {
            System.out.println("  " + e);
        }
    }
}
