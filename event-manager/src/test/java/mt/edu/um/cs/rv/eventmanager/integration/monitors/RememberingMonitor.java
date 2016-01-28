package mt.edu.um.cs.rv.eventmanager.integration.monitors;

import com.google.common.collect.ImmutableList;
import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.monitors.Monitor;
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
    public void handleEvent(Event e) {
        LOGGER.info("{}[{}] handling event [{}]", this.getClass().getSimpleName(), getName(), e);
        this.allEvents.add(e);
    }

    public List<Event> getAllEvents(){
        return ImmutableList.copyOf(allEvents);
    }
}
