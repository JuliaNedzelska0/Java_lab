public class ExternalSmsGateway {
    public void pushMessage(String text, int priority) {
        System.out.println("[SMS] " + text);
    }
}
