package mt.edu.um.cs.rv.eventmanager.si;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.integration.aggregator.AggregatingMessageHandler;
import org.springframework.integration.aggregator.DefaultAggregatingMessageGroupProcessor;
import org.springframework.integration.aggregator.HeaderAttributeCorrelationStrategy;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.channel.NullChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.scattergather.ScatterGatherHandler;
import org.springframework.integration.store.SimpleMessageStore;
import org.springframework.messaging.MessageHandler;

import java.util.concurrent.Executor;

/**
 * Created by dwardu on 18/01/2016.
 */
@Configuration
@Import({EventAdaptorConfiguration.class})
public class EventManagerConfigration {


    //////////////////////// SCATTERER ////////////////////////

    //set up scatter gather distribution - this will be responsible for running the show
    @Bean
    @ServiceActivator(inputChannel = "eventMessageRequestChannel")
    public MessageHandler scatterGatherDistribution() {
        ScatterGatherHandler handler = new ScatterGatherHandler(distributor(), gatherer());

        //output channel is not set, as output channel will be defined by the incoming message to be used as a sync block
        //handler.setOutputChannel(noop());
        return handler;
    }

    @Bean
    public NullChannel noop() {
        return new NullChannel();
    }

    @Bean
    ExecutorChannel eventMessageRequestChannel(Executor executor) {
        return new ExecutorChannel(executor);
    }

    @Bean
    public MessageHandler distributor() {
        return recipientListRouter();
    }

    @Bean
    public CustomRecipientListRouter recipientListRouter() {
        CustomRecipientListRouter router = new CustomRecipientListRouter();
        router.setApplySequence(true);
        return router;
    }

    @Bean
    public MessagingTemplate inputMessagingTemplate() {
        return new MessagingTemplate();
    }

    @Bean
    public EventMessageSender eventMessageSender(Executor executor) {
        return new EventMessageSender(inputMessagingTemplate(), eventMessageRequestChannel(executor), noop());
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
    public MonitorRegistry monitorRegistry(){
         return new MonitorRegistry();
    }

}

