package ConceptDrift.Timer;

public class ExponentialTimer extends Timer {

    double c;
    /**
     * Creates constant timer that starts at 0 and ends at t_e
     *
     * @param t_e End of Gradual Concept Drift
     */
    public ExponentialTimer(double t_e, double c) {
        super(t_e);
        if(c >= -1 && c <= 0){
            throw new IllegalArgumentException("C should not be in [-1,0]");
        }
        this.c = c;

    }

    @Override
    public double get_fraction(double time) {
        return c + 1 - (c * c + c) / (time / this.t_e + c);
    }

    @Override
    public String toString() {
        return "EXP(" + this.c + ")";
    }
}
