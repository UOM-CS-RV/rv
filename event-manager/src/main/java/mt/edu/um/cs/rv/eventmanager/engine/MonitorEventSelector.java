package mt.edu.um.cs.rv.eventmanager.engine;

import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.monitors.Monitor;
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
        Object payloadObject = message.getPayload();
        if (Event.class.isInstance(payloadObject)) {

            Event payload = (Event) message.getPayload();
            return monitor.requiredEvents().contains(payload.getClass());

        } else {
            return false;
        }
    }

    public Monitor getMonitor() {
        return monitor;
    }
}
