/**
 * Імітація стороннього SMS-сервісу (чужий код, його не змінюємо).
 * Інтерфейс несумісний з нашим NotificationSender: інша назва методу
 * і обов'язковий додатковий параметр пріоритету.
 */
public class ExternalSmsGateway {
    public void pushMessage(String text, int priority) {
        System.out.println("[SMS] " + text);
    }
}
