/**
 * Абстрактний клас частково реалізує інтерфейс: уся відсоткова знижка
 * рахується однаково, тому формула живе тут, а не дублюється в підкласах.
 * Підклас лише задає відсоток і назву.
 */
public abstract class AbstractPercentDiscount implements DiscountStrategy {
    protected final double percent;

    protected AbstractPercentDiscount(double percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("Відсоток знижки має бути від 0 до 100");
        }
        this.percent = percent;
    }

    @Override
    public double applyDiscount(double price) {
        return price * (1 - percent / 100.0);
    }
}
