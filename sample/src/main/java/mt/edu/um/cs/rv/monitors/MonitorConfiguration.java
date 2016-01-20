package mt.edu.um.cs.rv.monitors;

import mt.edu.um.cs.rv.eventmanager.si.CustomRecipientListRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.integration.channel.AbstractSubscribableChannel;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.core.MessageSelector;
import org.springframework.integration.filter.ExpressionEvaluatingSelector;
import org.springframework.integration.handler.ServiceActivatingHandler;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by dwardu on 18/01/2016.
 */
@Configuration
public class MonitorConfiguration {

    @Autowired
    CustomRecipientListRouter recipientListRouter;

    @Autowired
    Executor executor;


    @Bean
    public Monitor globalLoginMonitor() {
        Monitor monitor = new GlobalUserLoginCounterMonitor();
        activateMonitor(monitor, globalLoginMonitorChannel(), new MonitorEventSelector(monitor));
        return monitor;
    }

    @Bean
    public ExecutorChannel globalLoginMonitorChannel() {
        return new ExecutorChannel(executor);
    }


    @Bean
    public DelegatingUserLoginLogoutMonitor forEachUserMonitor() {

        List userMonitors = Arrays.asList(new UserLoginLogoutMonitor("a@example.com"), new UserLoginLogoutMonitor("b@example.com"));

        DelegatingUserLoginLogoutMonitor monitor = new DelegatingUserLoginLogoutMonitor(userMonitors);

        activateMonitor(monitor, forEachUserMonitorChannel(), new MonitorEventSelector(monitor));

        return monitor;
    }

    @Bean
    public ExecutorChannel forEachUserMonitorChannel() {
        return new ExecutorChannel(executor);
    }


    @Bean
    public Monitor userCreationMonitor() {
        Monitor monitor = new UserCreationMonitor(forEachUserMonitor());
        activateMonitor(monitor, userCreationMonitorChannel(), new MonitorEventSelector(monitor));
        return monitor;
    }

    @Bean
    public ExecutorChannel userCreationMonitorChannel() {
        return new ExecutorChannel(executor);
    }



    @Bean
    @Scope("prototype")
    public ServiceActivatingHandler activateMonitor(
            Monitor monitor,
            AbstractSubscribableChannel inputMessageChannel,
            MessageSelector messageSelector) {


        MonitorInvocationSupport monitorInvocationSupport = new MonitorInvocationSupport(monitor);

        ServiceActivatingHandler serviceActivatingHandler = new ServiceActivatingHandler(monitorInvocationSupport, "invokeMonitor");

        inputMessageChannel.subscribe(serviceActivatingHandler);

//        recipientListRouter.addRecipient(inputMessageChannel.getComponentName());
        //TODO - for either enable the message selector string or enable adding a  RecipientListRouter.Recipient
        recipientListRouter.addRecipient(inputMessageChannel, messageSelector);

        return serviceActivatingHandler;
    }

}
