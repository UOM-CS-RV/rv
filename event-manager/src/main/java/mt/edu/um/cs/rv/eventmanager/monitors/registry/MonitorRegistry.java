package mt.edu.um.cs.rv.eventmanager.monitors.registry;

import mt.edu.um.cs.rv.eventmanager.engine.CustomRecipientListRouter;
import mt.edu.um.cs.rv.eventmanager.engine.MonitorEventSelector;
import mt.edu.um.cs.rv.eventmanager.monitors.MonitorInvocationSupport;
import mt.edu.um.cs.rv.monitors.Monitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.integration.channel.AbstractSubscribableChannel;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSelector;
import org.springframework.integration.handler.ServiceActivatingHandler;

import java.util.UUID;

/**
 * Created by dwardu on 23/01/2016.
 */
public class MonitorRegistry {

    @Autowired
    CustomRecipientListRouter recipientListRouter;

    @Autowired
    ConfigurableApplicationContext configurableApplicationContext;

    public ServiceActivatingHandler registerNewMonitor(Monitor monitor){
        String channelName = monitor + "-" + UUID.randomUUID().toString();

        DirectChannel directInputChannel = createDirectInputChannel(channelName);

        MonitorEventSelector selector = new MonitorEventSelector(monitor);

        return registerNewMonitor(monitor, directInputChannel, selector);
    }

    //////////////////////// New Monitor Registration ////////////////////////
//    @Bean
//    @Scope("prototype")
    public ServiceActivatingHandler registerNewMonitor(
            Monitor monitor,
            AbstractSubscribableChannel inputMessageChannel,
            MessageSelector messageSelector) {


        MonitorInvocationSupport monitorInvocationSupport = new MonitorInvocationSupport(monitor);

        ServiceActivatingHandler serviceActivatingHandler = new ServiceActivatingHandler(monitorInvocationSupport, "invokeMonitor");

        inputMessageChannel.subscribe(serviceActivatingHandler);

//        recipientListRouter.addRecipient(inputMessageChannel.getComponentName());
        //TODO - either enable the message selector string or enable adding a  RecipientListRouter.Recipient
        recipientListRouter.addRecipient(inputMessageChannel, messageSelector);


        String serviceActivatorName = monitor.getName() + "-" + inputMessageChannel.getComponentName() + UUID.randomUUID().toString();
        configurableApplicationContext.getBeanFactory().registerSingleton(serviceActivatorName, serviceActivatingHandler);

        return serviceActivatingHandler;
    }

    private DirectChannel createDirectInputChannel(String inputChannelName) {
        DirectChannel directChannel = new DirectChannel();

        directChannel.setBeanName(inputChannelName);
        directChannel.setBeanFactory(configurableApplicationContext);

        configurableApplicationContext.getBeanFactory().registerSingleton(inputChannelName, directChannel);

        return directChannel;
    }

}
