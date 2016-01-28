package mt.edu.um.cs.rv.eventmanager.integration.events;

import mt.edu.um.cs.rv.events.Event;

/**
 * Created by dwardu on 27/01/2016.
 */
public class EventC implements Event {

    private int seqNo;
    private boolean sync;

    public EventC(boolean sync, int seqNo) {
        this.sync = sync;
        this.seqNo= seqNo;
    }

    @Override
    public boolean isSynchronous() {
        return sync;
    }

    public int getSeqNo() {
        return seqNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventC eventC = (EventC) o;

        if (seqNo != eventC.seqNo) return false;
        return sync == eventC.sync;

    }

    @Override
    public int hashCode() {
        int result = seqNo;
        result = 31 * result + (sync ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EventC{" +
                "seqNo=" + seqNo +
                '}';
    }
}
