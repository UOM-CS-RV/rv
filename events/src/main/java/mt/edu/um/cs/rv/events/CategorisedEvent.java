package mt.edu.um.cs.rv.events;

/**
 * Created by dwardu on 18/01/2016.
 */
public interface CategorisedEvent<C> extends Event
{
    C categoriseEvent();
}
