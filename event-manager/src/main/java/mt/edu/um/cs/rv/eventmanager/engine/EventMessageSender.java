package mt.edu.um.cs.rv.eventmanager.engine;

import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.monitors.Monitor;
import mt.edu.um.cs.rv.monitors.results.MonitorResult;
import mt.edu.um.cs.rv.monitors.results.MonitorResultList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.channel.NullChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by dwardu on 19/01/2016.
 */
public class EventMessageSender {

    private static Logger LOGGER = LoggerFactory.getLogger(EventMessageSender.class);

    private MessagingTemplate messagingTemplate;
    private ExecutorChannel executorChannel;
    private NullChannel noop;

    public EventMessageSender(MessagingTemplate messagingTemplate, ExecutorChannel executorChannel, NullChannel noop) {
        this.messagingTemplate = messagingTemplate;
        this.executorChannel = executorChannel;
        this.noop = noop;
    }

    public MonitorResult send(Event e) {
        try {
            return _send(e);
        }
        catch (MessagingException me){
            String msg = "Unexpected MessageException occurred";
            LOGGER.error("{}. Creating and returning a FAILURE MonitorResult.", msg, me);
            return MonitorResult.failure(null, me);
        }
        catch (Throwable t){
            String msg = "Unexpected Throwable occurred";
            LOGGER.error("{}. Creating and returning a FAILURE MonitorResult.", msg, t);
            return MonitorResult.failure(null, t);
        }
    }

    private MonitorResult _send(Event e) throws Throwable {

        LOGGER.debug("Building event system message from event");
        MessageBuilder<Event> eventMessageBuilder = MessageBuilder.withPayload(e);

        if (e.isSynchronous()) {
            LOGGER.debug("Handling event in a synchronous manner ...");
            eventMessageBuilder.setReplyChannelName("systemBlockingChannel");
            Message<Event> eventMessage = eventMessageBuilder.build();

            LOGGER.debug("Sending event system message synchronously");
            Message<?> r = messagingTemplate.sendAndReceive(executorChannel, eventMessage);

            LOGGER.debug("Preparing response");
            Object payload = r.getPayload();
            if (payload == null) {
                LOGGER.debug("Received a null MonitorResult, creating and returning a OK MonitorResult");
                return MonitorResult.ok();
            }
            if (payload instanceof MonitorResult) {
                LOGGER.debug("Received MonitorResult as response, returning the results");
                return (MonitorResult) payload;
            }
            //i.e. event was consumed by more than one top level monitor
            else if (payload instanceof Collection) {
                LOGGER.debug("Received a collection of MonitorResult as response, returning the results as a MonitorResultList");
                Collection payloadColl = (Collection) payload;
                MonitorResultList monitorResultList = new MonitorResultList();
                //TODO how to handle elements which are not MonitorResult
                payloadColl.stream().forEach(o -> monitorResultList.addMonitorResult((MonitorResult) o));
                return monitorResultList;
            }
            else if (payload instanceof Throwable){
                throw (Throwable) payload;
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

                LOGGER.error("{}. Creating and returning a FAILURE MonitorResult.", msg, throwable);
                MonitorResult<Serializable> failure = MonitorResult.failure(serializable, throwable);
                LOGGER.error("Created FAILURE MonitorResult [{}]", failure, throwable);
                return failure;
            }
        }
        else {
            LOGGER.debug("Handling event in a asynchronous manner ...");
            eventMessageBuilder.setReplyChannel(noop);
            Message<Event> eventMessage = eventMessageBuilder.build();

            LOGGER.debug("Sending event system message asynchronously");
            messagingTemplate.send(executorChannel, eventMessage);

            //TODO what to do here?
            LOGGER.debug("Returning OK for an event system message handled asynchronously.");
            return MonitorResult.ok();
        }

    }

}
