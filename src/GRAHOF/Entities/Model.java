package GRAHOF.Entities;

import GRAHOF.*;
import ProcessUnits.Case;
import moa.classifiers.trees.AdaHoeffdingOptionTree;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class Model {

    public final ArrayList<Integer> mu_train;
    private final AdaHoeffdingOptionTree AHOT;
    public final int model_number;
    public double f_m;

    public Model(int model_number) {
        this.mu_train = new ArrayList<>();
        this.AHOT = new AdaHoeffdingOptionTree();
        this.AHOT.prepareForUse();
        this.model_number = model_number;
    }

    @Override
    public String toString() {
        return "Model " + this.model_number + ", trained on " + this.mu_train.size() + " Units.";
    }

    public double[] predict_probabilities(Case c) {
        return stuff.normalise(AHOT.getVotesForInstance(c.getData()));
    }

    public String predict(Case c) {
        double[] probabilities = predict_probabilities(c);
        if (probabilities == null) {
            return null;
        }
        int i = stuff.argmax(probabilities);
        return c.getData().classAttribute().value(i);
    }

    public void update(Collection<Case> d) {
        for (Case c : d) {
            if (c.getTrue_label() != null) {

                AHOT.trainOnInstance(c.getData());

            } else {
                //System.out.println("Not trained on case with id " + c.cid);
            }
        }
    }

    public void addUnits(HashSet<Integer> mu_b) {
        this.mu_train.addAll(mu_b);
    }
}
