package mt.edu.um.cs.rv.events;

/**
 * Created by dwardu on 20/01/2016.
 */
public class UserCreatedEvent extends UserEvent {

    public UserCreatedEvent(String username, boolean synchronous) {
        super(username, synchronous);
    }
}
