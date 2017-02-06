package mt.edu.um.cs.rv.eventmanager.observers;

import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.events.builders.EventBuilder;
import mt.edu.um.cs.rv.events.builders.EventBuilderRegistry;
import mt.edu.um.cs.rv.events.triggers.TriggerData;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * An interface that defines how an event observer can generate an event based on an external observation/trigger.
 *
 * Created by edwardmallia on 19/01/2017.
 */
public abstract class ExternalEventObserver<M,T extends TriggerData,R>
{

    @Autowired
    EventBuilderRegistry eventBuilderRegistry;

    public final void onMessage(M message) {
        T trigger = generateTrigger(message);

        Boolean shouldEventBeSynchronous = shouldEventBeSynchronous(trigger);

        EventBuilder builder = eventBuilderRegistry.getBuilder(trigger.getClass());
        Event event = builder.build(trigger, shouldEventBeSynchronous);
        if (builder.shouldFireEvent(event)) {
            fireEvent(event);
        }

        R r = generateResponse(message, trigger);
        sendResponse(r);
    }

    protected final void fireEvent(Event event){
        //TODO should we use the EventMessageSender directly here?
        mt.edu.um.cs.rv.eventmanager.observers.DirectInvocationEventObserver observer = mt.edu.um.cs.rv.eventmanager.observers.DirectInvocationEventObserver.getInstance();
        observer.observeEvent(event);
    }

    public abstract T generateTrigger(M m);

    public abstract R generateResponse(M m, T t);

    public abstract void sendResponse(R r);

    //default implementation
    public Boolean shouldEventBeSynchronous(T t){
        return false;
    }
}
