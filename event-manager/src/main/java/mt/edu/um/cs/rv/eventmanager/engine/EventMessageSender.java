package mt.edu.um.cs.rv.eventmanager.engine;

import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.monitors.Monitor;
import mt.edu.um.cs.rv.monitors.results.MonitorResult;
import mt.edu.um.cs.rv.monitors.results.MonitorResultList;
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

    private MessagingTemplate messagingTemplate;
    private ExecutorChannel executorChannel;
    private NullChannel noop;

    public EventMessageSender(MessagingTemplate messagingTemplate, ExecutorChannel executorChannel, NullChannel noop) {
        this.messagingTemplate = messagingTemplate;
        this.executorChannel = executorChannel;
        this.noop = noop;
    }

    public MonitorResult send(Event e) {
        MessageBuilder<Event> eventMessageBuilder = MessageBuilder.withPayload(e);

        if (e.isSynchronous()) {
            eventMessageBuilder.setReplyChannelName("systemBlockingChannel");
            Message<Event> eventMessage = eventMessageBuilder.build();
            Message<?> r = messagingTemplate.sendAndReceive(executorChannel, eventMessage);

            Object payload = r.getPayload();
            if (payload instanceof MonitorResult) {
                return (MonitorResult) payload;
            }
            else if (payload instanceof Collection) {
                Collection payloadColl = (Collection) payload;      
                return MonitorResultList.of(new ArrayList<MonitorResult>(payloadColl));
            }
            else {
                //TODO what to do here?
                return MonitorResult.ok();
            }
        }
        else {
            eventMessageBuilder.setReplyChannel(noop);
            Message<Event> eventMessage = eventMessageBuilder.build();
            messagingTemplate.send(executorChannel, eventMessage);

            //TODO what to do here?
            return MonitorResult.ok();
        }

    }

}
