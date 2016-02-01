package mt.edu.um.cs.rv.eventmanager.performance.events;

import mt.edu.um.cs.rv.events.Event;

/**
 * Created by dwardu on 27/01/2016.
 */
public abstract class BaseEvent implements Event {

    private int seqNo;
    private boolean sync;
    private Long timeCreated;

    public BaseEvent(boolean sync, int seqNo) {
        this.sync = sync;
        this.seqNo= seqNo;
        this.timeCreated = System.currentTimeMillis();
    }

    @Override
    public boolean isSynchronous() {
        return sync;
    }

    public int getSeqNo() {
        return seqNo;
    }


    public Long getTimeCreated() {
        return timeCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseEvent baseEvent = (BaseEvent) o;

        if (seqNo != baseEvent.seqNo) return false;
        return sync == baseEvent.sync;

    }

    @Override
    public int hashCode() {
        int result = seqNo;
        result = 31 * result + (sync ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EventA{" +
                "seqNo=" + seqNo +
                '}';
    }
}
