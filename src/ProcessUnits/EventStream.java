package ProcessUnits;

import java.util.*;

/**
 * Abstract event class, is extended in a simulation stream and an csv reader stream
 */

public abstract class EventStream {

    /**
     * Queue of cases
     */
    final PriorityQueue<Event> events;

    /**
     * Initialises PQueue
     */
    EventStream() {
        this.events = new PriorityQueue<>(1, new event_comparator());
    }

    /**
     * Check if given event is end event
     * @param e to be check event
     * @return whether e is an end event
     */
    public abstract boolean End(Event e);


    /**
     * Check if more are expected
     * @return PQueue is not empty
     */
    public boolean hasnext() {
        return !events.isEmpty();
    }

    /**
     * Get the next event (this is abstract, such that subclasses can do alternative actions such as loading more events
     * @return Get next event (and remove it from PQueue
     */
    public abstract Event next();

    /**
     * Get the total number of cases in the stream
     */
    public abstract int total_cases();

    /**
     * Print the current content of the PQueue (without altering it)
     */
    public final void print_current() {
        PriorityQueue<Event> copy = new PriorityQueue<>(this.events);
        while (!copy.isEmpty()) {
            System.out.println(copy.poll().toString());
        }
    }

}

/**
 * Compares two events for the PQueue
 */
class event_comparator implements Comparator<Event> {

    @Override
    public int compare(Event e1, Event e2) {
        return Double.compare(e1.time, e2.time);
    }
}
