/**
 * Контракт: як обчислити ціну квитка після знижки.
 * Це "чистий" інтерфейс - у нього немає стану, лише поведінка.
 */
public interface DiscountStrategy {

    /** Повертає ціну після застосування знижки. */
    double applyDiscount(double price);

    /** Назва стратегії для виводу. */
    String getName();

    /** default-метод: усі реалізації успадковують його, але можуть перевизначити. */
    default String describe(double price) {
        return getName() + ": " + price + " грн -> " + applyDiscount(price) + " грн";
    }
}
