package mt.edu.um.cs.rv.eventmanager.engine;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.integration.aggregator.ReleaseStrategy;
import org.springframework.integration.store.MessageGroup;
import org.springframework.integration.store.SimpleMessageGroup;
import org.springframework.messaging.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.when;

/**
 * Created by dwardu on 19/01/2016.
 */
public class AfterAllMessagesInGroupReleaseStrategyTest {

    AfterAllMessagesInGroupReleaseStrategy afterAllMessagesInGroupReleaseStrategy;

    @Before
    public void setup() {
        afterAllMessagesInGroupReleaseStrategy = new AfterAllMessagesInGroupReleaseStrategy();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testCanReleaseWithNullMessageGroup(){
        MessageGroup messageGroup = null;

        //should throw IllegalArgumentException
        afterAllMessagesInGroupReleaseStrategy.canRelease(messageGroup);
    }

    @Test
    public void testCanReleaseWithNullMessageGroupMessages(){
        MessageGroup messageGroup = mock(MessageGroup.class);
        Collection c = null;
        when(messageGroup.getMessages()).thenReturn(c);

        //should always release if message group is empty
        boolean result = afterAllMessagesInGroupReleaseStrategy.canRelease(messageGroup);
        Assert.assertTrue("Group should be released if the message collection is null", result);
    }

    @Test
    public void testCanReleaseWithEmptyMessageGroupMessages(){
        MessageGroup messageGroup = mock(MessageGroup.class);
        when(messageGroup.getMessages()).thenReturn(new ArrayList<Message<?>>());

        //should always release if message group is empty
        boolean result = afterAllMessagesInGroupReleaseStrategy.canRelease(messageGroup);
        Assert.assertTrue("Group should be released if the number of messages is zero", result);
    }

    @Test
    public void testCanReleaseWithMatchingNumberOfMessagesAndMessageGroupSize(){
        int groupSize = 10;

        Iterator iterator = mock(Iterator.class);
        when(iterator.hasNext()).thenReturn(true);

        Collection messages = mock(Collection.class);
        when(messages.size()).thenReturn(groupSize);
        when(messages.iterator()).thenReturn(iterator);

        MessageGroup messageGroup = mock(MessageGroup.class);
        when(messageGroup.getMessages()).thenReturn(messages);
        when(messageGroup.getSequenceSize()).thenReturn(groupSize);

        //should be released as the numbers match
        boolean result = afterAllMessagesInGroupReleaseStrategy.canRelease(messageGroup);
        Assert.assertTrue("Group should be released if the number of messages is equal to the message group sizes", result);
    }

    @Test
    public void testCanReleaseWithNonMatchingNumberOfMessagesAndMessageGroupSize(){
        int groupSize = 10;

        Iterator iterator = mock(Iterator.class);
        when(iterator.hasNext()).thenReturn(true);

        Collection messages = mock(Collection.class);
        when(messages.size()).thenReturn(groupSize);
        when(messages.iterator()).thenReturn(iterator);

        MessageGroup messageGroup = mock(MessageGroup.class);
        when(messageGroup.getMessages()).thenReturn(messages);
        when(messageGroup.getSequenceSize()).thenReturn(groupSize + 1);

        //should be released as the numbers match
        boolean result = afterAllMessagesInGroupReleaseStrategy.canRelease(messageGroup);
        Assert.assertFalse("Group should not be released if the number of messages is not equal to the message group sizes", result);
    }


//    @Override
//    public boolean canRelease(MessageGroup group) {
//        Collection<Message<?>> messages = group.getMessages();
//
//        if ((messages == null) || (!messages.iterator().hasNext())) {
//            return false;
//        } else {
//            Integer sequenceSize = group.getSequenceSize();
//            Integer messagesSeen = group.getMessages().size();
//
//            if (sequenceSize.equals(messagesSeen)) {
//                LOGGER.info("Releasing group as number of expected messages = number of seen [{}]", group.getSequenceSize());
//                return true;
//            } else {
//                return false;
//            }
//
//        }
//    }
}
