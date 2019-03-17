package Visualisation;

import GRAHOF.Entities.Batch;
import GRAHOF.Entities.Model;
import ProcessUnits.Event;

import java.util.HashMap;

/**
 * Interface for listening to GRAHOF execution
 */

public interface GRAHOF_LISTENER {
    void onEventReceived(Event e);

    void onBatchCloseStart(Batch b);

    void onBatchCloseEnd(Batch b);

    void onModelScoring(HashMap<Model, Double> m_p);

    void onModelUpdate(Model m_star);

    void onNewModel(Model m_star);

    void onUnfitModel(Model m_star);

    void onMemoryPruneStart();

    void onMemoryPruneEnd();
}
