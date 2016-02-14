package mt.edu.um.cs.rv.eventmanager.monitors.actors;

import akka.actor.UntypedActor;
import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.monitors.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by edwardmallia on 11/02/2016.
 */
@Component("MonitorInvocationActor")
@Scope("prototype")
public class MonitorInvocationActor extends UntypedActor
{

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorInvocationActor.class);

    @Autowired
    ApplicationContext applicationContext;

    private Monitor monitor;

    public void setMonitor(Monitor monitor)
    {
        this.monitor = monitor;
    }

    //TODO change return type to a more elaborate type
    public Boolean invokeMonitor(Event e)
    {
        monitor.handleEvent(e);
        return Boolean.TRUE;

    }

    @Override
    public void onReceive(Object o) throws Exception
    {
        LOGGER.info("in the actor");
        monitor.handleEvent((Event) o);

    }
}
