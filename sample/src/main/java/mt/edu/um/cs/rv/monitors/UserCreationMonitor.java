package mt.edu.um.cs.rv.monitors;

import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.events.UserCreatedEvent;
import mt.edu.um.cs.rv.monitors.results.MonitorResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.MessagingException;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by dwardu on 20/01/2016.
 */
public class UserCreationMonitor implements Monitor {

    private static Logger LOGGER = LoggerFactory.getLogger(UserCreationMonitor.class);

    private DelegatingUserLoginLogoutMonitor delegatingUserLoginLogoutMonitor;

    private Set<Class<? extends Event>> requiredEvents;


    public UserCreationMonitor(DelegatingUserLoginLogoutMonitor delegatingUserLoginLogoutMonitor) {
        this.delegatingUserLoginLogoutMonitor = delegatingUserLoginLogoutMonitor;
        requiredEvents = new HashSet();
        requiredEvents.add(UserCreatedEvent.class);
    }

    @Override
    public String getName() {
        return "UserCreationMonitor";
    }

    @Override
    public Set<Class<? extends Event>> requiredEvents() {
        return this.requiredEvents;
    }

    @Override
    public MonitorResult handleEvent(Event event) throws MessagingException {
        LOGGER.info("Processing {}", event.getClass().getName());
        UserCreatedEvent userCreatedEvent = (UserCreatedEvent) event;

        UserLoginLogoutMonitor userLoginLogoutMonitor = new UserLoginLogoutMonitor(userCreatedEvent.getUsername());
        delegatingUserLoginLogoutMonitor.addUserLoginLogoutMonitor(userLoginLogoutMonitor);
        return MonitorResult.ok();
    }
}
