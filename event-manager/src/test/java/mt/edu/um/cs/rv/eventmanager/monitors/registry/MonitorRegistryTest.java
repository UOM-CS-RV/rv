package mt.edu.um.cs.rv.eventmanager.monitors.registry;

import mt.edu.um.cs.rv.eventmanager.engine.CustomRecipientListRouter;
import mt.edu.um.cs.rv.eventmanager.engine.MonitorEventSelector;
import mt.edu.um.cs.rv.monitors.Monitor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.integration.channel.AbstractSubscribableChannel;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.core.MessageSelector;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.TaskScheduler;

import java.util.concurrent.Executor;

import static org.mockito.Mockito.*;

/**
 * Created by dwardu on 26/01/2016.
 */
public class MonitorRegistryTest {

    private MonitorRegistry monitorRegistry;

    private CustomRecipientListRouter recipientListRouter;

    private ConfigurableApplicationContext configurableApplicationContext;

    private ConfigurableListableBeanFactory beanFactory;

    @Before
    public void setup() {
        recipientListRouter = mock(CustomRecipientListRouter.class);

        configurableApplicationContext = mock(ConfigurableApplicationContext.class);
        beanFactory = mock(ConfigurableListableBeanFactory.class);
        when(configurableApplicationContext.getBeanFactory()).thenReturn(beanFactory);

        TaskScheduler taskScheduler = mock(TaskScheduler.class);
        Executor executor = mock(Executor.class);

        monitorRegistry = new MonitorRegistry();
        monitorRegistry.setRecipientListRouter(recipientListRouter);
        monitorRegistry.setConfigurableApplicationContext(configurableApplicationContext);
        monitorRegistry.setExecutor(executor);
        monitorRegistry.setTaskScheduler(taskScheduler);
    }

    @Test
    public void testRegisterNewMonitor() {

        Monitor monitor = mock(Monitor.class);

        monitorRegistry.registerNewMonitor(monitor);

        verify(recipientListRouter, times(1)).addRecipient(any(MessageChannel.class), any(MonitorEventSelector.class));

        verify(configurableApplicationContext, times(3)).getBeanFactory();

        verify(beanFactory, times(2)).registerSingleton(any(String.class), any());

    }



}
