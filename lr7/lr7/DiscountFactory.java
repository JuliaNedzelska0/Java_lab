public class DiscountFactory {

    private DiscountFactory() {}

    public static DiscountStrategy create(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Тип знижки не може бути null");
        }
        switch (type.toLowerCase()) {
            case "none":      return new NoDiscount();
            case "student":   return new StudentDiscount();
            case "pensioner": return new PensionerDiscount();
            default:
                throw new IllegalArgumentException("Невідомий тип знижки: " + type);
        }
    }
}
