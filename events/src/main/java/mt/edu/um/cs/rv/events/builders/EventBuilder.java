package mt.edu.um.cs.rv.events.builders;

import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.events.triggers.TriggerData;

/**
 * Created by edwardmallia on 19/01/2017.
 */
public interface EventBuilder<D extends TriggerData, E extends Event, T>
{
    Class<E> forEvent();

    Class<D> forTriggerData();

    T forTrigger();

    E build(D d, Boolean synchronous);

    Boolean shouldFireEvent(E e);
}
