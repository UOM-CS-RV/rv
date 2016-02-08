package mt.edu.um.cs.rv.eventmanager.engine;

import mt.edu.um.cs.rv.eventmanager.common.TestEvent;
import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.monitors.Monitor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.messaging.Message;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by dwardu on 26/01/2016.
 */
public class MonitorEventSelectorTest {

    private MonitorEventSelector monitorEventSelector;

    @Before
    public void setup(){
        Monitor monitor = mock(Monitor.class);

        Set<Class<? extends Event>> requiredEvents = new HashSet<>();
        requiredEvents.add(TestEvent.class);

        when(monitor.requiredEvents()).thenReturn(requiredEvents);

        this.monitorEventSelector = new MonitorEventSelector(monitor);
    }

    @Test
    public void testAcceptIsSuccessful(){
        TestEvent testEvent = new TestEvent(true);

        Message message = mock(Message.class);
        when(message.getPayload()).thenReturn(testEvent);

        boolean accept = this.monitorEventSelector.accept(message);
        Assert.assertTrue("Message should be accepted", accept);
    }

    @Test
    public void testAcceptIsNotSuccessfulAsMessagePayloadIsSubscribedForEvent(){
        Event mock = mock(Event.class);

        Message message = mock(Message.class);
        when(message.getPayload()).thenReturn(mock);

        boolean accept = this.monitorEventSelector.accept(message);
        Assert.assertFalse("Message should NOT be accepted", accept);
    }

    @Test
    public void testAcceptIsNotSuccessfulAsMessagePayloadIsNotEvent(){
        Object o = new Object();

        Message message = mock(Message.class);
        when(message.getPayload()).thenReturn(o);

        boolean accept = this.monitorEventSelector.accept(message);
        Assert.assertFalse("Message should NOT be accepted", accept);
    }
}
