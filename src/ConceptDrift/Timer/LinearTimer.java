package ConceptDrift.Timer;

/**
 * Creates timer with linear movement
 */
public class LinearTimer extends Timer {

    public LinearTimer(double t_e) {
        super(t_e);
    }

    @Override
    public double get_fraction(double time) {
        return time / this.t_e;
    }

    @Override
    public String toString() {
        return "LIN";
    }
}