package GRAHOF;

import Visualisation.GRAHOF_LISTENER;
import Algorithms.Algorithms;
import GRAHOF.Entities.Batch;
import GRAHOF.Entities.Model;
import GRAHOF.Metrics.ScoreFunction;
import GRAHOF.Metrics.Score_Tracker;
import ProcessUnits.Case;
import ProcessUnits.Event;
import ProcessUnits.EventStream;
import ProcessUnits.Start_Event;

import java.util.*;

/**
 * The main algorithm of the research. Line numbers refer to the most recent algorithm explanation on github
 */

public class GRAHOF {

    //Parameters
    public final GRAHOF_PARAMETERS parameters;


    /**
     * Stream of events used to read activities
     */
    public final EventStream stream;
    /**
     * Most recent batch
     */
    public Batch b_star;
    /**
     * All not closed batches
     */
    public HashSet<Batch> B;
    /**
     * All models
     */
    public final HashSet<Model> M;
    /**
     * Scoring function used for f_m (F1 in BPM paper)
     */
    public ScoreFunction f;
    /**
     * Time in GRAHOF
     */
    public double time = 0;

    //Measurements
    public final HashSet<Case> missed_predictions = new HashSet<>();
    public final ArrayList<Score_Tracker> score_tracker = new ArrayList<>();
    public final HashMap<Integer, Double> Weight_to_chosen_model = new HashMap<>();
    public final ArrayList<String> force_closed = new ArrayList<>();
    public final HashMap<Integer, Integer> batch_size = new HashMap<>();
    public final ArrayList<GRAHOF_LISTENER> GA = new ArrayList<>();
    public int max_used_memory = 0;

    /**
     * Grahof listener is activated on specific actions in GRAHOF
     *
     * @param GL Grahof Listener to be notified
     */
    public void addListener(GRAHOF_LISTENER GL) {
        if (!this.GA.contains(GL)) {
            this.GA.add(GL);
        }
    }

    /**
     * setting up all measurement instruments, and receiving all info for a run
     *
     * @param eventStream      Stream of Events
     * @param grahofParameters Parameters for the run
     * @param scoreFunctions   first is used as guard for f_m, the others are also tracked
     */
    public GRAHOF(EventStream eventStream, GRAHOF_PARAMETERS grahofParameters, ScoreFunction[] scoreFunctions) {
        this.parameters = grahofParameters;
        this.stream = eventStream;
        this.f = scoreFunctions[0];

        //Set score functions
        for (ScoreFunction s : scoreFunctions) {
            this.score_tracker.add(new Score_Tracker(s));
        }

        //Line 1
        this.M = new HashSet<>();

        //Line 2
        this.b_star = new Batch(0, this.parameters.S - 1);

        //Line 3
        this.B = new HashSet<>();
        this.B.add(b_star);

    }

    public void run() {


        //Line 4
        while (this.stream.hasnext()) {


            if (this.max_used_memory < this.events_in_memory()) {
                this.max_used_memory = this.events_in_memory();
            }


            if (this.parameters.E != -1 && this.events_in_memory() > this.parameters.E) {
                for (GRAHOF_LISTENER l : this.GA) {
                    l.onMemoryPruneStart();
                }

                //Line 6 (Storing as PriorityQueue for Speed)
                PriorityQueue<Case> B_Open = new PriorityQueue<Case>(1, new Comparator<Case>() {
                    @Override
                    public int compare(Case c1, Case c2) {
                        return Double.compare(c1.get_end(), c2.get_end());
                    }
                });
                for (Batch b : this.B) {
                    B_Open.addAll(b.getNonClosedCases());
                }

                //Line 7
                while (this.events_in_memory() > 0.9 * this.parameters.E && !B_Open.isEmpty()) {
                    //Line 8
                    Case c = B_Open.poll();

                    //Line 9
                    Batch b = this.find_bucket(c.cid);

                    //Line 10
                    b.remove(c);

                    //Line 11 is actually done by Line 8 already

                    //Line 12
                    this.force_closed.add(c.cid);

                    //Line 13
                    if (canClose(b)) {
                        //Line 14
                        close(b);
                    }
                }
                for (GRAHOF_LISTENER l : this.GA) {
                    l.onMemoryPruneEnd();
                }
            }

            //Line 15
            Event e = this.stream.next();


            this.time = e.time;
            for (GRAHOF_LISTENER l : this.GA) {
                l.onEventReceived(e);
            }


            //Line 16
            if (this.force_closed.contains(e.cid)) {
                //Line 17
                continue;
            }

            //Line 18
            if (e instanceof Start_Event) {

                //Line 19
                while (this.b_star.t_e <= e.time) {
                    //Line 20
                    for (Batch b : new HashSet<>(this.B)) {
                        //Line 21
                        if (parameters.kappa != -1 && b_star.end_k - b.end_k > parameters.kappa * parameters.S) {
                            //Line 22
                            for (Case c : new HashSet<>(b.getCases())) {
                                //Line 23
                                if (!c.closed) {
                                    //Line 24
                                    b.remove(c);
                                    //Line 25
                                    this.force_closed.add(c.cid);
                                }
                            }
                            //Line 26
                            this.close(b);
                        }
                    }

                    //Line 27
                    this.b_star = new Batch(b_star.end_k + 1, b_star.end_k + this.parameters.S);

                    //Line 28
                    Algorithms.Assign_Weights_To_BStar(this);

                    //Line 29
                    this.B.add(this.b_star);

                }

                //Line 30
                Case c = new Case((Start_Event) e);

                //Line 31
                this.b_star.add(c);

                //Line 32
                Predict(this.b_star, c);

            } else {//Line 33


                //Line 34, 35
                Batch b = this.find_bucket(e.cid);
                Case c = b.add(e);

                //Line 36
                if (this.stream.End(e)) {

                    //Line 37
                    b.close_case(c);

                    //Line 38
                    if (canClose(b)) {
                        //Line 39
                        this.close(b);
                    }
                }
            }
        }

        //Line 39
        for (Batch b : this.B) {

            if (b.isEmpty()) {
                continue;
            }

            //Line 40
            Algorithms.Calculate_Bottlenecks(this, b);

            for (Score_Tracker st : this.score_tracker) {
                st.update(b);
            }
        }
    }

