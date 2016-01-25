package mt.edu.um.cs.rv.eventmanager.observers;

import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.eventmanager.engine.EventMessageSender;

/**
 * Created by dwardu on 19/01/2016.
 */
public class DirectInvocationEventObserver implements EventObserver {

    EventMessageSender eventMessageSender;

    public DirectInvocationEventObserver(EventMessageSender eventMessageSender) {
        this.eventMessageSender = eventMessageSender;
    }

    @Override
    public void observeEvent(Event e) {
        eventMessageSender.send(e);
    }
}
