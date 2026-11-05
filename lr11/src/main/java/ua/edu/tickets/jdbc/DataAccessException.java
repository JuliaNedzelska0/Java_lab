package ua.edu.tickets.jdbc;

/**
 * Неперевірений виняток для збоїв доступу до БД: SQLException обгортається сюди,
 * щоб інтерфейси CRUDService не залежали від JDBC (їхні сигнатури лишились як у ЛР №7).
 */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
