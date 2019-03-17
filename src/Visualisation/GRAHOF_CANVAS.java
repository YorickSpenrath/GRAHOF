package Visualisation;

import ConceptDrift.Drifter.ConceptDrifter;
import ConceptDrift.Drifter.FullLinearSumConceptDrifter;
import GRAHOF.*;
import GRAHOF.Entities.Batch;
import GRAHOF.Entities.Model;
import GRAHOF.Metrics.Score_Tracker;
import ProcessUnits.Case;
import ProcessUnits.Event;
import ProcessUnits.EventStream;
import ProcessUnits.EventStreamGenerator;
import Visualisation.Plot.Baseplot;
import Visualisation.Plot.Data_plot;
import Visualisation.Plot.timer_plot;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/*
 * This is the big visualisation class
 */
public class GRAHOF_CANVAS extends JPanel implements GRAHOF_LISTENER, ActionListener, ChangeListener {

    /**
     * Trackers of information over time
     */
    //x-axis
    private final ArrayList<Double> time_points;
    //y-axes (though currently only 1)
    private final ArrayList<Double> total_number_events;
    //Inter-update time
    private final double time_update = stuff.mu * 10;

    //Control speed
    private final JSlider SpeedControl = new JSlider();
    private final int max_step_duration = 1000;

    //GRAHOF that is executed
    private final GRAHOF grahof;
    //Show whenever this number of events is parsed
    private final int event_update_interval = 10000;

    //Slow actions down by this factor (for debugging / finetuning)
    private int speed_modifier = 1;
    private int eventUpdate_modifier = speed_modifier;
    private int batchClose_modifier = speed_modifier;
    private int modelCreation_modifier = speed_modifier;
    private int modelScore_modifier = speed_modifier;
    private int batchCloseEnd_modifier = speed_modifier;
    private int modelUnfit_modifier = speed_modifier;
    private int modelUpdate_modifier = speed_modifier;
    private int memoryPrune_modifier = speed_modifier;


    //Show the pruning of memory (BETA)
    private final boolean show_pruning = false;

    //List of all messages to be shown to user (future: show full list)
    private ArrayList<String> message_list = new ArrayList<>();

    //Total events received
    private int received_events = 0;

    /**
     * Text parameters
     */
    public static Color TEXTCOLOR = Color.BLACK;
    public static int VTS = 12;

    /**
     * Timer for animations.
     */
    private Integer model_update_ticker_step = null;
    private final int animation_steps = 60;

    /**
     * Decimal formatting
     */
    private static DecimalFormat df0 = new DecimalFormat("0");
    private static DecimalFormat df1 = new DecimalFormat("0.0");
    private static DecimalFormat df2 = new DecimalFormat("0.00");
    private static DecimalFormat df3 = new DecimalFormat("0.000");

    /**
     * Model dimensions
     */
    private static int MODELS_X = 450;
    private static int MODELS_Y = 100;
    private static int MODELS_DIM = 25;
    private static Color MODELS_COLOR = Color.BLUE;
    private static Color MODELS_COLOR_UNFIT_TEXT = Color.RED;


    /**
     * Batch dimensions
     */
    private static int BATCHES_X = 0;
    private static int BATCHES_Y = 100;
    private static int BATCHES_DIM = 25;
    private static final Color BATCHES_COLOR_UPDATING = Color.RED;
    private static final Color BATCHES_COLOR_CLOSED = Color.RED;
    private static final Color BATCHES_COLOR = Color.ORANGE;
    private static final Color BATCHES_COLOR_PRUNED = Color.BLACK;


    /**
     * Plot dimensions
     */
    private static int PLOT_X = 750;
    private static int PLOT_Y = 100;
    private static int INTERPLOTSIZE = 15 * Baseplot.PLOTSIZE / 10;

    /**
     * These are set on receiving certain notifications from GRAHOF
     */
    private Batch closingBatch = null;
    private HashMap<Model, Double> model_scores = null;
    private Model updated_model = null;
    private Model new_model = null;
    private HashSet<Model> unfitModels = null;
    private boolean pruning = false;
    private Batch pruned_batch = null;
    private Case pruned_case = null;

