package mt.edu.um.cs.rv.eventmanager.si;

import org.springframework.integration.core.MessageSelector;
import org.springframework.integration.router.RecipientListRouter;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by dwardu on 20/01/2016.
 */
public class CustomRecipientListRouter extends RecipientListRouter {

    private ConcurrentLinkedQueue<Recipient> recipientList = null;

    public CustomRecipientListRouter() {
        super();
        recipientList = fetchRecipientList();
    }


    public void addRecipient(MessageChannel channel, MessageSelector messageSelector) {
        this.recipientList.add(new Recipient(channel, messageSelector));
    }


    private ConcurrentLinkedQueue<Recipient> fetchRecipientList() {
        Field recipientsFields = ReflectionUtils.findField(RecipientListRouter.class, "recipients");
        recipientsFields.setAccessible(true);
        ConcurrentLinkedQueue<Recipient> field = (ConcurrentLinkedQueue<Recipient>) ReflectionUtils.getField(recipientsFields, this);
        return field;
    }

}
