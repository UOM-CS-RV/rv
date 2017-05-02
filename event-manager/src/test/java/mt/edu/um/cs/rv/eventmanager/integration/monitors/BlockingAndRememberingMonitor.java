package mt.edu.um.cs.rv.eventmanager.integration.monitors;

import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.monitors.results.MonitorResult;
import mt.edu.um.cs.rv.monitors.state.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Semaphore;

/**
 * Created by dwardu on 27/01/2016.
 */
public class BlockingAndRememberingMonitor extends RememberingMonitor {

    private static Logger LOGGER = LoggerFactory.getLogger(BlockingAndRememberingMonitor.class);
    private Semaphore semaphore;

    public BlockingAndRememberingMonitor(String name, Class<? extends Event>[] requiredEvents, Semaphore semaphore) {
        super(name,requiredEvents);
        this.semaphore = semaphore;
    }

    @Override
    public MonitorResult handleEvent(Event e, State s) {
        //sleep
        LOGGER.info("{}[{}] sleeping before handling event [{}]", this.getClass().getSimpleName(), getName(), e);
        try {
            semaphore.acquire();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        LOGGER.info("{}[{}] woke up and handling event [{}]", this.getClass().getSimpleName(), getName(), e);
        super.handleEvent(e, s);
        return MonitorResult.ok();
    }

}
