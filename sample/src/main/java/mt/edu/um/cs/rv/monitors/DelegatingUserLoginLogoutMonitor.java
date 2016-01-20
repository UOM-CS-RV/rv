package mt.edu.um.cs.rv.monitors;

import mt.edu.um.cs.rv.eventmanager.events.Event;
import mt.edu.um.cs.rv.events.LoginEvent;
import mt.edu.um.cs.rv.events.LogoutEvent;
import mt.edu.um.cs.rv.events.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by dwardu on 18/01/2016.
 */
public class DelegatingUserLoginLogoutMonitor implements Monitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelegatingUserLoginLogoutMonitor.class);

    private ConcurrentLinkedQueue<UserLoginLogoutMonitor> monitors;
    private Set<Class<? extends Event>> requiredEvents;

    public DelegatingUserLoginLogoutMonitor(List<UserLoginLogoutMonitor> monitors) {
        this.monitors = new ConcurrentLinkedQueue(monitors);

        requiredEvents = new HashSet();
        requiredEvents.add(LoginEvent.class);
        requiredEvents.add(LogoutEvent.class);
    }

    @Override
    public String getName() {
        return "For Each User - Parent";
    }

    @Override
    public Set<Class<? extends Event>> requiredEvents() {
        return requiredEvents;
    }

    public boolean addUserLoginLogoutMonitor(UserLoginLogoutMonitor monitor) {
        return this.monitors.add(monitor);
    }

    @Override
    public void handleEvent(Event event) {
        //should be safe due to required events set
        UserEvent userEvent = (UserEvent) event;
        LOGGER.info("[{}] delegating {} for user {}", getName(), event.getClass().getName(), userEvent.getUsername());

        int handleCount = 0;
        for (UserLoginLogoutMonitor monitor : monitors) {
            if (monitor.getUsername().equals(userEvent.getUsername())) {
                monitor.handleEvent(event);
                handleCount++;
            }
        }
        LOGGER.info("[{}] delegated {} for user {} to {} UserLoginLogoutMonitor(s)", getName(), event.getClass().getName(), userEvent.getUsername(), handleCount);
    }
}
