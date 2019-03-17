package ConceptDrift.Timer;

public class ParabolicTimer extends Timer {

    private final boolean cap;

    /**
     * Creates constant timer that starts at 0 and ends at t_e
     *
     * @param t_e End of Gradual Concept Drift
     */
    public ParabolicTimer(double t_e, boolean cap) {
        super(t_e);
        this.cap = cap;
    }

    @Override
    public double get_fraction(double time) {
        return (this.cap ? 0 : 1) + (this.cap ? -1 : 1) * 4 * (time / t_e) * (time / t_e - 1);
    }

    @Override
    public String toString() {
        return this.cap ? "CAP_PARA" : "CUP_PARA";
    }
}
