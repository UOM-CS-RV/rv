package mt.edu.um.cs.rv.eventmanager.engine;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.core.MessageSelector;
import org.springframework.messaging.MessageChannel;

import static org.mockito.Mockito.mock;

/**
 * Created by dwardu on 25/01/2016.
 */
public class CustomRecipientListRouterTest {

    CustomRecipientListRouter router;

    @Before
    public void setup() {
        router = new CustomRecipientListRouter(null);
    }

    @Test
    public void testAddRecipient() {

        MessageChannel messageChannel = mock(MessageChannel.class);
        MonitorEventSelector messageSelector = mock(MonitorEventSelector.class);

        router.addRecipient(messageChannel, messageSelector);

        Assert.assertEquals("Expected router recipient list to contain one element", 1, router.getRecipients().size());
    }

    //note, more test are out of scope, as the rest of the implementation is provided by org.springframework.integration.router.RecipientListRouter
}
