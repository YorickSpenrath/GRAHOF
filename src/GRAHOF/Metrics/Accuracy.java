package GRAHOF.Metrics;

import GRAHOF.Entities.Model;
import ProcessUnits.Case;

import java.util.Collection;

public class Accuracy extends ScoreFunction {

    @Override
    public String name() {
        return "Accuracy";
    }

    @Override
    public double score(Collection<Case> d, Model m) {
        int correct = 0;
        for (Case c : d) {
            String y_pred = m.predict(c);
            if (y_pred == null) {
                continue;
            }
            if (y_pred.equals(c.getTrue_label())) {
                correct++;
            }
        }
        return 1.0 * correct / d.size();
    }

    @Override
    public double score(Collection<Case> cases) {
        int correct = 0;
        for (Case c : cases) {
            String y_pred = c.getPredicted_label();
            if (y_pred == null) {
                continue;
            }
            correct += c.getPredicted_label().equals(c.getTrue_label()) ? 1 : 0;
        }
        return correct * 1.0 / cases.size();
    }
}
