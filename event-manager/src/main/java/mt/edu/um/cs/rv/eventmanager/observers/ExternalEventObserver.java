package mt.edu.um.cs.rv.eventmanager.observers;

import mt.edu.um.cs.rv.eventmanager.engine.EventMessageSender;
import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.events.builders.EventBuilder;
import mt.edu.um.cs.rv.events.builders.EventBuilderRegistry;
import mt.edu.um.cs.rv.events.triggers.TriggerData;
import mt.edu.um.cs.rv.monitors.results.MonitorResult;
import mt.edu.um.cs.rv.monitors.results.MonitorResultList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;

import java.util.ArrayList;
import java.util.List;
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
        List<EventBuilder> eventBuilders = eventBuilderRegistry.getBuilders(trigger.getClass());

        List<Future<MonitorResult<?>>> monitorResultFutures = new ArrayList();
        for (EventBuilder eventBuilder: eventBuilders) {
            Event event = eventBuilder.build(trigger, shouldEventBeSynchronous);
            LOGGER.debug("Built new event {}", event);

            //TODO should this be the default ?
            Future<MonitorResult<?>> monitorResultFuture = null;
            LOGGER.debug("Checking whether event should be fired ...");
            if (eventBuilder.shouldFireEvent(event)) {
                LOGGER.debug("Event should be fired. Firing event ...");
                monitorResultFuture = fireEvent(event);
            } else {
                LOGGER.debug("Event should not be fired.");
                monitorResultFuture = CompletableFuture.completedFuture(MonitorResult.ok());
            }

            monitorResultFutures.add(monitorResultFuture);
        }

        LOGGER.debug("Building chained CompletableFuture that will get the monitor result and generate the response.");
        return buildCompletableFuture(message, trigger, monitorResultFutures);
    }

    private CompletableFuture<R> buildCompletableFuture(final M m, final T t, final List<Future<MonitorResult<?>>> monitorResultFutures) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (monitorResultFutures == null || monitorResultFutures.isEmpty()){
                    return MonitorResult.ok();
                }
                else if (monitorResultFutures.size() == 1) {
                    return monitorResultFutures.get(0).get();
                }
                else {
                    MonitorResultList monitorResultList = new MonitorResultList();

                    for (Future<MonitorResult<?>> monitorResultFuture : monitorResultFutures) {
                        MonitorResult<?> monitorResult = monitorResultFuture.get();
                        monitorResultList.addMonitorResult(monitorResult);
                    }

                    return monitorResultList;
                }
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
