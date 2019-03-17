package ProcessUnits;

/**
 * This is the simulation factory for the BPM paper
 */
public class SimulationFactoryBPM extends SimulationFactory {

    public SimulationFactoryBPM() {
        this.scale = 1;
        this.shape = 16;
        this.lce_constant = 10.0;
        this.b_shape_factor = 2;
        this.nb_shape_factor = 0.25;
    }
}
