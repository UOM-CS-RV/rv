package mt.edu.um.cs.rv.eventmanager.monitors.registry;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import mt.edu.um.cs.rv.eventmanager.engine.CustomRecipientListRouter;
import mt.edu.um.cs.rv.eventmanager.engine.MonitorEventSelector;
import mt.edu.um.cs.rv.eventmanager.monitors.MonitorInvocationServiceActivator;
import mt.edu.um.cs.rv.monitors.Monitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.integration.channel.AbstractPollableChannel;
import org.springframework.integration.channel.AbstractSubscribableChannel;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.endpoint.PollingConsumer;
import org.springframework.integration.handler.ServiceActivatingHandler;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The Monitor Registry is responsible for registering new monitors within the event system.  The monitor
 * create the necessary threads, channels, beans and bean registrations.
 * Created by dwardu on 23/01/2016.
 */
public class MonitorRegistry
{

    @Autowired
    protected CustomRecipientListRouter recipientListRouter;

    @Autowired
    protected ConfigurableApplicationContext configurableApplicationContext;

    @Autowired
    protected Executor executor;

    @Autowired
    private TaskScheduler taskScheduler;

    private List<PollingConsumer> pollingConsumers = new ArrayList<>();


    //TODO make this proper
    private volatile boolean autoStartup = true;
    private volatile boolean running;

    @PostConstruct
    public void init()
    {
//        String monitor = "monitor-scheduler";
//        ThreadFactory channelThread = new ThreadFactoryBuilder()
//                .setNameFormat(monitor + "-%d")
//                .setDaemon(true)
//                .build();
//        executor = Executors.newSingleThreadExecutor(channelThread);
    }

    public ServiceActivatingHandler registerNewMonitor(Monitor monitor)
    {
        String channelName = monitor + "-" + UUID.randomUUID().toString();

        AbstractPollableChannel pollableChannel = createChannel(channelName);

        MonitorEventSelector selector = new MonitorEventSelector(monitor);

        return registerNewMonitor(monitor, pollableChannel, selector);
    }

    private ServiceActivatingHandler registerNewMonitor(
            Monitor monitor,
            AbstractPollableChannel inputMessageChannel,
            MonitorEventSelector messageSelector)
    {

        MonitorInvocationServiceActivator monitorInvocationSupport = new MonitorInvocationServiceActivator(monitor, configurableApplicationContext);

        ServiceActivatingHandler serviceActivatingHandler = new ServiceActivatingHandler(monitorInvocationSupport, "invokeMonitor");

        PollingConsumer pollingConsumer = new PollingConsumer(inputMessageChannel, serviceActivatingHandler);

        PeriodicTrigger trigger = new PeriodicTrigger(10, TimeUnit.MILLISECONDS);
        pollingConsumer.setTrigger(trigger);
        pollingConsumer.setMaxMessagesPerPoll(1);
        pollingConsumer.setTaskExecutor(executor);
        pollingConsumer.setTaskScheduler(taskScheduler);
        pollingConsumer.setBeanFactory(configurableApplicationContext.getBeanFactory());
        pollingConsumer.setReceiveTimeout(0);
        pollingConsumer.setAutoStartup(autoStartup);
        if (autoStartup)
        {
            pollingConsumer.start();
        }
        pollingConsumers.add(pollingConsumer);


        recipientListRouter.addRecipient(inputMessageChannel, messageSelector);

        String serviceActivatorName = monitor.getName() + "-" + inputMessageChannel.getComponentName() + UUID.randomUUID().toString();
//        configurableApplicationContext.getBeanFactory().registerSingleton(serviceActivatorName, serviceActivatingHandler);

        return serviceActivatingHandler;
    }


    private AbstractPollableChannel createChannel(String inputChannelName)
    {
        QueueChannel queueChannel = new QueueChannel();

        queueChannel.setBeanName(inputChannelName);
        queueChannel.setBeanFactory(configurableApplicationContext);

//        configurableApplicationContext.getBeanFactory().registerSingleton(inputChannelName, queueChannel);

        return queueChannel;
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

    public Executor getExecutor()
    {
        return executor;
    }

    public void setExecutor(Executor executor)
    {
        this.executor = executor;
    }

    public TaskScheduler getTaskScheduler()
    {
        return taskScheduler;
    }

    public void setTaskScheduler(TaskScheduler taskScheduler)
    {
        this.taskScheduler = taskScheduler;
    }

    public boolean isAutoStartup()
    {
        return autoStartup;
    }

    public void setAutoStartup(boolean autoStartup)
    {
        this.autoStartup = autoStartup;
    }

    public void start(){
        for (PollingConsumer pollingConsumer : pollingConsumers)
        {
            pollingConsumer.start();
        }
    }

    public void stop(){
        for (PollingConsumer pollingConsumer : pollingConsumers)
        {
            pollingConsumer.stop();
        }
    }
}
