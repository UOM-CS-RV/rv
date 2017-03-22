package mt.edu.um.cs.rv.eventmanager.observers;

import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.monitors.results.MonitorResult;

/**
 * Created by dwardu on 19/01/2016.
 */
public interface EventObserver {
    MonitorResult observeEvent(Event e);
}
