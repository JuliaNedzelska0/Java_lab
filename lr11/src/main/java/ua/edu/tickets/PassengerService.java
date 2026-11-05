package ua.edu.tickets;

import java.util.List;

public interface PassengerService extends CRUDService<Passenger> {

    /** Точний збіг за ПІБ. */
    List<Passenger> findByName(String name);

    List<Passenger> findAllSortedByNameAndAge();
}
