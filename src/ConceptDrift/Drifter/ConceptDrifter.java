package ConceptDrift.Drifter;

import ConceptDrift.Timer.Timer;

/**
 * Determines which combination of Pages and Publications maps to short or long Cases, such that the area of pages x publications
 * Covered over time is always equal to dim_size ^ 2 / (n_acts + 1), creating balanced classes.
 */
public abstract class ConceptDrifter {
    final int L;
    final int n;
    private final Timer timer;

    /**
     * Saves necessary parameters for any Concept Drifter
     *
     * @param dim_size          maximum number of Pages and Publications
     * @param number_activities Number of activities (to ensure the short Cases cover 1/n_acts of the total space)
     * @param t                 timer to handle sliding
     */
    ConceptDrifter(int dim_size, int number_activities, Timer t) {
        this.L = dim_size;
        this.n = number_activities;
        this.timer = t;
    }

    /**
     * Get the value of the timer
     *
     * @param time at which time to get the value
     * @return value of timer at {@code time}
     */
    final double tf(double time) {
        return this.timer.get_fraction(time);
    }

    /**
     * Determines if the given combination of Publications and Papers results in a short case at the given time
     *
     * @param time for which time to compute
     * @param pub  number of publications
     * @param pag  number of pages
     * @return whether the case is short
     */
    public abstract boolean is_short(double time, int pub, int pag);

    public final int get_L() {
        return this.L;
    }
}