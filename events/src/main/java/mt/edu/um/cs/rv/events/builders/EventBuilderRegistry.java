package mt.edu.um.cs.rv.events.builders;

import mt.edu.um.cs.rv.events.triggers.TriggerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by edwardmallia on 19/01/2017.
 */
@Component
public class EventBuilderRegistry
{

    private static final Logger LOGGER = LoggerFactory.getLogger(EventBuilderRegistry.class);

    @Autowired(required = false)
    private List<EventBuilder> eventBuilders;

    private Map<Class<? extends TriggerData>, EventBuilder> buildersMap;

    @PostConstruct
    public void init(){
        //just in case there are no event builders to wire in, create an empty list
        if (eventBuilders == null){
            eventBuilders = new ArrayList<>();
        }

        buildersMap = eventBuilders
                .stream()
                .collect(
                        Collectors.toMap(b -> b.forTrigger(), b -> b)
                        );
    }

    public EventBuilder getBuilder(Class<? extends TriggerData> t){
        return buildersMap.get(t);
    }
}
