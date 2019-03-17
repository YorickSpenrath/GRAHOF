package ProcessUnits;

/**
 * Simple tuple class
 */
public class Event {
    public final double time;
    public final String act;
    public final String cid;

    Event(String cid, String act, double time) {
        this.cid = cid;
        this.act = act;
        this.time = time;
    }

    public String toString() {
        return this.cid + " : " + this.act + " @ " + this.time;
    }

}
