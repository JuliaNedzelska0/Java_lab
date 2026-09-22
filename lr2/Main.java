import java.util.Scanner;

public class Main{
    public static void main(String[] args) {
        Scanner scanner = new Scanner (System.in);
        
        System.out.print("Введіть кількість квитків: ");
        int n = scanner.nextInt();
        
        Ticket[] ticket = new Ticket[n];
        
        for(int i=0; i < n; i++) {

        System.out.print ("----Заповнення інформації про білет----" + "\n");

        System.out.print ("Введіть номер білета: ");
        int ticketNumber = scanner.nextInt();

        System.out.print ("Введіть ціну білета: ");
        double price = scanner.nextDouble();

        System.out.print ("Введіть кількість місць: ");
        int place = scanner.nextInt();

        scanner.nextLine();

        System.out.print ("Введіть місто відправлення: ");
        String cityOfDeparture = scanner.nextLine();
        
        System.out.print ("Введіть місто прибуття: ");
        String cityOfArrival = scanner.nextLine();

        System.out.print ("----------------------------------" + "\n");

        ticket[i] = new Ticket(ticketNumber, price, place, cityOfDeparture, cityOfArrival);

    }

    System.out.println ("----Список усіх квитків (до сортування)----");
    int expensiveCount = 0;

    for (Ticket t : ticket) {
        System.out.println(t);
         if (t.getPrice() > 300.0){
            expensiveCount++;
         }
    }

    System.out.println("Кількість квитків дорожчих за 300 грн: " + expensiveCount);
    for (int i = 0; i < ticket.length; i++) {
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
            }
            else {
            System.out.println("\nКвитка з номером " + searchNum + " не знайдено.");
            }
    scanner.close();
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
