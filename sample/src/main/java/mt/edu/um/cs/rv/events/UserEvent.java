package mt.edu.um.cs.rv.events;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEvent userEvent = (UserEvent) o;

        if (synchronous != userEvent.synchronous) return false;
        return username != null ? username.equals(userEvent.username) : userEvent.username == null;

    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (synchronous ? 1 : 0);
        return result;
    }
}
