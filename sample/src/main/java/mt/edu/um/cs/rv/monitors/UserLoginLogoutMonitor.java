package mt.edu.um.cs.rv.monitors;

import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.events.LoginEvent;
import mt.edu.um.cs.rv.events.LogoutEvent;
import mt.edu.um.cs.rv.monitors.results.MonitorResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.MessagingException;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by dwardu on 20/01/2016.
 */
public class UserLoginLogoutMonitor implements Monitor {

    private static Logger LOGGER = LoggerFactory.getLogger(UserLoginLogoutMonitor.class);

    private String username;
    private Set<Class<? extends Event>> requiredEvents;
    private AtomicLong loginCount = new AtomicLong();
    private AtomicLong logoutCount = new AtomicLong();

    public UserLoginLogoutMonitor(String username) {
        this.username = username;

        requiredEvents = new HashSet();
        requiredEvents.add(LoginEvent.class);
        requiredEvents.add(LogoutEvent.class);
    }

    @Override
    public String getName() {
        return "UserLoginLogoutMonitor for " + username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public Set<Class<? extends Event>> requiredEvents() {
        return this.requiredEvents;
    }

    @Override
    public MonitorResult handleEvent(Event event) throws MessagingException {
        if (LoginEvent.class.equals(event.getClass())) {
            long l = loginCount.incrementAndGet();
            LOGGER.info("[{}] processing {} for user {}. Total logins observed for user {}: {}", getName(), event.getClass().getName(), username, username, l);
        } else if (LogoutEvent.class.equals(event.getClass())) {
            long l = logoutCount.incrementAndGet();
            LOGGER.info("[{}] processing {} for user {}. Total logouts observed for user {}: {}", getName(), event.getClass().getName(), username, username, l);
        } else {
            throw new IllegalStateException();
        }
        return MonitorResult.ok();
    }
}
