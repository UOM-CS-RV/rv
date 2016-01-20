package mt.edu.um.cs.rv.eventmanager.si;

import mt.edu.um.cs.rv.eventmanager.events.Event;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.channel.NullChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

import java.util.concurrent.Executor;

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

    public void send(Event e) {
        MessageBuilder<Event> eventMessageBuilder = MessageBuilder.withPayload(e);

        if (e.isSynchronous()) {
            eventMessageBuilder.setReplyChannelName("systemBlockingChannel");
            Message<Event> eventMessage = eventMessageBuilder.build();
            Message<?> r = messagingTemplate.sendAndReceive(executorChannel, eventMessage);
            //TODO handle response?
        }
        else {
            eventMessageBuilder.setReplyChannel(noop);
            Message<Event> eventMessage = eventMessageBuilder.build();
            messagingTemplate.send(executorChannel, eventMessage);
        }

    }

}
