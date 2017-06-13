package mt.edu.um.cs.rv.eventmanager.engine;

import mt.edu.um.cs.rv.eventmanager.common.TestEvent;
import mt.edu.um.cs.rv.eventmanager.engine.config.EventManagerConfigration;
import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.monitors.results.MonitorResult;
import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.channel.NullChannel;
import org.springframework.integration.core.AsyncMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by dwardu on 25/01/2016.
 */
public class EventMessageSenderTest {

    private EventMessageSender eventMessageSender;

    private AsyncMessagingTemplate asyncMessagingTemplate;

    @Before
    public void setup() {

        asyncMessagingTemplate = mock(AsyncMessagingTemplate.class);

        eventMessageSender = new EventMessageSender(asyncMessagingTemplate);
    }

    @Test
    public void testSendSyncEvent() {

        when(asyncMessagingTemplate.asyncSendAndReceive(eq(EventManagerConfigration.EVENT_MANAGER_REQUEST_CHANNEL), any(Message.class))).thenReturn(CompletableFuture.completedFuture(new GenericMessage(MonitorResult.ok())));

        Event e = new TestEvent(true);
        eventMessageSender.send(e);

        verify(asyncMessagingTemplate, times(1)).asyncSendAndReceive(eq(EventManagerConfigration.EVENT_MANAGER_REQUEST_CHANNEL), any(Message.class));
    }

    @Test
    public void testSendAsyncEvent() {

        when(asyncMessagingTemplate.asyncSendAndReceive(eq(EventManagerConfigration.EVENT_MANAGER_REQUEST_CHANNEL), any(Message.class))).thenReturn(CompletableFuture.completedFuture(new GenericMessage(MonitorResult.ok())));

        Event e = new TestEvent(false);
        eventMessageSender.send(e);

        verify(asyncMessagingTemplate, times(1)).asyncSendAndReceive(eq(EventManagerConfigration.EVENT_MANAGER_REQUEST_CHANNEL), any(Message.class));
    }
}
