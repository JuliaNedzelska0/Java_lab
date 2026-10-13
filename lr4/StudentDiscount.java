public class StudentDiscount extends AbstractPercentDiscount {
    public StudentDiscount() {
        super(50);
    }

    @Override
    public String getName() {
        return "Студентська знижка (-50%)";
    }
}
