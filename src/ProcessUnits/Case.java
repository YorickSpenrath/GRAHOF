package ProcessUnits;

import com.yahoo.labs.samoa.instances.Attribute;
import com.yahoo.labs.samoa.instances.Instance;

import java.util.ArrayList;

public class Case {
    /**
     * Case id of the Case
     */
    public final String cid;

    /**
     * Feature and Label data of the Case
     */
    private final Instance data;

    public Instance getData() {
        return this.data;
    }

    /**
     * List of Events (not in a PriorityQueue because we are reading Streams under the assumption that it is chronological)
     */
    private final ArrayList<Event> events = new ArrayList<>();

    /**
     * Predicted Label
     */
    private String predicted_label = null;

    public String getPredicted_label() {
        return predicted_label;
    }

    /**
     * Only allowed once
     *
     * @param predicted_label the label that is predicted for this Case
     */

    public void setPredicted_label(String predicted_label) {
        if (this.predicted_label == null) {
            this.predicted_label = predicted_label;
        } else {
            throw new IllegalStateException("Predicted label already set!");
        }
    }


    /**
     * Actual Label
     */
    private String true_label = null;

    public String getTrue_label() {
        return true_label;
    }

    /**
     * Only allowed once
     *
     * @param true_label the actual label. Only supposed to be set by the bottleneck algorithm
     */
    public void setTrue_label(String true_label) {
        if (this.true_label == null) {
            this.true_label = true_label;
            this.data.setClassValue(this.data.classAttribute().indexOfValue(true_label));
        } else {
            throw new IllegalStateException("True label already set!");
        }
    }


    /**
     * Whether we can say 'Case Closed' (i.e. we do not expect more Events for this Case)
     */
    public boolean closed;

    /**
     * Default Constructor, given a Start Event. References all necessary info from {@code e}
     *
     * @param e Event that starts this case
     */
    public Case(Start_Event e) {
        this.cid = e.cid;
        this.data = e.data;
        this.events.add(e);
        this.closed = false;
    }

    /**
     * Generates description for Case
     *
     * @return includes ID, Trace, Data
     */
    @Override
    public String toString() {
        return "Case " +
                this.cid +
                " : " + this.getTrace() +
                "\n" + this.getStringData();
    }

    /**
     * Return this data of this case, also converting categorical data
     *
     * @return Data as human-interpretable String
     */
    private String getStringData() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.data.numValues(); i++) {
            Attribute a = this.data.attribute(i);
            double value = this.data.value(i);
            sb.append("\t");
            sb.append(a.name());
            sb.append(" = ");
            if (a.isNominal() && value != -1) {
                sb.append(a.value((int) value));
            } else {
                sb.append(value);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Gets the start of the Case
     *
     * @return the start of the first Event
     */
    public double get_start() {
        return this.events.get(0).time;
    }

    /**
     * Gets the duration of the Case
     *
     * @return Difference between first and last Event in the Case
     */
    public double duration() {
        return this.events.get(this.events.size() - 1).time - this.events.get(0).time;
    }

    /**
     * Adds given Event {@code e} to the Case. Checks that the Event occurs after the current end, and belongs to this Case
     *
     * @param e The Event to be added
     */

    void add(Event e) {
        if (e.time < this.events.get(this.events.size() - 1).time) {
            throw new IllegalArgumentException("Received Event happens before last Event in Case");
        }
        if (!e.cid.equals(this.cid)) {
            throw new IllegalArgumentException("Received Event for with different Case ID");
        }
        this.events.add(e);
    }

    /**
     * Generates the trace, separated by a "_";
     *
     * @return c(0).act + _ + c(1).act + _ + ... + c(|c|-1).act
     */
    public String getTrace() {
        StringBuilder s = new StringBuilder();
        String con = "";
        for (Event e : this.events) {
            s.append(con);
            con = "_";
            s.append(e.act);
        }
        return s.toString();
    }

    /**
     * Fetches the Event of the Case with the given index
     *
     * @param i index
     * @return c(i)
     */
    public Event get(int i) {
        return this.events.get(i);
    }

    /**
     * Get the number of events in this case
     * @return |c|
     */
    public int size() {
        return this.events.size();
    }

    /**
     * Fetch the time of the last added event
     * @return c(|c|-1).time
     */
    public double get_end() {
        return this.get(this.size() - 1).time;
    }
}