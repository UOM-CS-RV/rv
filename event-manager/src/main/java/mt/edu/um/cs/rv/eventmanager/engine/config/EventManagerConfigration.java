package mt.edu.um.cs.rv.eventmanager.engine.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import mt.edu.um.cs.rv.eventmanager.adaptors.EventAdaptorConfiguration;
import mt.edu.um.cs.rv.eventmanager.engine.AfterAllMessagesInGroupReleaseStrategy;
import mt.edu.um.cs.rv.eventmanager.engine.CustomRecipientListRouter;
import mt.edu.um.cs.rv.eventmanager.engine.EventMessageSender;
import mt.edu.um.cs.rv.eventmanager.monitors.registry.MonitorRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.integration.aggregator.AggregatingMessageHandler;
import org.springframework.integration.aggregator.DefaultAggregatingMessageGroupProcessor;
import org.springframework.integration.aggregator.HeaderAttributeCorrelationStrategy;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.channel.NullChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.scattergather.ScatterGatherHandler;
import org.springframework.integration.store.SimpleMessageStore;
import org.springframework.messaging.MessageHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by dwardu on 18/01/2016.
 */
@Configuration
@EnableIntegration
@Import({EventAdaptorConfiguration.class})
public class EventManagerConfigration
{


    //////////////////////// SCATTERER ////////////////////////

    //set up scatter gather distribution - this will be responsible for running the show
    @Bean
    @ServiceActivator(inputChannel = "eventMessageRequestChannel")
    public MessageHandler scatterGatherDistribution()
    {
        ScatterGatherHandler handler = new ScatterGatherHandler(distributor(), gatherer());

        //output channel is not set, as output channel will be defined by the incoming message to be used as a sync block
        //handler.setOutputChannel(noop());
        return handler;
    }

    @Bean
    public NullChannel noop()
    {
        return new NullChannel();
    }

    @Bean
    ExecutorChannel eventMessageRequestChannel()
    {
        ThreadFactory channelThread = new ThreadFactoryBuilder()
                .setNameFormat("EventMessageRequestChannel")
                .setDaemon(true)
                .build();
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor(channelThread);
        return new ExecutorChannel(singleThreadExecutor);
    }

    @Bean
    public MessageHandler distributor()
    {
        return recipientListRouter();
    }

    public CustomRecipientListRouter recipientListRouter()
    {
        CustomRecipientListRouter router = new CustomRecipientListRouter();
        router.setApplySequence(true);
        return router;
    }

    @Bean
    public MessagingTemplate inputMessagingTemplate()
    {
        return new MessagingTemplate();
    }

    @Bean
    public EventMessageSender eventMessageSender()
    {
        return new EventMessageSender(inputMessagingTemplate(), eventMessageRequestChannel(), noop());
    }


    //////////////////////// GATHERER ////////////////////////

    @Bean
    public MessageHandler gatherer()
    {
        return new AggregatingMessageHandler(
                new DefaultAggregatingMessageGroupProcessor(),
                new SimpleMessageStore(),
                new HeaderAttributeCorrelationStrategy(IntegrationMessageHeaderAccessor.CORRELATION_ID),
                new AfterAllMessagesInGroupReleaseStrategy());
    }

    //////////////////////// MONITOR REGISTRY ////////////////////////
    @Bean
    public MonitorRegistry monitorRegistry()
    {
        return new MonitorRegistry();
    }


}

