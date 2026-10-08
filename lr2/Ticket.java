public class Ticket {
private int ticketNumber;
private double price;
private int place;
private String cityOfDeparture;
private String cityOfArrival;
public Ticket (int ticketNumber, double price,int place, String cityOfDeparture, String cityOfArrival){
this.ticketNumber = ticketNumber;
this.price = price;
this.place = place;
this.cityOfDeparture = cityOfDeparture;
this.cityOfArrival = cityOfArrival;
}

@Override
public String toString(){
    return "----------------------------------" + "\n" +
    "Квиток: №" + ticketNumber + "\n" +
    "Місце: " + place + "\n" +
    "Місто відправлення: " + cityOfDeparture + "\n" +
    "Місто прибуття: " + cityOfArrival  + "\n" +
    "Ціна: " + price + "грн" + "\n" +
    "----------------------------------";
}

public double getPrice(){
    return price;
}

public int getTicketNumber() {
    return ticketNumber;
}

@Override
public boolean equals (Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;

        Ticket other = (Ticket) obj;

        return ticketNumber == other.ticketNumber &&
        Double.compare(other.price, price) == 0 &&
        place == other.place &&
        cityOfDeparture.equalsIgnoreCase(other.cityOfDeparture) &&
        cityOfArrival.equalsIgnoreCase(other.cityOfArrival);
    }

}
