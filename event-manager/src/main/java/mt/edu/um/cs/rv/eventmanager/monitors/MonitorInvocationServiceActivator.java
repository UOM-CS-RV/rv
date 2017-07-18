package mt.edu.um.cs.rv.eventmanager.monitors;

import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.monitors.Monitor;
import mt.edu.um.cs.rv.monitors.results.MonitorResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.Serializable;

/**
 * Created by dwardu on 19/01/2016.
 */
public class MonitorInvocationServiceActivator
{
    private static Logger LOGGER = LoggerFactory.getLogger(MonitorInvocationServiceActivator.class);

    private Monitor monitor;

    private ApplicationContext applicationContext;

    public MonitorInvocationServiceActivator(Monitor monitor, ApplicationContext applicationContext)
    {
        this.monitor = monitor;
        this.applicationContext = applicationContext;
    }
    
    public MonitorResult invokeMonitor(Event e)
    {
        try {
            return monitor.handleEvent(e, null);
        }
        catch (Throwable throwable){
            String msg = String.format("Unexpected Throwable occurred of type - [%s]", throwable.getClass());
            LOGGER.error("{}. Creating and returning a FAILURE MonitorResult.", msg, throwable);
            MonitorResult<Serializable> failure = MonitorResult.failure(null, throwable);
            LOGGER.error("Created FAILURE MonitorResult [{}]", failure);
            return failure;
        }
    }
}
