package mt.edu.um.cs.rv.eventmanager.engine;

import mt.edu.um.cs.rv.eventmanager.common.TestEvent;
import mt.edu.um.cs.rv.events.Event;
import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.channel.NullChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.messaging.support.GenericMessage;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by dwardu on 25/01/2016.
 */
public class EventMessageSenderTest {

    private EventMessageSender eventMessageSender;

    private MessagingTemplate messagingTemplate;
    private ExecutorChannel executorChannel;
    private NullChannel noop;


    @Before
    public void setup() {

        messagingTemplate = mock(MessagingTemplate.class);
        executorChannel = mock(ExecutorChannel.class);
        noop = mock(NullChannel.class);

        eventMessageSender = new EventMessageSender(messagingTemplate, executorChannel, noop);
    }

    @Test
    public void testSendSyncEvent() {

        when(messagingTemplate.sendAndReceive(eq(executorChannel), any())).thenReturn(new GenericMessage(""));

        Event e = new TestEvent(true);
        eventMessageSender.send(e);

        verify(messagingTemplate, times(1)).sendAndReceive(eq(executorChannel), any());
    }

    @Test
    public void testSendAsyncEvent() {

        Event e = new TestEvent(false);
        eventMessageSender.send(e);

        verify(messagingTemplate, times(1)).send(eq(executorChannel), any());
    }
}
