package Experiments;

import GRAHOF.*;
import GRAHOF.Metrics.Accuracy;
import GRAHOF.Metrics.F1;
import GRAHOF.Metrics.ScoreFunction;
import ProcessUnits.EventStream;
import ProcessUnits.SimulationFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Experiment builder class. Has certain base values (static variables defaultX). Each value can be set
 * to either a single of the same type, or a collection of the type. Requires a simulation factory and a name
 */

public class Experiment {

    public static int defaultS = 3;
    public static int defaultRho = 12;
    public static Integer defaultKappa = -1;
    public static double defaultD = 0.8;
    public static Integer defaultE = -1;
    public static double defaultPhi = 0.75;
    public static int defaultReps = 30;

    private final String name;
    private final ArrayList<Integer> S_values = new ArrayList<>();
    private final ArrayList<Integer> rho_values = new ArrayList<>();
    private final ArrayList<Integer> kappa_values = new ArrayList<>();
    private final ArrayList<Double> d_values = new ArrayList<>();
    private final ArrayList<Integer> E_values = new ArrayList<>();
    private final ArrayList<Double> phi_values = new ArrayList<>();
    private int repetitions;
    private final SimulationFactory generator;


    public Experiment(String name, SimulationFactory sf) {
        this.name = name;
        this.generator = sf;
        //Initialise to default
        this.setS(defaultS);
        this.setRho(defaultRho);
        this.setKappa(defaultKappa);
        this.setD(defaultD);
        this.setE(defaultE);
        this.setPhi(defaultPhi);
        this.setReps(defaultReps);
    }

    public Experiment setReps(int reps) {
        this.repetitions = reps;
        return this;
    }

    public Experiment setS(int S) {
        this.S_values.clear();
        this.S_values.add(S);
        return this;
    }

    public Experiment setS(Collection<Integer> S) {
        this.S_values.clear();
        this.S_values.addAll(S);
        return this;
    }

    public Experiment setRho(int rho) {
        this.rho_values.clear();
        this.rho_values.add(rho);
        return this;
    }

    public Experiment setRho(Collection<Integer> rho) {
        this.rho_values.clear();
        this.rho_values.addAll(rho);
        return this;
    }

    public Experiment setKappa(int kappa) {
        this.kappa_values.clear();
        this.kappa_values.add(kappa);
        return this;
    }

    public Experiment setKappa(Collection<Integer> kappa) {
        this.kappa_values.clear();
        this.kappa_values.addAll(kappa);
        return this;
    }

    public Experiment setD(double d) {
        this.d_values.clear();
        this.d_values.add(d);
        return this;
    }

    public Experiment setD(Collection<Double> d) {
        this.d_values.clear();
        this.d_values.addAll(d);
        return this;
    }

    public Experiment setPhi(double phi) {
        this.phi_values.clear();
        this.phi_values.add(phi);
        return this;
    }

    public Experiment setPhi(Collection<Double> phi) {
        this.phi_values.clear();
        this.phi_values.addAll(phi);
        return this;
    }

    public Experiment setE(int E) {
        this.E_values.clear();
        this.E_values.add(E);
        return this;
    }

    public Experiment setE(Collection<Integer> E) {
        this.E_values.clear();
        this.E_values.addAll(E);
        return this;
    }

    public void run() throws FileNotFoundException {
        this.run(false);
    }

    /**
     * Runs all experiments (looping over all values of S, rho, kappa, phi, d, E) generating values for
     * F1, Acc, number of model, maximum memory (for each both avg and stddev).
     * Writes all values (input, and output) to file with in folder with {@code this.name}
     *
     * @param overwrite Overwrite the folder (contains a small bug)
     * @throws FileNotFoundException If folder cannot be written to
     */
    public void run(boolean overwrite) throws FileNotFoundException {
        String folder = stuff.base_dir + "/" + this.name;
        if (new File(folder).exists()) {
            if (overwrite) {
                if (!stuff.deleteDirectory(new File(folder))) {
                    throw new IllegalStateException("Could not delete folder... " + this.name);
                }
                if (!(new File(folder).mkdir())) {
                    throw new IllegalStateException("Could not create folder..." + this.name);
                }
            } else {
                throw new IllegalStateException("Folder already exists");
            }
        } else {
            new File(folder).mkdir();
        }

        PrintWriter pw = new PrintWriter(folder + "/ results.csv");
        pw.println("S;p;kappa;phi;d;E;time_avg;time_std;F1_avg;F1_std;Acc_avg;Acc_std;M_avg;M_std;mem_avg;mem_std");

        for (Integer S : this.S_values) {
            for (Integer p : this.rho_values) {
                for (Integer kappa : this.kappa_values) {
                    for (Double phi : this.phi_values) {
                        for (Double d : this.d_values) {
                            for (Integer E : this.E_values) {

                                //Track scores
                                ArrayList<Double> durations = new ArrayList<>();
                                ArrayList<Double> F1s = new ArrayList<>();
                                ArrayList<Double> Accs = new ArrayList<>();
                                ArrayList<Double> Created_Models = new ArrayList<>();
                                ArrayList<Double> Memory = new ArrayList<>();


                                GRAHOF_PARAMETERS param = new GRAHOF_PARAMETERS(S, p, kappa, phi, d, E);
                                System.out.println(param.toCSVString());
                                System.out.print("\t");

                                //Do experiments
                                for (int i = 0; i < this.repetitions; i++) {

                                    ScoreFunction[] SF = new ScoreFunction[]{new F1(), new Accuracy()};
                                    EventStream ES = this.generator.generate(i);
                                    GRAHOF grahof = new GRAHOF(ES, param, SF);
                                    long start = System.currentTimeMillis();
                                    grahof.run();

                                    durations.add((double) (System.currentTimeMillis() - start));
                                    F1s.add(grahof.score_tracker.get(0).get_score());
                                    Accs.add(grahof.score_tracker.get(1).get_score());
                                    Created_Models.add((double) grahof.M.size());
                                    Memory.add((double) grahof.max_used_memory);
                                    System.out.print("X");
                                }
                                System.out.println();

                                pw.print(param.toCSVString());
                                pw.print(";");
                                pw.print(stuff.stringAvgStd(durations));
                                pw.print(";");
                                pw.print(stuff.stringAvgStd(F1s));
                                pw.print(";");
                                pw.print(stuff.stringAvgStd(Accs));
                                pw.print(";");
                                pw.print(stuff.stringAvgStd(Created_Models));
                                pw.print(";");
                                pw.print(stuff.stringAvgStd(Memory));
                                pw.println();
                            }
                        }
                    }
                }
            }
        }
        pw.close();
    }
}
