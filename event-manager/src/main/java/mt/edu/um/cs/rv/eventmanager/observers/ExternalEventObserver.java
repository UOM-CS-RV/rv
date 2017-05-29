package mt.edu.um.cs.rv.eventmanager.observers;

import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.events.builders.EventBuilder;
import mt.edu.um.cs.rv.events.builders.EventBuilderRegistry;
import mt.edu.um.cs.rv.events.triggers.TriggerData;
import mt.edu.um.cs.rv.monitors.results.MonitorResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * An interface that defines how an event observer can generate an event based on an external observation/trigger.
 *
 * Created by edwardmallia on 19/01/2017.
 */
public abstract class ExternalEventObserver<M,T extends TriggerData,R>
{
    private static Logger LOGGER = LoggerFactory.getLogger(ExternalEventObserver.class);

    @Autowired
    EventBuilderRegistry eventBuilderRegistry;

    public final R onMessage(M message) {
        LOGGER.debug("Generating Trigger from Message");
        T trigger = generateTrigger(message);

        LOGGER.debug("Computing whether event should be a/synchronous");
        Boolean shouldEventBeSynchronous = shouldEventBeSynchronous(trigger);
        LOGGER.debug("Event should be {}", shouldEventBeSynchronous ? "synchronous" : "asynchronous");

        LOGGER.debug("Building event ...");
        EventBuilder builder = eventBuilderRegistry.getBuilder(trigger.getClass());
        Event event = builder.build(trigger, shouldEventBeSynchronous);
        LOGGER.debug("Built new event {}", event);
        //TODO should this be the default ?
        MonitorResult monitorResult = MonitorResult.ok();
        LOGGER.debug("Checking whether event should be fired ...");
        if (builder.shouldFireEvent(event)) {
            LOGGER.debug("Event should be fired. Firing event ...");
            monitorResult = fireEvent(event);
            LOGGER.debug("Event fired with monitor result {}", monitorResult);
        }

        LOGGER.debug("Generating response for trigger");
        R r = generateResponse(message, trigger, monitorResult);
        LOGGER.debug("Sending response for trigger");
        return sendResponse(r);
    }

    protected final MonitorResult fireEvent(Event event){
        //TODO should we use the EventMessageSender directly here?
        mt.edu.um.cs.rv.eventmanager.observers.DirectInvocationEventObserver observer = mt.edu.um.cs.rv.eventmanager.observers.DirectInvocationEventObserver.getInstance();
        return observer.observeEvent(event);
    }

    public abstract T generateTrigger(M m);

    public abstract R generateResponse(M m, T t, MonitorResult monitorResult);

    public abstract R sendResponse(R r);

    //default implementation
    public Boolean shouldEventBeSynchronous(T t){
        return false;
    }
}
