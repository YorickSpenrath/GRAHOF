package ConceptDrift.Timer;

public class ConstantTimer extends Timer {
    /**
     * Creates timer with no movement, set to a given fraction
     */
    private final double fraction;

    public ConstantTimer(double t_e, double fraction) {
        super(t_e);
        if (fraction < 0 || fraction > 1) {
            throw new IllegalArgumentException("fraction should be in [0,1]");
        }
        this.fraction = fraction;
    }

    @Override
    public double get_fraction(double time) {
        return this.fraction;
    }

    @Override
    public String toString() {
        return "CONST(" + this.fraction + ")";
    }
}
