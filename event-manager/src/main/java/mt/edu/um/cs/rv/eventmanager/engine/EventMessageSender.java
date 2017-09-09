package mt.edu.um.cs.rv.eventmanager.engine;

import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.monitors.results.MonitorResult;
import mt.edu.um.cs.rv.monitors.results.MonitorResultList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.core.AsyncMessagingTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.*;

import static mt.edu.um.cs.rv.eventmanager.engine.config.EventManagerConfigration.EVENT_MANAGER_REQUEST_CHANNEL;
import static mt.edu.um.cs.rv.eventmanager.engine.config.EventManagerConfigration.EVENT_MANAGER_RESPONSE_CHANNEL;

/**
 * Created by dwardu on 19/01/2016.
 */
public class EventMessageSender {

    private static Logger LOGGER = LoggerFactory.getLogger(EventMessageSender.class);

    private AsyncMessagingTemplate asyncMessagingTemplate;

    public EventMessageSender(AsyncMessagingTemplate asyncMessagingTemplate) {
        this.asyncMessagingTemplate = asyncMessagingTemplate;
    }

    private Future<MonitorResult<?>> sendAsync(final Event e){
        LOGGER.debug("Building event system message from event {}", e.getClass().getName());
        Message<Event> eventMessage = MessageBuilder
                .withPayload(e)
                .setReplyChannelName(EVENT_MANAGER_RESPONSE_CHANNEL)
                .setErrorChannelName(EVENT_MANAGER_RESPONSE_CHANNEL)
                .build();

        LOGGER.debug("Sending event {} to event system message asynchronously (if event is sync, we will block to wait for response)", e.getClass().getName());
        final Future<Message<?>> messageFuture = this.asyncMessagingTemplate.asyncSendAndReceive(EVENT_MANAGER_REQUEST_CHANNEL, eventMessage);

        Future<MonitorResult<?>> monitorResultFuture = new Future<MonitorResult<?>>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return messageFuture.cancel(mayInterruptIfRunning);
            }

            @Override
            public boolean isCancelled() {
                return messageFuture.isCancelled();
            }

            @Override
            public boolean isDone() {
                return messageFuture.isDone();
            }

            @Override
            public MonitorResult get() throws InterruptedException, ExecutionException {
                Message<?> message = messageFuture.get();
                return transformResult(message);
            }

            @Override
            public MonitorResult get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                Message<?> message = messageFuture.get(timeout, unit);
                return transformResult(message);
            }

            private MonitorResult<?> transformResult(Message<?> r) {
                LOGGER.debug("Preparing response for event {}", e.getClass().getName());
                Object payload = r.getPayload();

                if (payload == null) {
                    LOGGER.debug("Received a null MonitorResult for event {} processing, creating and returning a OK MonitorResult", e.getClass().getName());
                    return MonitorResult.ok();
                }
                if (payload instanceof MonitorResult) {
                    LOGGER.debug("Received MonitorResult [{}] as response for event {} processing, returning the results", payload, e.getClass().getName());
                    return (MonitorResult) payload;
                }
                //i.e. event was consumed by more than one top level monitor
                else if (payload instanceof Collection) {
                    LOGGER.debug("Received a collection of MonitorResult [{}] as response for event {} processing, returning the results as a MonitorResultList", payload, e.getClass().getName());
                    Collection payloadColl = (Collection) payload;
                    MonitorResultList monitorResultList = new MonitorResultList();
                    //TODO how to handle elements which are not MonitorResult - should not occur
                    payloadColl.stream().forEach(o -> monitorResultList.addMonitorResult((MonitorResult) o));
                    return monitorResultList;
                }
                else if (payload instanceof Throwable){
                    Throwable throwable = (Throwable) payload;
                    String msg = String.format("Received an unexpected response type of Throwable - [%s]", payload.getClass());
                    LOGGER.error("{} for event {} processing. Creating and returning a FAILURE MonitorResult.", msg, e.getClass().getName(), throwable);
                    MonitorResult<Serializable> failure = MonitorResult.failure(null, throwable);
                    LOGGER.error("Created FAILURE MonitorResult [{}]", failure);
                    return failure;
                }
                else {
                    String msg = String.format("Received an unexpected response type of [%s]", payload.getClass());

                    Throwable throwable = new RuntimeException(msg);
                    Serializable serializable = msg;
                    if (payload instanceof Throwable){
                        throwable = (Throwable) payload;
                    }
                    else if (payload instanceof Serializable){
                        serializable = (Serializable) payload;
                    }

                    LOGGER.error("{} for event {} processing. Creating and returning a FAILURE MonitorResult.", msg, e.getClass().getName(),throwable);
                    MonitorResult<Serializable> failure = MonitorResult.failure(serializable, throwable);
                    LOGGER.error("Created FAILURE MonitorResult [{}]", failure);
                    return failure;
                }
            }
        };

        return monitorResultFuture;
    }

    public Future<MonitorResult<?>> send(final Event e) {
        try {
            Future<MonitorResult<?>> monitorResultFuture = sendAsync(e);

            if (e.isSynchronous()){
                LOGGER.debug("Event {} is synchronous, blocking to wait for response", e.getClass().getName());
                //block to wait for response
                monitorResultFuture.get();
            }

            return monitorResultFuture;
        }
        catch (MessagingException me){
            String msg = "Unexpected MessageException occurred";
            LOGGER.error("{} for event {} processing. Creating and returning a FAILURE MonitorResult.", msg, e.getClass().getName(), me);
            MonitorResult<?> failureResult = MonitorResult.failure(null, me);
            return CompletableFuture.completedFuture(failureResult);
        }
        catch (Throwable t){
            String msg = "Unexpected Throwable occurred";
            LOGGER.error("{} for event {} processing. Creating and returning a FAILURE MonitorResult.", e.getClass().getName(), msg, t);
            MonitorResult<?> failureResult = MonitorResult.failure(null, t);
            return CompletableFuture.completedFuture(failureResult);
        }
    }

}
