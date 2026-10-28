public class PensionerDiscount extends AbstractPercentDiscount {
    public PensionerDiscount() { super(30); }
    @Override public String getName() { return "Пенсіонерська знижка"; }
}
