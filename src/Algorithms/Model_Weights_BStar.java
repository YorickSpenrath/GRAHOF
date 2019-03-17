package Algorithms;

import GRAHOF.*;
import GRAHOF.Entities.Batch;
import GRAHOF.Entities.Model;

import java.util.HashMap;

class Model_Weights_BStar {

    /**
     * Assigns weight to most recent created batch
     * @param g GRAHOF instance
     */

    static void run(GRAHOF g) {
        Batch b = g.b_star;

        b.model_weights.clear();
        if (g.M.isEmpty()) {
            return;
        }

        //Lines 1 - 3
        HashMap<Integer, Double> v_i = new HashMap<>();

        //Line 5
        for (Integer i_p : b.mu_b) {

            //Line 14
            int i_pp = i_p - g.parameters.rho;

            //Line 15
            while (i_pp >= 0) {
                //Line 16
                v_i.put(i_pp, v_i.getOrDefault(i_pp, 0.0) + 1);
                //Line 17
                i_pp -= g.parameters.rho;
            }
        }

        //Add Unit weights to respective models (Line 18)
        //Line 19
        for (Model m : g.M) {
            //Line 20
            double w_k = 0;

            //Line 21
            for (Integer u_i : m.mu_train) {
                //Line 22
                w_k += v_i.getOrDefault(u_i, 0.0);
            }

            //Save result of the algorithm
            b.model_weights.put(m, w_k);

        }
        //Fall-through method
//        if (b.model_weights.values().stream().mapToDouble(d -> d).sum() == 0) {
//            Model k = g.M.stream().max((m1,m2) -> Integer.compare(m1.model_number, m2.model_number)).get();
//            b.model_weights.put(k, 1.0);
//        }
    }
}

