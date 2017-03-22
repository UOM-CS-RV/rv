package mt.edu.um.cs.rv.eventmanager.integration.monitors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.monitors.Monitor;
import mt.edu.um.cs.rv.monitors.results.MonitorResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by dwardu on 27/01/2016.
 */
public class RememberingMonitor implements Monitor {

    private static Logger LOGGER = LoggerFactory.getLogger(RememberingMonitor.class);

    private String name;
    private Class<? extends Event>[] requiredEvents;

    protected Set<Event> allEvents = new HashSet<>();
    protected List<Event> allOrderedEvents = new ArrayList<>();

    public RememberingMonitor(String name, Class<? extends Event>[] requiredEvents) {
        this.name = name;
        this.requiredEvents = requiredEvents;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<Class<? extends Event>> requiredEvents() {
        return new HashSet(Arrays.asList(requiredEvents));
    }

    @Override
    public MonitorResult handleEvent(Event e) {
        LOGGER.debug("{}[{}] handling event [{}]", this.getClass().getSimpleName(), getName(), e);
        this.allEvents.add(e);
        this.allOrderedEvents.add(e);
        return MonitorResult.ok();
    }

    public Set<Event> getAllEvents() {
        return ImmutableSet.copyOf(allEvents);
    }

    public List<Event> getAllOrderedEvents() {
        return ImmutableList.copyOf(allOrderedEvents);
    }

    @Override
    public String toString() {
        return name;
    }
}
