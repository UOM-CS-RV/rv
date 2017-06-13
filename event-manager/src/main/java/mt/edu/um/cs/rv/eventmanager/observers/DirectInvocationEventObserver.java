package mt.edu.um.cs.rv.eventmanager.observers;

import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.eventmanager.engine.EventMessageSender;
import mt.edu.um.cs.rv.monitors.results.MonitorResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by dwardu on 19/01/2016.
 */
public class DirectInvocationEventObserver implements EventObserver, ApplicationContextAware
{

    private static Logger LOGGER = LoggerFactory.getLogger(DirectInvocationEventObserver.class);
    EventMessageSender eventMessageSender;

    private static DirectInvocationEventObserver directInvocationEventObserver;

    public DirectInvocationEventObserver(EventMessageSender eventMessageSender) {
        this.eventMessageSender = eventMessageSender;
    }

    @Override
    public Future<MonitorResult<?>> observeEvent(Event e) {
        return eventMessageSender.send(e);
    }

    /**
     * Provides a way to access the spring singleton in a static manner.
     * @return The spring singleton bean for this class
     */
    public static DirectInvocationEventObserver getInstance() {
        if (DirectInvocationEventObserver.directInvocationEventObserver == null){
            throw new IllegalStateException("DirectInvocationEventObserver has not been initialised yet.");
        }
        return DirectInvocationEventObserver.directInvocationEventObserver;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        DirectInvocationEventObserver.directInvocationEventObserver = this;
    }
}
