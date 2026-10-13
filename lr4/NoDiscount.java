public class NoDiscount implements DiscountStrategy {
    @Override
    public double applyDiscount(double price) {
        return price;
    }

    @Override
    public String getName() {
        return "Без знижки";
    }
}
