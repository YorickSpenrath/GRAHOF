package ConceptDrift.Timer;

/**
 * Class for handling a sliding movement from 0 to 1 for a given time between 0 and t_e
 */
public abstract class Timer {
    double t_e;

    /**
     * Creates constant timer that starts at 0 and ends at t_e
     *
     * @param t_e End of Gradual Concept Drift
     */
    Timer(double t_e) {
        this.t_e = t_e;
    }

    /**
     * How far the Gradual Speed has progressed
     *
     * @param time time to get the position of the Gradual Concept Drift
     * @return position of the Slider
     */
    public abstract double get_fraction(double time);

    public final void set_t_e(double t_e) {
        this.t_e = t_e;
    }

    public abstract String toString();

}
