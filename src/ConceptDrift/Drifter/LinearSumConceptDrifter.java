package ConceptDrift.Drifter;

import ConceptDrift.Timer.Timer;

/**
 * Generates a trapezoid, two sides lie along the axis, the other two sides are such that the bisector crosses the origin.
 * the distance along the axis to the start of the trapezoid starts at 0 and increases with the timer
 * The distance along the axis to the end of the trapezoid ends at dim_size
 */
public class LinearSumConceptDrifter extends ConceptDrifter {

    private final double a_0;

    public LinearSumConceptDrifter(int dim_size, int number_activities, Timer t) {
        super(dim_size, number_activities, t);
        this.a_0 = Math.sqrt((this.n - 1.0) / (this.n + 1)) * this.L;
    }

    private double inner(double time) {
        return this.a_0 * this.tf(time);
    }

    private double outer(double time) {
        return Math.sqrt(2.0 / (this.n + 1) * this.L * this.L + this.inner(time) * this.inner(time));
    }

    @Override
    public boolean is_short(double time, int pub, int pag) {
        int sum = pub + pag;
        return this.inner(time) <= sum && sum <= this.outer(time);
    }
}