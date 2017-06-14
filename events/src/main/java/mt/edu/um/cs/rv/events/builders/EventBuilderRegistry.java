package mt.edu.um.cs.rv.events.builders;

import mt.edu.um.cs.rv.events.triggers.TriggerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by edwardmallia on 19/01/2017.
 */
@Component
public class EventBuilderRegistry
{

    private static final Logger LOGGER = LoggerFactory.getLogger(EventBuilderRegistry.class);

    @Autowired(required = false)
    private List<EventBuilder> eventBuilders;

    private MultiValueMap<Class<? extends TriggerData>, EventBuilder> buildersMap;

    @PostConstruct
    public void init(){
        //just in case there are no event builders to wire in, create an empty list
        if (eventBuilders == null){
            eventBuilders = new ArrayList<>();
        }

        buildersMap = new LinkedMultiValueMap<>();

        eventBuilders
                .stream()
                .forEach(b -> buildersMap.add(b.forTrigger(), b));
    }

    public List<EventBuilder> getBuilders(Class<? extends TriggerData> t){
        return buildersMap.get(t);
    }
}
