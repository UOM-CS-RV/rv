package mt.edu.um.cs.rv.eventmanager.monitors.registry;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import mt.edu.um.cs.rv.eventmanager.engine.CustomRecipientListRouter;
import mt.edu.um.cs.rv.eventmanager.engine.MonitorEventSelector;
import mt.edu.um.cs.rv.eventmanager.monitors.MonitorInvocationServiceActivator;
import mt.edu.um.cs.rv.monitors.Monitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.integration.channel.AbstractSubscribableChannel;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.handler.ServiceActivatingHandler;

import javax.annotation.PostConstruct;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by dwardu on 23/01/2016.
 */
public class MonitorRegistry
{

    @Autowired
    protected CustomRecipientListRouter recipientListRouter;

    @Autowired
    protected ConfigurableApplicationContext configurableApplicationContext;

    private boolean withActor = false;

    ExecutorService executor;

    @PostConstruct
    public void init()
    {
        String monitor = "monitor-scheduler";
        ThreadFactory channelThread = new ThreadFactoryBuilder()
                .setNameFormat(monitor + "-%d")
                .setDaemon(true)
                .build();
        executor = Executors.newSingleThreadExecutor(channelThread);
    }

    public ServiceActivatingHandler registerNewMonitor(Monitor monitor)
    {
        String channelName = monitor + "-" + UUID.randomUUID().toString();

        AbstractSubscribableChannel subscribableChannel = createChannel(channelName, executor);

        MonitorEventSelector selector = new MonitorEventSelector(monitor);

        return registerNewMonitor(monitor, subscribableChannel, selector);
    }

    private ServiceActivatingHandler registerNewMonitor(
            Monitor monitor,
            AbstractSubscribableChannel inputMessageChannel,
            MonitorEventSelector messageSelector)
    {

        MonitorInvocationServiceActivator monitorInvocationSupport = new MonitorInvocationServiceActivator(monitor, configurableApplicationContext, withActor);

        ServiceActivatingHandler serviceActivatingHandler = new ServiceActivatingHandler(monitorInvocationSupport, "invokeMonitor");

        inputMessageChannel.subscribe(serviceActivatingHandler);

        recipientListRouter.addRecipient(inputMessageChannel, messageSelector);

        String serviceActivatorName = monitor.getName() + "-" + inputMessageChannel.getComponentName() + UUID.randomUUID().toString();
        configurableApplicationContext.getBeanFactory().registerSingleton(serviceActivatorName, serviceActivatingHandler);

        return serviceActivatingHandler;
    }


    private AbstractSubscribableChannel createChannel(String inputChannelName, Executor executor)
    {
        ExecutorChannel subscribableChannel = new ExecutorChannel(executor);

        subscribableChannel.setBeanName(inputChannelName);
        subscribableChannel.setBeanFactory(configurableApplicationContext);

        configurableApplicationContext.getBeanFactory().registerSingleton(inputChannelName, subscribableChannel);

        return subscribableChannel;
    }

    public CustomRecipientListRouter getRecipientListRouter()
    {
        return recipientListRouter;
    }

    public void setRecipientListRouter(CustomRecipientListRouter recipientListRouter)
    {
        this.recipientListRouter = recipientListRouter;
    }

    public ConfigurableApplicationContext getConfigurableApplicationContext()
    {
        return configurableApplicationContext;
    }

    public void setConfigurableApplicationContext(ConfigurableApplicationContext configurableApplicationContext)
    {
        this.configurableApplicationContext = configurableApplicationContext;
    }

    public void setWithActor(boolean withActor)
    {
        this.withActor = withActor;
    }
}
