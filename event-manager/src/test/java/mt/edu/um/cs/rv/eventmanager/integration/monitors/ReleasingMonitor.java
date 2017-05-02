package mt.edu.um.cs.rv.eventmanager.integration.monitors;

import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.monitors.Monitor;
import mt.edu.um.cs.rv.monitors.results.MonitorResult;
import mt.edu.um.cs.rv.monitors.state.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * Created by dwardu on 27/01/2016.
 */
public class ReleasingMonitor implements Monitor {

    private static Logger LOGGER = LoggerFactory.getLogger(ReleasingMonitor.class);

    private String name;
    private HashSet requiredEvents;

    private Semaphore semaphore;

    public ReleasingMonitor(String name, Class<? extends Event>[] requiredEvents) {
        this.name = name;
        this.requiredEvents = new HashSet(Arrays.asList(requiredEvents));
    }

    public ReleasingMonitor(String name, Class<? extends Event>[] requiredEvents, Semaphore semaphore) {
        this(name, requiredEvents);
        this.semaphore = semaphore;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Set<Class<? extends Event>> requiredEvents() {
        return this.requiredEvents;
    }

    @Override
    public MonitorResult handleEvent(Event e, State s) {
        LOGGER.debug("{}[{}] Releasing semaphore after handling event [{}]", this.getClass().getSimpleName(), getName(), e);
        if (semaphore == null) {
            throw new IllegalArgumentException("Semaphore expected to be configured");
        }
        semaphore.release();
        return MonitorResult.ok();
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    public void setSemaphore(Semaphore semaphore) {
        this.semaphore = semaphore;
    }

}
