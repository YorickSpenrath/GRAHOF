package Experiments;

import Visualisation.GRAHOF_ARTIST;
import GRAHOF.*;
import GRAHOF.Metrics.Accuracy;
import GRAHOF.Metrics.F1;
import GRAHOF.Metrics.ScoreFunction;
import ProcessUnits.EventStream;
import ProcessUnits.SimulationFactory;
import ProcessUnits.SimulationFactoryBPM;

/**
 * Class this class for the visualisation
 */
public class VISUALISATION {

    public static void run() {

        //Create simulation factory
        SimulationFactory factory = new SimulationFactoryBPM();

        //Generate run
        EventStream ES = factory.generate();
        GRAHOF_PARAMETERS SBTP = new GRAHOF_PARAMETERS(3, 12, -1, 0.75, 0.8, -1);
        ScoreFunction[] sf = new ScoreFunction[]{new F1(), new Accuracy()};
        GRAHOF grahof = new GRAHOF(ES, SBTP, sf);

        //Artistic side
        GRAHOF_ARTIST ga = new GRAHOF_ARTIST(grahof);

        grahof.addListener(ga.get_listener());

        //Time run
        grahof.run();

    }
}