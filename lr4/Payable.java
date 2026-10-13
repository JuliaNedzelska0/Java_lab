/** Друга інтерфейсна поведінка Ticket - бізнесова: ціна та ціна зі знижкою. */
public interface Payable {
    double getPrice();

    default double priceWith(DiscountStrategy discount) {
        return discount.applyDiscount(getPrice());
    }
}
