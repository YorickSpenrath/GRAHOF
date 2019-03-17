package GRAHOF.Metrics;

import GRAHOF.Entities.Model;
import ProcessUnits.Case;

import java.util.Collection;

public class F1 extends ScoreFunction {

    @Override
    public String name() {
        return "F1";
    }

    @Override
    public double score(Collection<Case> d, Model m) {
        if (d.isEmpty()) {
            throw new IllegalArgumentException("Given collection is empty!");
        }
        double score = 0;
        for (String label : d.iterator().next().getData().classAttribute().getAttributeValues()) {
            int TP = 0, FP = 0, FN = 0, total = 0;
            for (Case c : d) {
                String y_pred = m.predict(c);
                if (y_pred == null || c.getTrue_label() == null) {
                    continue;
                }
                if (c.getTrue_label().equals(label)) {
                    total++;
                    if (y_pred.equals(label)) {
                        TP++;
                    } else {
                        FN++;
                    }
                } else {
                    if (y_pred.equals(label)) {
                        FP++;
                    }
                }
            }
            if (TP + FP + FN != 0) {
                score += ((2.0 * TP) / (2.0 * TP + FP + FN)) * total;
            }

        }
        return score / d.size();
    }

    @Override
    public double score(Collection<Case> d) {
        if (d.isEmpty()) {
            throw new IllegalArgumentException("Given collection is empty!");
        }
        double score = 0;
        for (String label : d.iterator().next().getData().classAttribute().getAttributeValues()) {
            int TP = 0, FP = 0, FN = 0, total = 0;
            for (Case c : d) {
                if (c.getPredicted_label() == null || c.getTrue_label() == null) {
                    continue;
                }

                if (c.getTrue_label().equals(label)) {
                    total++;
                    if (c.getPredicted_label().equals(label)) {
                        TP++;
                    } else {
                        FN++;
                    }
                } else {
                    if (c.getPredicted_label().equals(label)) {
                        FP++;
                    }
                }
            }
            if (TP + FP + FN != 0) {
                score += ((2.0 * TP) / (2.0 * TP + FP + FN)) * total;
            }
        }
        double f1 = score / d.size();
        if (f1 > 1) {
            throw new IllegalStateException("This should not happen... F1>1");
        }
        return f1;
    }


}
