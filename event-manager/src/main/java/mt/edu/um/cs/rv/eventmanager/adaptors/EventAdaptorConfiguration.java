package mt.edu.um.cs.rv.eventmanager.adaptors;

import mt.edu.um.cs.rv.eventmanager.engine.EventMessageSender;
import mt.edu.um.cs.rv.eventmanager.observers.DirectInvocationEventObserver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by dwardu on 19/01/2016.
 */
@Configuration
public class EventAdaptorConfiguration {

    @Bean
    public DirectInvocationEventObserver directInvocationEventAdaptor(EventMessageSender eventMessageSender) {
        return new DirectInvocationEventObserver(eventMessageSender);
    }

}
