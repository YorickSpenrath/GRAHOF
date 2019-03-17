package GRAHOF.Entities;

import ProcessUnits.Case;
import ProcessUnits.EventLog;
import ProcessUnits.Event;

import GRAHOF.*;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

public class Batch {

    private int numEvents = 0;
    /**
     * The final Unit is \mu_{end_k}
     */
    public int end_k;
    /**
     * Start of the interval
     */
    public final double t_0;
    /**
     * End of the interval
     */
    public double t_e;
    /**
     * Weight of each model
     */
    public final HashMap<Model, Double> model_weights;
    /**
     * Set of integers k for which Unit \mu_k \subseteq Interval
     */
    public final HashSet<Integer> mu_b;
    /**
     * Case log for the Batch
     */
    public final EventLog case_log;

    /**
     * Check if case is in the bucket
     *
     * @param cid Case id to be checked
     * @return There is a Case with this ID in the bucket
     */
    public boolean has_case(String cid) {
        return this.case_log.has_case(cid);
    }

    /**
     * Get all cases in this bucket
     *
     * @return All Cases in the Case Log
     */
    public Collection<Case> getCases() {
        return this.case_log.cases.values();
    }

    /**
     * Constructor, given start and end Unit. Not that BOTH are INCLUDED in the interval
     *
     * @param start_k Index of first Unit in the Batch
     * @param end_k   Index of last Unit in the Batch
     */
    public Batch(int start_k, int end_k) {
        this.end_k = end_k;

        //Interval is generated from Units
        this.t_e = (end_k + 1) * stuff.mu;
        this.t_0 = (start_k) * stuff.mu;

        //Add all Units
        this.mu_b = new HashSet<>();
        for (int k = start_k; k <= end_k; k++) {
            this.mu_b.add(k);
        }

        //Initiate new Case Log and Model Weights (which are initially empty, but GRAHOF sets them on creation.
        this.case_log = new EventLog();
        this.model_weights = new HashMap<>();

    }

    /**
     * String representation
     *
     * @return Returns the Interval, and closed + total number of Cases
     */
    @Override
    public String toString() {
        DecimalFormat df2 = new DecimalFormat("0.00");
        return "Batch : [" + df2.format(this.t_0) + ", " + df2.format(this.t_e) + "> \t " + this.num_unclosed() + "/" + this.case_log.cases.size() + " open cases.";
    }


    public int num_unclosed() {
        return (int) this.getCases().stream().filter(c -> !c.closed).count();
    }

    /**
     * Add given Case to the Case Log. Verifies the Case belongs to this Batch
     *
     * @param c The Case to be added
     */
    public void add(Case c) {
        if (c.get_start() < this.t_0 || c.get_start() >= this.t_e) {
            throw new IllegalStateException("Case does not belong to this bucket : " + c.get_start() + " not in [" + this.t_0 + ", " + this.t_e + ">");
        }
        this.case_log.add(c);
        this.numEvents++;
    }

    /**
     * Add given Event to the Case Log. Verifies that the Event belongs to a Case in the Batch
     *
     * @param e Event to be added
     * @return The Case {@code e} was added to
     */
    public Case add(Event e) {
        if (this.case_log.has_case(e.cid)) {
            this.numEvents++;
            return this.case_log.add(e);
        } else {
            throw new IllegalStateException("Received event does not belong to this bucket");
        }
    }

    /**
     * Get all cases for a given Unit
     *
     * @param mu_k index of the Unit
     * @return all cases that start during the Unit
     */
    public Collection<Case> getCases(int mu_k) {
        HashSet<Case> ret = new HashSet<>();
        this.getCases().stream().filter(c -> c.get_start() >= mu_k * stuff.mu).filter(c -> c.get_start() < (mu_k + 1) * stuff.mu).forEach(ret::add);
        return ret;
    }

    public boolean isEmpty() {
        return this.case_log.isEmpty();
    }

    public boolean verify_trainability() {
        HashMap<String, Integer> counts = new HashMap<>();
        for (Case c : this.getCases()) {
            counts.put(c.getTrue_label(), counts.getOrDefault(c.getTrue_label(), 0) + 1);
        }

        if (counts.size() < 2) {
            return false;
        }
        return counts.values().stream().mapToInt(i -> i).min().getAsInt() >= stuff.minClassSize;
    }

    public void remove(Case c) {
        this.case_log.remove(c);
        this.numEvents -= c.size();
    }

    public void close_case(Case c) {
        if (!this.case_log.has_case(c.cid)) {
            throw new IllegalStateException("Given case is not in this Batch");
        }
        if (c.closed) {
            throw new IllegalStateException("Case already closed");
        }
        c.closed = true;
    }

    public String getIntervalString() {
        return "[" + this.t_0 + "," + this.t_e + ">";
    }

    public int numEvents() {
        return this.numEvents;
    }

    public Collection<Case> getNonClosedCases() {
        return this.getCases().stream().filter(c -> !c.closed).collect(Collectors.toList());
    }
}

