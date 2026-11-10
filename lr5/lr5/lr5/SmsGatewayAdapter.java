public class SmsGatewayAdapter implements NotificationSender {
    private static final int DEFAULT_PRIORITY = 1;

    private final ExternalSmsGateway gateway;

    public SmsGatewayAdapter(ExternalSmsGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public void send(String message) {
        gateway.pushMessage(message, DEFAULT_PRIORITY);
    }
}
