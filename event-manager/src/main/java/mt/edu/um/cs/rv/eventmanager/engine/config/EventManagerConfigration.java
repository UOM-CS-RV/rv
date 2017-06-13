package mt.edu.um.cs.rv.eventmanager.engine.config;

import mt.edu.um.cs.rv.eventmanager.adaptors.EventAdaptorConfiguration;
import mt.edu.um.cs.rv.eventmanager.engine.AfterAllMessagesInGroupReleaseStrategy;
import mt.edu.um.cs.rv.eventmanager.engine.CustomRecipientListRouter;
import mt.edu.um.cs.rv.eventmanager.engine.EventMessageSender;
import mt.edu.um.cs.rv.eventmanager.monitors.registry.MonitorRegistry;
import mt.edu.um.cs.rv.eventmanager.monitors.registry.NoInterestedMonitorsHandler;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.integration.aggregator.AggregatingMessageHandler;
import org.springframework.integration.aggregator.DefaultAggregatingMessageGroupProcessor;
import org.springframework.integration.aggregator.HeaderAttributeCorrelationStrategy;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.AsyncMessagingTemplate;
import org.springframework.integration.endpoint.PollingConsumer;
import org.springframework.integration.handler.ServiceActivatingHandler;
import org.springframework.integration.scattergather.ScatterGatherHandler;
import org.springframework.integration.store.SimpleMessageStore;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * Created by dwardu on 18/01/2016.
 */
@Configuration
@EnableIntegration
@Import({EventAdaptorConfiguration.class})
public class EventManagerConfigration {


    public static final String EVENT_MANAGER_REQUEST_CHANNEL = "eventManagerRequestChannel";
    public static final String EVENT_MANAGER_RESPONSE_CHANNEL = "eventManagerResponseChannel";

    //////////////////////// SCATTERER ////////////////////////

    //set up scatter gather distribution - this will be responsible for running the show
    @Bean
    @ServiceActivator(inputChannel = EVENT_MANAGER_REQUEST_CHANNEL)
    public MessageHandler scatterGatherDistribution() {
        ScatterGatherHandler handler = new ScatterGatherHandler(distributor(), gatherer());

        //output channel is not set, as output channel will be defined by the incoming message to be used as a sync block
        //handler.setOutputChannel(noop());
        return handler;
    }

    @Bean
    public MessageHandler distributor() {
        return recipientListRouter();
    }

    public CustomRecipientListRouter recipientListRouter() {
        CustomRecipientListRouter router = new CustomRecipientListRouter(noInterestedMonitorsQueueChannel());
        router.setApplySequence(true);
        return router;
    }

    @Bean
    public AsyncMessagingTemplate inputMessagingTemplate() {
        return new AsyncMessagingTemplate();
    }

    @Bean
    public EventMessageSender eventMessageSender() {
        return new EventMessageSender(inputMessagingTemplate());
    }


    //////////////////////// GATHERER ////////////////////////

    @Bean
    public MessageHandler gatherer() {
        return new AggregatingMessageHandler(
                new DefaultAggregatingMessageGroupProcessor(),
                new SimpleMessageStore(),
                new HeaderAttributeCorrelationStrategy(IntegrationMessageHeaderAccessor.CORRELATION_ID),
                new AfterAllMessagesInGroupReleaseStrategy());
    }

    //////////////////////// MONITOR REGISTRY ////////////////////////
    @Bean
    public MonitorRegistry monitorRegistry() {
        return new MonitorRegistry();
    }

    @Bean
    public QueueChannel noInterestedMonitorsQueueChannel() {
        return new QueueChannel();
    }

    @Bean
    public ServiceActivatingHandler noInterestedMonitorsServiceActivatingHandler(
            Executor executor, TaskScheduler taskScheduler, ConfigurableApplicationContext configurableApplicationContext
    ) {

        NoInterestedMonitorsHandler noInterestedMonitorsHandler = new NoInterestedMonitorsHandler();

        ServiceActivatingHandler serviceActivatingHandler = new ServiceActivatingHandler(noInterestedMonitorsHandler, "handleEvent");

        PollingConsumer pollingConsumer = new PollingConsumer(noInterestedMonitorsQueueChannel(), serviceActivatingHandler);

        PeriodicTrigger trigger = new PeriodicTrigger(10, TimeUnit.MILLISECONDS);
        pollingConsumer.setTrigger(trigger);
        pollingConsumer.setMaxMessagesPerPoll(1);
        pollingConsumer.setTaskExecutor(executor);
        pollingConsumer.setTaskScheduler(taskScheduler);
        pollingConsumer.setBeanFactory(configurableApplicationContext.getBeanFactory());
        pollingConsumer.setReceiveTimeout(0);

        pollingConsumer.start();

        return serviceActivatingHandler;
    }


}

