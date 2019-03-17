package GRAHOF.Metrics;

import GRAHOF.Entities.Batch;
import GRAHOF.GRAHOF;
import GRAHOF.Metrics.ScoreFunction;
import ProcessUnits.Case;

import java.text.DecimalFormat;
import java.util.*;

public class Score_Tracker {

    public final HashMap<Integer, Double> scores;
    public final HashMap<Integer, Double> incremental_scores;
    private int total;
    private double weighted_score;
    public final ScoreFunction score_function;

    public Score_Tracker(ScoreFunction sf) {
        this.scores = new HashMap<>();
        this.incremental_scores = new HashMap<>();
        this.weighted_score = 0;
        this.total = 0;
        this.score_function = sf;
    }

    public double get_score() {
        return this.weighted_score;
    }

    public void update(Batch b) {
        for (int mu_k : b.mu_b) {

            //Get cases
            Collection<Case> cases = b.getCases(mu_k);

            if (!cases.isEmpty()) {
                double score = this.score_function.score(cases);
                this.scores.put(mu_k, score);
                //Update score
                double total_score = this.weighted_score * this.total + score * cases.size();
                this.total += cases.size();
                this.weighted_score = total_score / this.total;
            }
            this.incremental_scores.put(mu_k, this.weighted_score);
        }
    }

    public String get_all() {
        TreeMap<Integer, Double> values = new TreeMap<>(this.scores);
        StringBuilder s = new StringBuilder();
        DecimalFormat df = new DecimalFormat("0.000");
        for (int i : values.keySet()) {
            s.append(i).append(" => ").append(df.format(values.get(i))).append("\n");
        }
        return s.toString();
    }

    public String get_all_incremental_scores() {
        TreeMap<Integer, Double> values = new TreeMap<>(this.incremental_scores);
        StringBuilder s = new StringBuilder();
        DecimalFormat df = new DecimalFormat("0.000");
        for (int i : values.keySet()) {
            s.append(i).append(" => ").append(df.format(values.get(i))).append("\n");
        }
        return s.toString();
    }

    public double score(int i) {
        if (this.scores.containsKey(i)) {
            return this.scores.get(i);
        } else {
            throw new IllegalArgumentException("Data for given Unit not known");
        }
    }

    public double incremental_score(int i) {
        return this.incremental_scores.get(i);
    }

    public ArrayList<Integer> get_sorted_keys() {
        ArrayList<Integer> ret = new ArrayList<>();
        this.scores.keySet().stream().mapToInt(i -> i).sorted().forEach(ret::add);
        return ret;
    }
}