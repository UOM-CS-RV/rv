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
            if (payload instanceof MonitorResult) {
                LOGGER.debug("Received MonitorResult as response, returning the results");
                return (MonitorResult) payload;
            }
            else if (payload instanceof Collection) {
                LOGGER.debug("Received a collection of MonitorResult as response, returning the results as a MonitorResultList");
                Collection payloadColl = (Collection) payload;      
                return MonitorResultList.of(new ArrayList<MonitorResult>(payloadColl));
            }
            else {
                //TODO what to do here?
                LOGGER.debug("Received an unexpected type of response {}. ", payload);
                return MonitorResult.error();
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
