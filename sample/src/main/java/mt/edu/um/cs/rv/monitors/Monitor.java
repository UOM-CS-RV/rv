package mt.edu.um.cs.rv.monitors;

import mt.edu.um.cs.rv.eventmanager.events.Event;

import java.util.Set;

/**
 * Created by dwardu on 18/01/2016.
 */
public interface Monitor {

    String getName();

    Set<Class<? extends Event>> requiredEvents();

    void handleEvent(Event e);
}
