package mt.edu.um.cs.rv.eventmanager.monitors.registry;

import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.monitors.results.MonitorResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dwardu on 13/06/2017.
 */
public class NoInterestedMonitorsHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoInterestedMonitorsHandler.class);

    public MonitorResult handleEvent(Event e)
    {
        LOGGER.debug("No interested monitors in event [{}]. Handling by returning OK.");
        return MonitorResult.ok();
    }
}
