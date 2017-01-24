package mt.edu.um.cs.rv.events.builders;

import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.events.triggers.TriggerData;

/**
 * Created by edwardmallia on 19/01/2017.
 */
public interface EventBuilder<T extends TriggerData, E extends Event>
{
    Class<E> forEvent();

    Class<T> forTrigger();

    void setupEventDataFromTrigger(T t);

    E build(Boolean synchronous);

    Boolean shouldFireEvent(E e);
}
