package Algorithms;

//The working of this algorithm is extensively discussed in the report and the paper. The lines are references to
//The lines in the report. If I update the algorithm in the report/paper without updating the semantics, I will try and
//Remember to change the lines here.

import GRAHOF.Entities.Batch;
import ProcessUnits.Case;
import GRAHOF.stuff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

class BottleneckAlgorithm {

    static void run(Batch b, double d) {
        HashMap<String, ArrayList<Case>> H = new HashMap<>();
        HashMap<String, ArrayList<Case>> L = new HashMap<>();
        for (Case c : b.getCases()) {
            String variant = c.getTrace();
            H.putIfAbsent(variant, new ArrayList<>());
            L.putIfAbsent(variant, new ArrayList<>());
            if (c.duration() <= d) {
                H.get(variant).add(c);
            } else {
                L.get(variant).add(c);
            }
        }

        //Line 1
        for (String variant : H.keySet()) {
            //Line 2 -> 19
            run(variant, H.get(variant), L.get(variant));
        }//Line 20

    }

    private static void run(String variant, ArrayList<Case> H_i, ArrayList<Case> L_i) {

        if (H_i.size() < 2) {
            return;
        }

        //Line 2
        String[] Sigma = variant.split("_");

        //Line 3
        for (Case c : H_i) {
            //Line 4
            c.setTrue_label(stuff.short_case);
        } //Line 5

        //Preparation for line 6 -> 9
        HashMap<Integer, Double> x_k = new HashMap<>();
        HashMap<Integer, Double> s_k = new HashMap<>();


        //Line 6
        for (int k = 1; k <= Sigma.length - 1; k++) {
            //Line 7
            double xk = 0.0;
            for (Case c : H_i) {
                xk += c.get(k).time - c.get(k - 1).time;
            }
            x_k.put(k, xk / H_i.size());

            //Line 8
            double sk = 0.0;
            for (Case c : H_i) {
                double d = c.get(k).time - c.get(k - 1).time - xk;
                sk += d * d;
            }
            s_k.put(k, Math.sqrt(sk / H_i.size()));
        }//Line 9

        //Line 10
        for (Case c : L_i) {
            //Line 11
            HashSet<Integer> K_0 = new HashSet<>();
            for (int i = 1; i <= Sigma.length - 1; i++) {
                K_0.add(i);
            }

            //Line 12
            HashSet<Integer> K_1 = new HashSet<>();
            for (int k : K_0) {
                if (c.get(k).time - c.get(k - 1).time > x_k.get(k)) {
                    K_1.add(k);
                }
            }

            //Line 13
            HashSet<Integer> K_2 = new HashSet<>();
            for (int k : K_1) {
                if ((c.get(k).time - c.get(k - 1).time) / (c.duration()) > stuff.alpha) {
                    K_2.add(k);
                }
            }

            //Line 14
            if (!K_2.isEmpty()) {
                //Line 15
                int arg_max = -1;
                double max = -1;
                for (int k : K_2) {
                    if ((c.get(k).time - c.get(k - 1).time - x_k.get(k)) / (s_k.get(k)) > max) {
                        arg_max = k;
                    }
                }
                c.setTrue_label(c.get(arg_max).act);
            } else { //Line 16
                //Line 17
                int arg_max = -1;
                double max = -1;
                for (int k : K_1) {
                    if ((c.get(k).time - c.get(k - 1).time) > max) {
                        arg_max = k;
                    }
                }
                c.setTrue_label(c.get(arg_max).act);

            }//Line 18

        }// Line 19
    }
}
