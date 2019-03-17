package GRAHOF.Metrics;

import GRAHOF.Entities.Batch;
import GRAHOF.Entities.Model;
import ProcessUnits.Case;

import java.util.Collection;

public abstract class ScoreFunction {

    public abstract String name();

    final double score(Batch b, Model m) {
        return score(b.case_log.cases.values(), m);
    }

    public abstract double score(Collection<Case> d, Model m);

    public abstract double score(Collection<Case> cases);
}
