package ConceptDrift.Drifter;

import ConceptDrift.Timer.Timer;

/**
 * Generates an arc of PI/4 radians centered at the origin,
 * such that the l radius increases with the timer
 * and the h radius ends at dim_size
 */
public class ArcConceptDrifter extends ConceptDrifter {

    private final double a_0, B;

    ArcConceptDrifter(int dim_size, int number_activities, Timer t) {
        super(dim_size, number_activities, t);
        this.a_0 = (Math.sqrt(1 - 4 / (Math.PI * (this.n + 1)))) * this.L;
        this.B = 4 / (Math.PI * (this.n + 1) * (this.L * this.L));
    }

    private double inner(double time) {
        return this.a_0 * this.tf(time);
    }

    private double outer(double time) {
        double x = this.inner(time);
        return Math.sqrt(this.B + x * x);
    }

    @Override
    public boolean is_short(double time, int pub, int pag) {
        double r = Math.sqrt(pub * pub + pag * pag);
        if (this.inner(time) > r) {
            return false;
        }
        return !(this.outer(time) < r);
    }
}