package ua.edu.tickets;

import ua.edu.tickets.hibernate.HibernatePassengerService;
import ua.edu.tickets.hibernate.HibernateTicketService;
import ua.edu.tickets.hibernate.JpaUtil;
import ua.edu.tickets.hibernate.ProblemsDemo;
import ua.edu.tickets.jdbc.DBConnection;
import ua.edu.tickets.jdbc.SchemaInitializer;

import java.util.List;

public class Main {

    public static void main(String[] args) throws EntityNotFoundException {
        SchemaInitializer.init();          // init.sql з ЛР №10: схему створює він, Hibernate лише перевіряє (validate)

        HibernateTicketService ticketSvc = new HibernateTicketService();
        HibernatePassengerService passengerSvc = new HibernatePassengerService();
        ticketSvc.deleteAll();             // демо починається з чистих таблиць (спершу квитки: зовнішній ключ)
        passengerSvc.deleteAll();

        try {
            // Клієнтський код працює лише з інтерфейсами з ЛР №7. Реалізація обирається лише тут.
            TicketService tickets = ticketSvc;
            CRUDService<Passenger> passengers = passengerSvc;

            runLab7Demo(tickets, passengers);
            runRelationDemo(passengers, tickets, passengerSvc);
            runJpqlDemo(ticketSvc);

            System.out.println("\n=== 10. Проблеми Hibernate ===");
            ProblemsDemo.lazyInitialization(passengerSvc, 10);
            ProblemsDemo.nPlusOne();
        } finally {
            ticketSvc.deleteAll();
            passengerSvc.deleteAll();
            JpaUtil.getInstance().close();
            DBConnection.getInstance().close();
        }
    }

    // ===== Демо з ЛР №7 без змін: сховище тепер Hibernate =====
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

        System.out.println("\n=== 5. Сортування (JPQL ORDER BY) ===");
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

    // ===== Двоспрямований зв'язок Passenger 1 --- N Ticket =====
    private static void runRelationDemo(CRUDService<Passenger> passengers, TicketService tickets,
                                        HibernatePassengerService passengerSvc) throws EntityNotFoundException {
        System.out.println("\n=== 8. Зв'язок один-до-багатьох ===");
        Passenger maria = new Passenger(10, "Коваленко Марія", 29);
        maria.addTicket(new TrainTicket(201, 320, 21, "Київ", "Харків"));   // обидві сторони узгоджені
        maria.addTicket(new BusTicket(202, 140, 4, "Київ", "Житомир"));
        passengers.save(maria);                                          // cascade PERSIST/MERGE зберігає і квитки
        passengers.save(new Passenger(11, "Шевчук Тарас", 41));
        passengers.save(new Passenger(12, "Мельник Оксана", 25));

        Passenger loaded = passengerSvc.findByIdWithTickets(10);
        System.out.println(loaded + ", квитків: " + loaded.getTickets().size());
        loaded.getTickets().forEach(t -> System.out.println("  " + t));

        // Власник зв'язку Ticket: щоб змінити FK, зберігаємо саме квиток
        Ticket first = loaded.getTickets().get(0);
        loaded.removeTicket(first);
        tickets.save(first);
        System.out.println("Після removeTicket квитків: " + passengerSvc.findByIdWithTickets(10).getTickets().size()
                + " (квиток №" + first.getId() + " лишився в БД без власника)");
        Passenger again = passengerSvc.findByIdWithTickets(10);
        again.addTicket(first);
        tickets.save(first);
    }

    // ===== JPQL: пошук і пагінація =====
    private static void runJpqlDemo(HibernateTicketService ticketSvc) {
        System.out.println("\n=== 9. JPQL ===");
        System.out.println("findByCityOfDeparture(\"Чернівці\"):");
        print(ticketSvc.findByCityOfDeparture("Чернівці"));
        System.out.println("findPage(0, 3) і findPage(1, 3), усього " + ticketSvc.count() + ":");
        print(ticketSvc.findPage(0, 3));
        print(ticketSvc.findPage(1, 3));
    }

    private static void print(List<?> items) {
        for (Object e : items) {
            System.out.println("  " + e);
        }
    }
}
