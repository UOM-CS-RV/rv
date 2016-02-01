package mt.edu.um.cs.rv.eventmanager.performance.events;

/**
 * Created by dwardu on 27/01/2016.
 */
public class EventC extends BaseEvent {


    public EventC(boolean sync, int seqNo) {
        super(sync, seqNo);
    }

    @Override
    public String toString() {
        return "EventC{" +
                "seqNo=" + this.getSeqNo() +
                '}';
    }
}
