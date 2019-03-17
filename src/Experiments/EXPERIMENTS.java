package Experiments;

import ProcessUnits.SimulationFactory;
import ProcessUnits.SimulationFactoryBPM;

import java.io.FileNotFoundException;
import java.util.ArrayList;


/**
 * This class executes all experiments, by calling an "Experiment Builder" (Experiment Class)
 *
 */
public class EXPERIMENTS {

    public static void run() throws FileNotFoundException {

        //GRAHOF VERIFICATION
        SimulationFactory sf = new SimulationFactoryBPM();
        int reps = 10;
        new Experiment("GRAHOF_S", sf).setS(multiS()).setReps(reps).run(true);
        new Experiment("GRAHOF_PHI", sf).setPhi(generateDoubleAL(0.0, 1.0, 0.05)).setReps(reps).run(true);
        new Experiment("GRAHOF_KAPPA", sf).setKappa(generateIntAL(2, 7, 1)).setReps(reps).run(true);
    }

    public static ArrayList<Double> generateDoubleAL(double start, double stop, double step) {
        ArrayList<Double> ret = new ArrayList<>();
        for (double d = start; d < stop; d += step) {
            ret.add(d);
        }
        return ret;
    }

    public static ArrayList<Integer> generateIntAL(int start, int stop, int step) {
        ArrayList<Integer> ret = new ArrayList<>();
        for (int d = start; d < stop; d += step) {
            ret.add(d);
        }
        return ret;
    }

    public static ArrayList<Integer> multiS() {
        ArrayList<Integer> ret = new ArrayList<>();
        for (int i : new int[]{1, 2, 3, 4, 6, 12}) {
            ret.add(i);
        }
        return ret;
    }


}
