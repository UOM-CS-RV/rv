package mt.edu.um.cs.rv.monitors;

import mt.edu.um.cs.rv.eventmanager.events.Event;
import mt.edu.um.cs.rv.events.LoginEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.MessagingException;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by dwardu on 18/01/2016.
 */
public class GlobalUserLoginCounterMonitor implements Monitor {

    private static Logger LOGGER = LoggerFactory.getLogger(GlobalUserLoginCounterMonitor.class);

    private String name;
    private Set<Class<? extends Event>> requiredEvents;
    private AtomicLong loginCount = new AtomicLong();

    public GlobalUserLoginCounterMonitor() {
        requiredEvents = new HashSet();
        requiredEvents.add(LoginEvent.class);
    }

    @Override
    public String getName() {
        return "GlobalUserLoginCounterMonitor";
    }

    @Override
    public Set<Class<? extends Event>> requiredEvents() {
        return this.requiredEvents;
    }

    @Override
    public void handleEvent(Event event) throws MessagingException {
        long l = loginCount.incrementAndGet();
        LOGGER.info("Processing {}. Total logins observed: {}", event.getClass().getName(), l);
    }
}
