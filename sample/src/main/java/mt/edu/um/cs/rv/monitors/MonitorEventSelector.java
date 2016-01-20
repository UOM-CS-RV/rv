package mt.edu.um.cs.rv.monitors;

import mt.edu.um.cs.rv.eventmanager.events.Event;
import org.springframework.integration.core.GenericSelector;
import org.springframework.integration.core.MessageSelector;
import org.springframework.messaging.Message;

/**
 * Created by dwardu on 20/01/2016.
 */
public class MonitorEventSelector implements MessageSelector {

    private Monitor monitor;

    public MonitorEventSelector(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public boolean accept(Message<?> message) {
        Event payload = (Event) message.getPayload();

        return monitor.requiredEvents().contains(payload.getClass());
    }
}