    /**
     * Check if a batch b can close
     *
     * @param b Batch to be checked
     * @return b is not b_star, and there are no unclosed cases in b
     */
    private boolean canClose(Batch b) {
        return this.b_star != b && b.num_unclosed() == 0;
    }


    /**
     * Actual closing of b: running the bottleneck algo, updating / creating a model, removing from B
     *
     * @param b the batch to be closed
     */
    private void close(Batch b) {
        //Closing a Batch means (1) running the algorithm, (2) updating a model, and (3) removing b from B


        for (GRAHOF_LISTENER l : this.GA) {
            l.onBatchCloseStart(b);
        }

        //(1)
        Algorithms.Calculate_Bottlenecks(this, b);

        for (Score_Tracker st : this.score_tracker) {
            st.update(b);
        }

        for (int k : b.mu_b) {
            this.batch_size.put(k, b.getCases(k).size());
        }

        //(2)
        Algorithms.Update_Models(this, b);

        //(3)
        this.B.remove(b);

        for (GRAHOF_LISTENER l : this.GA) {
            l.onBatchCloseEnd(b);
        }

    }

    /**
     * Make a prediction for case {@code c} in batch {@code b}, and set it to the case.
     * Verifies that the batch indeed contains the case. If no weights have been set, the predicted label is null
     *
     * @param b the batch (for model weights)
     * @param c the case (for features)
     */
    private void Predict(Batch b, Case c) {
        if (!b.case_log.has_case(c.cid)) {
            throw new IllegalStateException("Cannot make prediction, given case not in bucket");
        }
        if (b.model_weights.isEmpty()) {
            c.setPredicted_label(null);
            return;
        }

        double[] probabilities = new double[c.getData().classAttribute().numValues()];

        for (Model p : b.model_weights.keySet()) {
            double[] prediction = p.predict_probabilities(c);
            if (prediction == null) {
                this.missed_predictions.add(c);
                c.setPredicted_label(null);
                return;
            } else {
                for (int i = 0; i < prediction.length; i++) {
                    probabilities[i] += b.model_weights.get(p) * prediction[i];
                }
            }
        }
        c.setPredicted_label(c.getData().classAttribute().value(stuff.argmax(probabilities)));
    }

    /**
     * Find the batch containing the case id
     *
     * @param cid case id of the case to be found
     * @return b : \exists c \in b  : c.cid = cid
     * @throws IllegalStateException if no Batch has the case
     */
    private Batch find_bucket(String cid) {

        for (Batch b : this.B) {
            if (b.has_case(cid)) {
                return b;
            }
        }
        throw new IllegalStateException("Case not contained in any bucket");
    }

    /**
     * Returns all batches sorted on increasing end-time
     *
     * @return B, sorted on end-time
     */
    public ArrayList<Batch> get_sorted_batches() {
        ArrayList<Batch> ret = new ArrayList<>(this.B);
        ret.sort(Comparator.comparingDouble(b -> b.t_e));
        return ret;
    }

    /**
     * Return the stream
     *
     * @return the stream read by GRAHOF
     */
    public EventStream get_stream() {
        return this.stream;
    }

    /**
     * Total number of events in memory
     *
     * @return SUM _{b\in B} |{e \in B}|
     */
    public int events_in_memory() {
        return this.B.stream().mapToInt(Batch::numEvents).sum();
    }
}