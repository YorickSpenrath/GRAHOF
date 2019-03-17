package ConceptDrift.Drifter;

import ConceptDrift.Timer.Timer;

/**
 * Generates a rectangle spanning all publication values and (1/n_act) of the pages values (or vice versa if use_pages is false)
 * The left bound of the pages starts at 0 and increases with the timer
 * The right bound of the pages ends at dim_size
 */

public class LinearConceptDrifter extends ConceptDrifter {

    private final boolean use_pages;
    private final double a_0;

    public LinearConceptDrifter(int dim_size, int number_activities, Timer t, boolean use_pages) {
        super(dim_size, number_activities, t);
        this.use_pages = use_pages;
        this.a_0 = this.n / (this.n + 1.0) * this.L;
    }

    @Override
    public boolean is_short(double time, int pub, int pag) {
        double x = (this.use_pages ? pag : pub) - this.a_0 * this.tf(time);
        return 0 <= x && x <= this.L / (this.n + 1.0);
    }
}