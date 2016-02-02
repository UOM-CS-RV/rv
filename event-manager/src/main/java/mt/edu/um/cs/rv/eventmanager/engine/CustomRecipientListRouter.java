package mt.edu.um.cs.rv.eventmanager.engine;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import mt.edu.um.cs.rv.events.Event;
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

    private boolean useLookupMap = false;

    private ConcurrentLinkedQueue<Recipient> recipientList = null;

    private Multimap eventToMessageChannelMap = ArrayListMultimap.create();

    public CustomRecipientListRouter() {
        super();
        recipientList = fetchRecipientList();
    }

    public void addRecipient(MessageChannel channel, MonitorEventSelector selector) {

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
        if (useLookupMap) {
            Class<?> payloadClass = message.getPayload().getClass();
            return eventToMessageChannelMap.get(payloadClass);
        } else {
            return super.determineTargetChannels(message);
        }
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
