package Algorithms;

import GRAHOF.*;
import GRAHOF.Entities.Batch;

/**
 * Redirector class with all important algorithms
 */

public class Algorithms {

    public static void Assign_Weights_To_BStar(GRAHOF grahof) {
        Model_Weights_BStar.run(grahof);
    }

    public static void Calculate_Bottlenecks(GRAHOF g, Batch b) {
        BottleneckAlgorithm.run(b, g.parameters.d);
    }

    public static void Update_Models(GRAHOF g, Batch b) {
        Model_Update.run(g, b);
    }

}
