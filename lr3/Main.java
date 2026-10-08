import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    // Читає дані квитка. Помилку валідації логує і прокидає далі (re-throw)
    public static Ticket readTicket(Scanner scanner) throws TicketException {
        System.out.print("----Заповнення інформації про білет----" + "\n");

        System.out.print("Введіть номер білета: ");
        int ticketNumber = scanner.nextInt();

        System.out.print("Введіть ціну білета: ");
        double price = scanner.nextDouble();

        System.out.print("Введіть номер місця: ");
        int place = scanner.nextInt();

        scanner.nextLine();

        System.out.print("Введіть місто відправлення: ");
        String cityOfDeparture = scanner.nextLine();

        System.out.print("Введіть місто прибуття: ");
        String cityOfArrival = scanner.nextLine();

        System.out.print("----------------------------------" + "\n");

        try {
            return new Ticket(ticketNumber, price, place, cityOfDeparture, cityOfArrival);
        } catch (TicketException e) {
            System.out.println("[ЛОГ] Не вдалося створити квиток: " + e.getMessage());
            throw e;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Введіть кількість квитків: ");
            int n = scanner.nextInt();

            Ticket[] ticket = new Ticket[n]; // від'ємне n -> NegativeArraySizeException

            int i = 0;
            while (i < n) {
                try {
                    ticket[i] = readTicket(scanner);
                    i++;
                } catch (InvalidPriceException e) {
                    // специфічний виняток — першим
                    System.out.println("Помилка ціни: " + e.getMessage()
                            + " (введено: " + e.getInvalidPrice() + "). Спробуйте ще раз.");
                } catch (TicketException e) {
                    // базовий клас — перехоплює InvalidPlaceException та інші підтипи
                    System.out.println("Помилка квитка: " + e.getMessage() + ". Спробуйте ще раз.");
                } catch (InputMismatchException e) {
                    System.out.println("Помилка введення: очікувалось число. Спробуйте ще раз.");
                    scanner.nextLine(); // очищаємо некоректний ввід
                }
            }

            System.out.println("----Список усіх квитків (до сортування)----");

            int expensiveCount = 0;

            for (Ticket t : ticket) {
                System.out.println(t);
                if (t.getPrice() > 300.0) {
                    expensiveCount++;
                }
            }
            System.out.println("Кількість квитків дорожчих за 300 грн: " + expensiveCount);

            for (int a = 0; a < ticket.length; a++) {
                for (int j = 0; j < ticket.length - 1; j++) {
                    if (ticket[j].getPrice() > ticket[j + 1].getPrice()) {
                        Ticket temp = ticket[j];
                        ticket[j] = ticket[j + 1];
                        ticket[j + 1] = temp;
                    }
                }
            }

            System.out.println("\n----Список усіх квитків (після сортування)----");
            for (Ticket t : ticket) {
                System.out.println(t);
            }

            System.out.println("\n----Пошук квитка за номером----");
            System.out.print("Введіть номер квитка для пошуку: ");
            int searchNum = scanner.nextInt();

            int foundIndex = findTicketByNumber(ticket, searchNum);
            if (foundIndex != -1) {
                System.out.println("\nКвиток знайдено за індексом: " + foundIndex);
                System.out.println(ticket[foundIndex]);
            } else {
                System.out.println("\nКвитка з номером " + searchNum + " не знайдено.");
            }

        } catch (InputMismatchException e) {
            System.out.println("Помилка введення: очікувалось ціле число.");
        } catch (NegativeArraySizeException e) {
            System.out.println("Кількість квитків не може бути від'ємною.");
        } catch (Exception e) {
            System.out.println("Непередбачена помилка: " + e.getMessage());
        } finally {
            scanner.close();
            System.out.println("Роботу завершено.");
        }
    }

    public static int findTicketByNumber(Ticket[] array, int targetNumber) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].getTicketNumber() == targetNumber) {
                return i;
            }
        }
        return -1;
    }
}
