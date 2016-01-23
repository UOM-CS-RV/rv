package mt.edu.um.cs.rv.monitors;

import mt.edu.um.cs.rv.events.Event;

/**
 * Created by dwardu on 19/01/2016.
 */
public class MonitorInvocationSupport {
    private Monitor monitor;

    public MonitorInvocationSupport(Monitor monitor) {
        this.monitor = monitor;
    }

    //TODO change return type to a more elaborate type
    public Boolean invokeMonitor(Event e) {

        monitor.handleEvent(e);
        return Boolean.TRUE;

    }
}
