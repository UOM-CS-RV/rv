package mt.edu.um.cs.rv.eventmanager.performance.events;

/**
 * Created by dwardu on 27/01/2016.
 */
public class EventB extends BaseEvent {


    public EventB(boolean sync, int seqNo) {
        super(sync, seqNo);
    }

    @Override
    public String toString() {
        return "EventB{" +
                "seqNo=" + this.getSeqNo() +
                '}';
    }
}
