package mt.edu.um.cs.rv.eventmanager.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.aggregator.ReleaseStrategy;
import org.springframework.integration.store.MessageGroup;
import org.springframework.messaging.Message;

import java.util.Collection;

/**
 * Created by dwardu on 19/01/2016.
 */
public class AfterAllMessagesInGroupReleaseStrategy implements ReleaseStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(AfterAllMessagesInGroupReleaseStrategy.class);

    @Override
    public boolean canRelease(MessageGroup group) {
        if (group == null){
            throw new IllegalArgumentException("MessageGroup should not be null");
        }

        Collection<Message<?>> messages = group.getMessages();


        if ((messages == null) || (!messages.iterator().hasNext())) {
            return true;
        } else {
            Integer sequenceSize = group.getSequenceSize();
            Integer messagesSeen = group.getMessages().size();

            if (sequenceSize.equals(messagesSeen)){
                LOGGER.info("Releasing group as number of expected messages = number of seen [{}]", group.getSequenceSize());
                return true;
            } else {
                return false;
            }

        }
    }
}
