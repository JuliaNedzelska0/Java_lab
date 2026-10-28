public interface DiscountStrategy {

    double applyDiscount(double price);

    String getName();

    default String describe(double price) {
        return getName() + ": " + price + " грн -> " + applyDiscount(price) + " грн";
    }
}
