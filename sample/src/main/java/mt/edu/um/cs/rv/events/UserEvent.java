package mt.edu.um.cs.rv.events;

import mt.edu.um.cs.rv.events.Event;

/**
 * Created by dwardu on 20/01/2016.
 */
public abstract class UserEvent implements Event {
    private String username;

    private boolean synchronous;

    public UserEvent(String username, boolean synchronous) {
        this.username = username;
        this.synchronous = synchronous;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isSynchronous() {
        return synchronous;
    }
}
