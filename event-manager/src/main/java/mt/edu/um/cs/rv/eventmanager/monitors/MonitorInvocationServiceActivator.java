package mt.edu.um.cs.rv.eventmanager.monitors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.monitors.Monitor;
import org.springframework.context.ApplicationContext;

import static mt.edu.um.cs.rv.eventmanager.monitors.actors.SpringExtension.SpringExtProvider;

/**
 * Created by dwardu on 19/01/2016.
 */
public class MonitorInvocationServiceActivator
{

    private Monitor monitor;

    private ActorRef monitorActor;

    private ApplicationContext applicationContext;

    private boolean withActor;

    public MonitorInvocationServiceActivator(Monitor monitor, ApplicationContext applicationContext, boolean withActor)
    {
        this.monitor = monitor;
        this.applicationContext = applicationContext;
        this.withActor = withActor;

        ActorSystem actorSystem = applicationContext.getBean(ActorSystem.class);

        String actorName = monitor.getName() + "Actor";
        monitorActor = actorSystem.actorOf(
                SpringExtProvider.get(actorSystem).props("MonitorInvocationActor", monitor), actorName);
    }

    //TODO change return type to a more elaborate type
    public Boolean invokeMonitor(Event e)
    {

        if (withActor)
            this.monitorActor.tell(e, null);
        else
            monitor.handleEvent(e);


        return Boolean.TRUE;
    }
}
