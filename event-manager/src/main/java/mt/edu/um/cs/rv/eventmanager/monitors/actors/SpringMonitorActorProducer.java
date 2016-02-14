package mt.edu.um.cs.rv.eventmanager.monitors.actors;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import mt.edu.um.cs.rv.monitors.Monitor;
import org.springframework.context.ApplicationContext;

/**
 * Created by edwardmallia on 11/02/2016.
 */

/**
 * An actor producer that lets Spring create the Actor instances.
 */
public class SpringMonitorActorProducer implements IndirectActorProducer
{

    final ApplicationContext applicationContext;

    final String actorBeanName;
    final Monitor monitor;

    public SpringMonitorActorProducer(ApplicationContext applicationContext,
                                      String actorBeanName,
                                      Monitor monitor)
    {
        this.applicationContext = applicationContext;
        this.actorBeanName = actorBeanName;
        this.monitor = monitor;
    }

    @Override
    public Actor produce()
    {
        MonitorInvocationActor actor = (MonitorInvocationActor) applicationContext.getBean(actorBeanName);
        actor.setMonitor(monitor);
        return actor;
    }

    @Override
    public Class<? extends Actor> actorClass()
    {
        return (Class<? extends Actor>) applicationContext.getType(actorBeanName);
    }
}

