public abstract class AbstractPercentDiscount implements DiscountStrategy {
    private final double percent;

    protected AbstractPercentDiscount(double percent) { this.percent = percent; }

    @Override
    public double applyDiscount(double price) {
        return price - price * percent / 100.0;
    }
}
