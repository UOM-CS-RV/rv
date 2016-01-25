package mt.edu.um.cs.rv.eventmanager.observers;

import mt.edu.um.cs.rv.eventmanager.si.EventMessageSender;
import mt.edu.um.cs.rv.events.Event;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


/**
 * Created by dwardu on 25/01/2016.
 */
public class DirectInvocationEventObserverTest {


    EventMessageSender eventMessageSender;

    DirectInvocationEventObserver directInvocationEventObserver;

    Event e;

    @Before
    public void setup() {
        eventMessageSender = mock(EventMessageSender.class);

        directInvocationEventObserver = new DirectInvocationEventObserver(eventMessageSender);

        e = new TestEvent();
    }

    @Test
    public void testObserveEventSendsEvent() {
        directInvocationEventObserver.observeEvent(e);

        verify(eventMessageSender, times(1)).send(e);
    }

}
