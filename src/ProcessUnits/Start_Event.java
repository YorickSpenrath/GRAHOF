package ProcessUnits;

import GRAHOF.stuff;
import com.yahoo.labs.samoa.instances.Instance;

import java.util.Arrays;

/**
 * Subclass of event, containing feature data
 */
public class Start_Event extends Event {
    final public Instance data;

    Start_Event(String cid, String act, double time, Instance i) {
        super(cid, act, time);
        if (!act.equals(stuff.start_act_name)) {
            throw new IllegalStateException("Trying to create start event that is not start activity");
        }
        this.data = i;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());

        for (int i = 0; i < this.data.numValues() - 1; i++) {
            sb.append("\n\t").append(this.data.attribute(i).name()).append(" = ").append(this.data.attribute(i).value((int) this.data.value(i)));
        }

        return sb.toString();

    }
}