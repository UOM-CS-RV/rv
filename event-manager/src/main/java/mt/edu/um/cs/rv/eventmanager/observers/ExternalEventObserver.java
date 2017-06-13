package mt.edu.um.cs.rv.eventmanager.observers;

import mt.edu.um.cs.rv.eventmanager.engine.EventMessageSender;
import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.events.builders.EventBuilder;
import mt.edu.um.cs.rv.events.builders.EventBuilderRegistry;
import mt.edu.um.cs.rv.events.triggers.TriggerData;
import mt.edu.um.cs.rv.monitors.results.MonitorResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * An interface that defines how an event observer can generate an event based on an external observation/trigger.
 * <p>
 * Created by edwardmallia on 19/01/2017.
 */
public abstract class ExternalEventObserver<M, T extends TriggerData, R> {
    private static Logger LOGGER = LoggerFactory.getLogger(ExternalEventObserver.class);

    @Autowired
    EventBuilderRegistry eventBuilderRegistry;

    @Autowired
    EventMessageSender eventMessageSender;

    public final CompletableFuture<R> onMessage(M message) {
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
        Future<MonitorResult<?>> monitorResultFuture = null;
        LOGGER.debug("Checking whether event should be fired ...");
        if (builder.shouldFireEvent(event)) {
            LOGGER.debug("Event should be fired. Firing event ...");
            monitorResultFuture = fireEvent(event);
        } else {
            LOGGER.debug("Event should not be fired.");
            monitorResultFuture = CompletableFuture.completedFuture(MonitorResult.ok());
        }

        LOGGER.debug("Building chained CompletableFuture that will get the monitor result and generate the response.");
        return buildCompletableFuture(message, trigger, monitorResultFuture);
    }

    private CompletableFuture<R> buildCompletableFuture(final M m, final T t, final Future<MonitorResult<?>> monitorResultFuture) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return monitorResultFuture.get();
            } catch (MessagingException me){
                String msg = "Unexpected MessageException occurred";
                LOGGER.error("{}. Creating and returning a FAILURE MonitorResult.", msg, me);
                return MonitorResult.failure(null, me);
            }
            catch (Throwable throwable){
                String msg = "Unexpected Throwable occurred";
                LOGGER.error("{}. Creating and returning a FAILURE MonitorResult.", msg, throwable);
                return MonitorResult.failure(null, throwable);
            }
        }).thenApply(monitorResult -> generateResponse(m, t, monitorResult));
    }

    private Future<MonitorResult<?>> fireEvent(Event event) {
        return eventMessageSender.send(event);
    }

    public abstract T generateTrigger(M m);

    public abstract R generateResponse(M m, T t, MonitorResult monitorResult);

    //default implementation
    public Boolean shouldEventBeSynchronous(T t) {
        return false;
    }
}
