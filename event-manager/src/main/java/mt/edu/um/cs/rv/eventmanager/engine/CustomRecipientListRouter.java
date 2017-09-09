package mt.edu.um.cs.rv.eventmanager.engine;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import mt.edu.um.cs.rv.events.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.router.RecipientListRouter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by dwardu on 20/01/2016.
 */
public class CustomRecipientListRouter extends RecipientListRouter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomRecipientListRouter.class);

    private boolean useLookupMap = false;

    private ConcurrentLinkedQueue<Recipient> recipientList = null;

    private Multimap eventToMessageChannelMap = ArrayListMultimap.create();

    private QueueChannel noInterestedMonitorsQueueChannel;
    
    public CustomRecipientListRouter(QueueChannel noInterestedMonitorsQueueChannel) {
        super();
        this.noInterestedMonitorsQueueChannel = noInterestedMonitorsQueueChannel;
        recipientList = fetchRecipientList();
    }

    public void addRecipient(MessageChannel channel, MonitorEventSelector selector) {
        LOGGER.debug("Adding new channel for the following events [{}]", selector.getMonitor().requiredEvents());

        if (useLookupMap) {
            Set<Class<? extends Event>> classes = selector.getMonitor().requiredEvents();
            for (Class c : classes) {
                eventToMessageChannelMap.put(c, channel);
            }
        } else {
            this.recipientList.add(new Recipient(channel, selector));
        }
    }

    @Override
    protected Collection<MessageChannel> determineTargetChannels(Message<?> message) {
        Collection ret = null;
        if (useLookupMap) {
            Class<?> payloadClass = message.getPayload().getClass();
            ret = eventToMessageChannelMap.get(payloadClass);
        } else {
            ret = super.determineTargetChannels(message);
        }

        if (ret == null || ret.isEmpty()){
            ret = ImmutableList.of(noInterestedMonitorsQueueChannel);
        }
        return ret;
    }

    private ConcurrentLinkedQueue<Recipient> fetchRecipientList() {
        Field recipientsFields = ReflectionUtils.findField(RecipientListRouter.class, "recipients");
        recipientsFields.setAccessible(true);
        ConcurrentLinkedQueue<Recipient> field = (ConcurrentLinkedQueue<Recipient>) ReflectionUtils.getField(recipientsFields, this);
        return field;
    }


    public boolean isUseLookupMap() {
        return useLookupMap;
    }

    public void setUseLookupMap(boolean useLookupMap) {
        this.useLookupMap = useLookupMap;
    }
}
