public interface Payable {
    double getPrice();

    default double priceWith(DiscountStrategy discount) {
        return discount.applyDiscount(getPrice());
    }
}
