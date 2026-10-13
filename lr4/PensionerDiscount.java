public class PensionerDiscount extends AbstractPercentDiscount {
    public PensionerDiscount() {
        super(30);
    }

    @Override
    public String getName() {
        return "Знижка для пенсіонерів (-30%)";
    }

    // перевизначення default-методу
    @Override
    public String describe(double price) {
        return "[Пільговий проїзд] " + super.describe(price);
    }
}
