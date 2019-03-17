package ProcessUnits;

import java.util.HashMap;

/*
 *
 */
public class EventLog {

    /**
     * Dictionary to find Cases on Case ID
     */
    public HashMap<String, Case> cases = new HashMap<>();

    /**
     * Adds Case, checking if it is not already in the Case Log.
     *
     * @param c The Case to be added
     */
    public void add(Case c) {
        if (this.cases.containsKey(c.cid)) {
            throw new IllegalStateException("Case already in Case Log");
        } else {
            this.cases.put(c.cid, c);
        }
    }

    /**
     * Adds a given Event to the corresponding Case, if the Case is in the Event Log AND not closed. Returns the Case
     *
     * @param e The Event to be added
     * @return Case with cid equal to e.cid, if addition was successful
     */
    public Case add(Event e) {
        if (!this.cases.containsKey(e.cid)) {
            throw new IllegalStateException("Corresponding Case not in Event Log");
        }
        Case c = this.cases.get(e.cid);
        if (c.closed) {
            throw new IllegalStateException("Corresponding Case is already closed");
        }
        c.add(e);
        return c;
    }

    /**
     * Checks if given Case ID is in the Case Log
     *
     * @param cid Case ID to be checked
     * @return \exists c \in this : c.cid = cid
     */
    public boolean has_case(String cid) {
        return this.cases.containsKey(cid);
    }

    /**
     * Check if the event log is empty
     * @return \exists c \in this
     */

    public boolean isEmpty() {
        return this.cases.isEmpty();
    }

    /**
     * Removes a given case, throws an exception if not present
     * @param c the removed case
     */
    public void remove(Case c) {
        if (this.cases.containsKey(c.cid)) {
            this.cases.remove(c.cid);
        } else {
            throw new IllegalStateException("Case cannot be removed, not in Case Log");
        }
    }
}