    /**
     * Constructor
     *
     * @param g to be visualised grahof execution
     */
    GRAHOF_CANVAS(GRAHOF g) {

        this.grahof = g;

        /*
         * Initialise slider
         */
        this.SpeedControl.addChangeListener(this);
        this.SpeedControl.setMinimum(1);
        this.SpeedControl.setMaximum(this.max_step_duration);
        this.SpeedControl.setValue(this.max_step_duration);
        this.add(this.SpeedControl);

        /*
         * trackers
         */
        total_number_events = new ArrayList<>();
        total_number_events.add(0.0);
        time_points = new ArrayList<>();
        time_points.add(0.0);
    }


    /**
     * Paint current state of GRAHOF
     *
     * @param g
     */
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        //Draw Information on current state
        draw_text(g);

        //Draw plots
        draw_plots(g);

        //Draw Models
        draw_models(g);

        //Draw Batches
        draw_batches(g);


    }

    private void draw_text(Graphics g) {
        g.drawString("Parsed Events = " + this.received_events, 10, 10);
        g.drawString("Time = " + df1.format(this.grahof.time), 10, 10 + VTS);
        g.drawString("Events in memory = " + this.grahof.events_in_memory(), 10, 10 + 2 * VTS);
        if (!this.message_list.isEmpty()) {
            g.drawString("Latest msg: " + this.message_list.get(this.message_list.size() - 1), 10, 10 + 3 * VTS);
        }
    }

    private void draw_plots(Graphics g) {
        for (int i = 0; i < grahof.score_tracker.size(); i++) {
            Score_Tracker st = grahof.score_tracker.get(i);
            ArrayList<Double> x_vals = new ArrayList<>();
            ArrayList<Double> y_vals = new ArrayList<>();
            for (int k : st.get_sorted_keys()) {
                x_vals.add(1.0 * k);
                y_vals.add(st.incremental_score(k));
            }
            new Data_plot(x_vals, y_vals)
                    .set_ylim(0.0, 1.0)
                    .set_title(st.score_function.name() + " = " + df3.format(st.get_score()))
                    .setDF(df0)
                    .show(g, PLOT_X, PLOT_Y + (INTERPLOTSIZE * i));
        }

        int i = grahof.score_tracker.size();

        EventStream S = grahof.get_stream();
        if (S instanceof EventStreamGenerator) {
            ConceptDrifter cd = ((EventStreamGenerator) S).get_cd();
            if (cd instanceof FullLinearSumConceptDrifter) {
                new timer_plot((FullLinearSumConceptDrifter) cd)
                        .show(g, PLOT_X, PLOT_Y + INTERPLOTSIZE * i, grahof.time);
                i++;
            }
        }

        Data_plot plt = new Data_plot(this.time_points, this.total_number_events);
        plt.show(g, PLOT_X, PLOT_Y + (INTERPLOTSIZE) * i);
    }


    private void draw_batches(Graphics g) {

        int step_size = VTS * 3;
        int x = BATCHES_X;
        int y = BATCHES_Y - VTS / 2;
        int d = BATCHES_DIM;

        //Header
        g.setColor(TEXTCOLOR);
        g.drawString("Interval", x + d + step_size, y);
        g.drawString("Open", x + d + step_size * 4, y);
        g.drawString("Total", (int) (x + d + step_size * 5.5), y);
        g.drawString("Events", (int) (x + d + step_size * 7.5), y);


        ArrayList<Batch> batch_al = new ArrayList<>();
        grahof.B.stream().sorted((b1, b2) -> -Double.compare(b1.end_k, b2.end_k)).forEach(batch_al::add);
        for (Batch b : batch_al) {
            int[] xy = get_batch_xy(b);
            x = xy[0];
            y = xy[1];

            if (this.model_update_ticker_step != null && this.closingBatch == b && this.updated_model != null) {
                int[] mxy = this.get_model_xy(this.updated_model);
                x = (int) (x + (mxy[0] - x) * (1.0 * this.model_update_ticker_step / this.animation_steps));
                y = (int) (y + (mxy[1] - y) * (1.0 * this.model_update_ticker_step / this.animation_steps));
                g.setColor(BATCHES_COLOR_UPDATING);


            } else {

                g.setColor(TEXTCOLOR);
                int y_text = y + d * 13 / 20;
                g.drawString("[" + b.t_0 + "," + b.t_e + ">", x + d + step_size, y_text);
                g.drawString("" + b.num_unclosed(), x + d + step_size * 4, y_text);
                g.drawString("" + b.getCases().size(), x + d + (int) (step_size * 5.5), y_text);
                g.drawString("" + b.numEvents(), x + d + (int) (step_size * 7.5), y_text);

                if (b == this.closingBatch) {
                    g.setColor(BATCHES_COLOR_CLOSED);
                } else if (pruning && this.pruned_batch == b) {
                    g.setColor(BATCHES_COLOR_PRUNED);
                } else {
                    g.setColor(BATCHES_COLOR);
                }
            }
            g.fillRoundRect(x, y, d, d, d / 2, d / 2);

        }


    }

    //Add message to message list
    private void add_msg(String s) {
        if (this.pruning) {
            s = "[Pruning] " + s;
        }
        this.message_list.add(s);
        System.out.println(this.message_list.get(this.message_list.size() - 1));
    }


    private void draw_models(Graphics g) {

        int step_size = VTS * 3;
        int x = MODELS_X;
        int y = MODELS_Y - VTS / 2;
        g.setColor(TEXTCOLOR);

        if (grahof.B.contains(this.closingBatch) && this.model_scores != null && !this.model_scores.isEmpty()) {
            g.drawString("Batch F1", x - step_size * 2, y);
        }

        g.drawString("k", x + MODELS_DIM + step_size, y);
        g.drawString("F1", x + MODELS_DIM + step_size * 2, y);
        g.drawString("|mu_train|", (int) (x + MODELS_DIM + step_size * 3.5), y);
        ArrayList<Model> model_al = new ArrayList<>();
        grahof.M.stream().sorted((m1, m2) -> -Integer.compare(m1.model_number, m2.model_number)).forEach(model_al::add);
        for (Model m : model_al) {
            int[] xy = get_model_xy(m);
            x = xy[0];
            y = xy[1];
            g.setColor(MODELS_COLOR);
            g.fillRoundRect(x, y, MODELS_DIM, MODELS_DIM, MODELS_DIM / 2, MODELS_DIM / 2);

            g.setColor(TEXTCOLOR);
            int y_text = y + MODELS_DIM * 13 / 20;

            if (grahof.B.contains(this.closingBatch) && this.model_scores != null) {
                if (this.unfitModels.contains(m)) {
                    g.setColor(MODELS_COLOR_UNFIT_TEXT);
                } else {
                    g.setColor(TEXTCOLOR);
                }
                if (m != this.new_model) {
                    g.drawString(df3.format(this.model_scores.get(m)) + "", x - step_size * 2, y_text);
                }
            }
            g.setColor(TEXTCOLOR);
            g.drawString("" + m.model_number, x + MODELS_DIM + step_size, y_text);

            g.drawString("" + df3.format(m.f_m), x + MODELS_DIM + step_size * 2, y_text);
            g.drawString("" + m.mu_train.size(), x + MODELS_DIM + (int) (step_size * 3.5), y_text);

        }
    }


    //Get xy position of given model
    private int[] get_model_xy(Model m) {
        int x = MODELS_X;
        int y = MODELS_Y + (MODELS_DIM + VTS) * (grahof.M.size() - m.model_number - 1);
        return new int[]{x, y};
    }

    //Get xy position of given batch
    private int[] get_batch_xy(Batch b) {
        int x = BATCHES_X;
        int y = BATCHES_Y + (BATCHES_DIM + VTS) * (grahof.B.size() - 1 - grahof.get_sorted_batches().indexOf(b));
        return new int[]{x, y};
    }

    //Sleep so that the user can be amazed by the state of the GRAHOF
    private void sleep(double speed_modifier) {
        if (speed_modifier <= 0) {
            return;
        }

        try {
            Thread.sleep((long) ((this.max_step_duration + 1 - this.SpeedControl.getValue()) * speed_modifier));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Next update to tracked variables
    private Double next_time_point_at() {
        return this.time_points.get(this.time_points.size() - 1) + this.time_update;
    }

    @Override
    public void onEventReceived(Event e) {
        this.received_events++;

        if (e.time > next_time_point_at()) {
            this.time_points.add(next_time_point_at());
            this.total_number_events.add(grahof.events_in_memory() - 1.0);
        }

        if (received_events % this.event_update_interval == 0) {
            this.add_msg("Received Event Milestone : " + this.received_events);
            this.repaint();
            this.sleep(this.eventUpdate_modifier);
        }
    }

    @Override
    public void onBatchCloseStart(Batch b) {
        this.closingBatch = b;
        this.add_msg("Closing batch with interval : " + b.getIntervalString());
        this.repaint();
        this.sleep(this.batchClose_modifier);
    }

    @Override
    public void onModelScoring(HashMap<Model, Double> m_p) {
        if (this.closingBatch == null) {
            throw new IllegalStateException("Received scoring, not closing a Batch");
        }
        this.model_scores = new HashMap<>(m_p);
        this.unfitModels = new HashSet<>();
        this.add_msg("Computed F1 scores for all models on closing Batch");
        this.repaint();
        this.sleep(this.modelScore_modifier);
    }

    @Override
    public void onBatchCloseEnd(Batch b) {
        if (this.closingBatch != b) {
            throw new IllegalStateException("Closed batch is not expected");
        } else {
            this.closingBatch = null;
            this.model_scores = null;
            this.updated_model = null;
            this.new_model = null;
            this.unfitModels = null;
        }
        this.add_msg("Batch is closed");
        this.repaint();
        this.sleep(this.batchCloseEnd_modifier);
    }

    @Override
    public void onModelUpdate(Model m) {
        this.updated_model = m;
        this.add_msg("Updated model with id " + m.model_number);
        this.model_update_ticker_step = 0;
        while (this.model_update_ticker_step < this.animation_steps) {
            this.add_msg("Moving model ... " + this.model_update_ticker_step + " / " + this.animation_steps);
            this.repaint();
            this.sleep(1.0 * this.modelUpdate_modifier / this.animation_steps);
            this.model_update_ticker_step++;
        }
    }

    @Override
    public void onNewModel(Model m) {
        if (this.updated_model != null) {
            throw new IllegalStateException("New model, but Model is also updated!");
        }
        if (this.new_model != null) {
            throw new IllegalStateException("New model, but we already have a new model!");
        }
        this.add_msg("Created new model, with id " + m.model_number);
        this.new_model = m;
        this.repaint();
        this.sleep(this.modelCreation_modifier);
    }

    @Override
    public void onUnfitModel(Model m_star) {
        if (this.unfitModels == null) {
            throw new IllegalStateException("Not expecting unfitting models");
        }
        this.unfitModels.add(m_star);
        this.add_msg("Model does not match : " + m_star.model_number);
        this.repaint();
        this.sleep(this.modelUnfit_modifier);
    }

    @Override
    public void onMemoryPruneStart() {
        if (this.pruning) {
            throw new IllegalStateException("Already pruning!");
        }
        this.pruning = true;
        this.add_msg("Pruning Memory ...");
        this.repaint();
        this.sleep(this.memoryPrune_modifier);
    }

    @Override
    public void onMemoryPruneEnd() {
        if (!this.pruning) {
            throw new IllegalStateException("Not pruning...");
        }
        this.add_msg("Finished Pruning");
        this.pruning = false;
        this.pruned_case = null;
        this.pruned_batch = null;
        this.repaint();
        this.sleep(this.memoryPrune_modifier);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
    }

    @Override
    public void stateChanged(ChangeEvent changeEvent) {
    }
}
