package mt.edu.um.cs.rv.monitors;

import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.monitors.results.MonitorResult;

import java.util.Set;

/**
 * Created by dwardu on 18/01/2016.
 */
public interface Monitor {

    String getName();

    Set<Class<? extends Event>> requiredEvents();

    MonitorResult handleEvent(Event e);
}
