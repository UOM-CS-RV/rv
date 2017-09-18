package mt.edu.um.cs.rv.eventmanager.observers;

import mt.edu.um.cs.rv.eventmanager.engine.EventMessageSender;
import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.events.builders.EventBuilder;
import mt.edu.um.cs.rv.events.builders.EventBuilderRegistry;
import mt.edu.um.cs.rv.events.triggers.Trigger;
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
public abstract class ExternalEventObserver<M, TD extends TriggerData, R> implements Trigger {
    private static Logger LOGGER = LoggerFactory.getLogger(ExternalEventObserver.class);

    @Autowired
    EventBuilderRegistry eventBuilderRegistry;

    @Autowired
    EventMessageSender eventMessageSender;

    public final CompletableFuture<R> onMessage(M message) {
        LOGGER.debug("Generating TriggerData from Message [{}]", message);
        TD triggerData = generateTriggerData(message);
        LOGGER.debug("Generated TriggerData [{}] from Message [{}]", triggerData, message);

        LOGGER.debug("Computing whether event to be created from TriggerData [{}] should be a/synchronous", triggerData);
        Boolean shouldEventBeSynchronous = shouldEventBeSynchronous(triggerData);
        LOGGER.debug("Event should be {}", shouldEventBeSynchronous ? "synchronous" : "asynchronous");

        LOGGER.debug("Building event for TriggerData class [{}] in ExternalEventObserver type [{}]. Evaluating EventBuilders to use ...",
                triggerData.getClass().getTypeName(), this.getClass().getTypeName());
        List<EventBuilder> eventBuilders = eventBuilderRegistry.getBuilders(triggerData.getClass(), this.getClass());
        LOGGER.debug("Found [{}] EventBuilders for TriggerData class [{}] in ExternalEventObserver type [{}].",
                eventBuilders == null ? 0 : eventBuilders, triggerData.getClass().getTypeName(), this.getClass().getTypeName());

        List<Future<MonitorResult<?>>> monitorResultFutures = new ArrayList<>();
        for (EventBuilder eventBuilder : eventBuilders) {
            LOGGER.debug("Building new event using EventBuilder [{}] TriggerData class [{}] in ExternalEventObserver type [{}].",
                    eventBuilder.getClass().getTypeName(), triggerData.getClass().getTypeName(), this.getClass().getTypeName());

            Event event = eventBuilder.build(triggerData, shouldEventBeSynchronous);
            LOGGER.debug("Built new event {} using EventBuilder [{}] TriggerData class [{}] in ExternalEventObserver type [{}].",
                    event, eventBuilders.getClass().getTypeName(), triggerData.getClass().getTypeName(), this.getClass().getTypeName());
            
            Future<MonitorResult<?>> monitorResultFuture = null;
            LOGGER.debug("Checking whether event [{}] should be fired ...", event);
            if (eventBuilder.shouldFireEvent(event)) {
                LOGGER.debug("Event [{}] should be fired. Firing event ...", event);
                monitorResultFuture = fireEvent(event);
            } else {
                LOGGER.debug("Event [{}] should not be fired.", event);
                monitorResultFuture = CompletableFuture.completedFuture(MonitorResult.ok());
            }

            monitorResultFutures.add(monitorResultFuture);
        }

        LOGGER.debug("Building chained CompletableFuture that will get the monitor result and generate the response.");
        return buildCompletableFuture(message, triggerData, monitorResultFutures);
    }

    private CompletableFuture<R> buildCompletableFuture(final M m, final TD triggerData, final List<Future<MonitorResult<?>>> monitorResultFutures) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (monitorResultFutures == null || monitorResultFutures.isEmpty()) {
                    return MonitorResult.ok();
                } else if (monitorResultFutures.size() == 1) {
                    return monitorResultFutures.get(0).get();
                } else {
                    MonitorResultList monitorResultList = new MonitorResultList();

                    for (Future<MonitorResult<?>> monitorResultFuture : monitorResultFutures) {
                        MonitorResult<?> monitorResult = monitorResultFuture.get();
                        monitorResultList.addMonitorResult(monitorResult);
                    }

                    return monitorResultList;
                }
            } catch (MessagingException me) {
                String msg = "Unexpected MessageException occurred";
                LOGGER.error("{}. Creating and returning a FAILURE MonitorResult.", msg, me);
                return MonitorResult.failure(null, me);
            } catch (Throwable throwable) {
                String msg = "Unexpected Throwable occurred";
                LOGGER.error("{}. Creating and returning a FAILURE MonitorResult.", msg, throwable);
                return MonitorResult.failure(null, throwable);
            }
        }).thenApply(monitorResult -> generateResponse(m, triggerData, monitorResult));
    }

    private Future<MonitorResult<?>> fireEvent(Event event) {
        return eventMessageSender.send(event);
    }

    public abstract TD generateTriggerData(M m);

    public abstract R generateResponse(M m, TD triggerData, MonitorResult monitorResult);

    //default implementation
    public Boolean shouldEventBeSynchronous(TD triggerData) {
        return false;
    }
}
