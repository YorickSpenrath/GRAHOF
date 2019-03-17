package ConceptDrift.Drifter;

import ConceptDrift.Timer.Timer;

public class FullLinearSumConceptDrifter extends ConceptDrifter {

    public final double a_0;
    private final double f;
    public final double condition_h1;

    /**
     * Saves necessary parameters for any Concept Drifter
     *
     * @param dim_size          maximum number of Pages and Publications
     * @param number_activities Number of activities (to ensure the short Cases cover 1/n_acts of the total space)
     * @param t                 timer to handle sliding
     */
    public FullLinearSumConceptDrifter(int dim_size, int number_activities, Timer t) {
        super(dim_size, number_activities, t);
        this.a_0 = (2 - Math.sqrt(2.0 / (n + 1))) * L;
        this.condition_h1 = L * Math.sqrt(1 - (2.0 / (n + 1)));
        this.f = 2.0 * L * L / (n + 1);

    }

    public double l(double time) {
        return a_0 * this.tf(time);
    }

    public double h(double time) {
        if (this.tf(time) >= 1.0) {
            return 2 * this.L;
        }
        double lt = this.l(time);
        if (lt <= condition_h1) {
            return Math.sqrt(this.f + lt * lt);
        } else if (lt <= L) {
            return 2 * L - Math.sqrt(2 * L * L - this.f - lt * lt);
        } else {
            return 2 * L - Math.sqrt((2 * L - lt) * (2 * L - lt) - this.f);
        }
    }

    @Override
    public boolean is_short(double time, int pub, int pag) {
        int sum = pub + pag;
        return this.l(time) <= sum && sum <= this.h(time);
    }
}