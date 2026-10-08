import java.util.Scanner;
public class Main {
    public static void main (String[] args){
        Scanner scanner = new Scanner (System.in);

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

        double generalPrice = price * place;

        System.out.println("Інформація про білет:");
        System.out.printf("Номер білета: %d", ticketNumber);
        System.out.printf("Ціна білета: %.2f", price);
        System.out.printf("Кількість місць: %d", place);
        System.out.printf("Місто відправлення: %s", cityOfDeparture);
        System.out.printf("Місто прибуття: %s", cityOfArrival);
        System.out.printf ("Загальна ціна: %.2f", generalPrice);

        scanner.close();

    }
}
