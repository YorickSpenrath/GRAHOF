package ProcessUnits;

import ConceptDrift.Drifter.ConceptDrifter;
import ConceptDrift.Drifter.FullLinearSumConceptDrifter;
import ConceptDrift.Timer.LinearTimer;
import ConceptDrift.Timer.Timer;
import GRAHOF.stuff;

/**
 * This is a meta-generator, generating streams of events
 */
public class SimulationFactory {

    private final int n = stuff.n;

    private int dim_size = 100;
    private int nYears = 57; //This number is a tribute to my life as student.
    private int nCasesMonthTopic = 250;
    private double W = 0.5;
    double shape = 20.0 / n;
    double scale = 0.7;
    double nb_shape_factor = 1;
    double b_shape_factor = n + 1;
    double lce_constant = 0;
    private ConceptDrift.Timer.Timer g = new LinearTimer(this.nYears * 12);
    private ConceptDrifter conceptdrifter = new FullLinearSumConceptDrifter(this.dim_size, this.n, g);

    public EventStreamGenerator generate() {
        return generate(0);
    }

    public EventStreamGenerator generate(int seed) {
        return new EventStreamGenerator(this.dim_size, this.nYears, this.nCasesMonthTopic, this.shape, this.scale, this.conceptdrifter, this.W, seed, this.nb_shape_factor, this.b_shape_factor, this.lce_constant);
    }

}
