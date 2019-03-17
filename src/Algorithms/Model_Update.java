package Algorithms;

import Visualisation.GRAHOF_LISTENER;
import GRAHOF.Entities.Batch;
import GRAHOF.GRAHOF;
import GRAHOF.Entities.Model;
import GRAHOF.stuff;
import ProcessUnits.Case;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

class Model_Update {

    /**
     * Finding the best Model in GRAHOF for Batch {@code b}
     *
     * @param b the Batch that is used in the update
     */
    static void run(GRAHOF g, Batch b) {
        if (!(b.verify_trainability())) {
            return;
        }


        //Line 1
        ArrayList<Collection<Case>> d = stuff.stratify(b.case_log.cases.values(), 0.80, 0);
        Collection<Case> d_train = d.get(0);
        Collection<Case> d_test = d.get(1);

        //Line 2 (and pre calculation of f(d,m))
        HashMap<Model, Double> M_p = new HashMap<>();
        for (Model m : g.M) {
            M_p.put(m, g.f.score(d_test, m));
        }

        for (GRAHOF_LISTENER l : g.GA) {
            l.onModelScoring(M_p);
        }


        //Line 3
        while (!M_p.isEmpty()) {

            //Line 4
            Model m_star = stuff.argmax(M_p);

            //Line 5
            if (M_p.get(m_star) >= g.parameters.phi * m_star.f_m) {
                for (GRAHOF_LISTENER l : g.GA) {
                    l.onModelUpdate(m_star);
                }
                //Line 6
                m_star.f_m = g.parameters.phi * m_star.f_m + (1 - g.parameters.phi) * M_p.get(m_star);

                //Line 7
                m_star.update(d_train);

                //Line 8
                m_star.addUnits(b.mu_b);


                Double weight = b.model_weights.getOrDefault(m_star, 0.0) / b.model_weights.values().stream().mapToDouble(dub -> dub).sum();
                if (!weight.isNaN()) {
                    for (Integer k : b.mu_b) {
                        g.Weight_to_chosen_model.put(k, weight);
                    }
                }

                //Line 9
                return;
            } else { //Line 10
                //Line 11
                M_p.remove(m_star);
                for (GRAHOF_LISTENER l : g.GA) {
                    l.onUnfitModel(m_star);
                }

            }
        }
        //Line 12
        Model m_star = new Model(g.M.size());
        //Line 13
        m_star.update(d_train);
        //Line 14
        m_star.f_m = g.f.score(d_test, m_star);
        //Line 15
        m_star.addUnits(b.mu_b);
        //Line 16
        g.M.add(m_star);
        for (GRAHOF_LISTENER l : g.GA) {
            l.onNewModel(m_star);
        }

    }
}
