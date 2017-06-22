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
import java.util.stream.Collectors;

/**
 * Created by edwardmallia on 19/01/2017.
 */
@Component
public class EventBuilderRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventBuilderRegistry.class);

    @Autowired(required = false)
    private List<EventBuilder> eventBuilders;

    private MultiValueMap<Class<? extends TriggerData>, EventBuilder> buildersMap;

    @PostConstruct
    public void init() {
        //just in case there are no event builders to wire in, create an empty list
        if (eventBuilders == null) {
            eventBuilders = new ArrayList<>();
        }

        buildersMap = new LinkedMultiValueMap<>();

        eventBuilders
                .stream()
                .forEach(b -> buildersMap.add(b.forTriggerData(), b));
    }

    public List<EventBuilder> getBuilders(Class<? extends TriggerData> d) {
        return buildersMap.get(d);
    }

    public List<EventBuilder> getBuilders(Class<? extends TriggerData> d, Object trigger) {
        return buildersMap.get(d)
                .stream()
                .filter(eventBuilder -> {
                            if (eventBuilder.forTrigger() == null) {
                                if (trigger == null) {
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                return d.equals(trigger);
                            }
                        }
                ).collect(Collectors.toList())
                ;
    }
}
