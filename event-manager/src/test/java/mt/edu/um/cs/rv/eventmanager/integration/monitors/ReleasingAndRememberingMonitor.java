package mt.edu.um.cs.rv.eventmanager.integration.monitors;

import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.monitors.results.MonitorResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Semaphore;

/**
 * Created by dwardu on 27/01/2016.
 */
public class ReleasingAndRememberingMonitor extends RememberingMonitor {

    private static Logger LOGGER = LoggerFactory.getLogger(ReleasingAndRememberingMonitor.class);
    private Semaphore semaphore;

    public ReleasingAndRememberingMonitor(String name, Class<? extends Event>[] requiredEvents) {
        super(name, requiredEvents);
    }

    public ReleasingAndRememberingMonitor(String name, Class<? extends Event>[] requiredEvents, Semaphore semaphore) {
        super(name, requiredEvents);
        this.semaphore = semaphore;
    }

    @Override
    public MonitorResult handleEvent(Event e) {
        super.handleEvent(e);
        LOGGER.debug("{}[{}] Releasing semaphore after handling event [{}]", this.getClass().getSimpleName(), getName(), e);
        if (semaphore == null){
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
