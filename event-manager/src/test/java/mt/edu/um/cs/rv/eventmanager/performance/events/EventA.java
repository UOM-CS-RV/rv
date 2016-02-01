package mt.edu.um.cs.rv.eventmanager.performance.events;

/**
 * Created by dwardu on 27/01/2016.
 */
public class EventA extends BaseEvent {

    public EventA(boolean sync, int seqNo) {
        super(sync, seqNo);
    }

    @Override
    public String toString() {
        return "EventA{" +
                "seqNo=" + this.getSeqNo() +
                '}';
    }
}
