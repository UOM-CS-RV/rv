package mt.edu.um.cs.rv.eventmanager.observers;

import mt.edu.um.cs.rv.events.Event;

/**
 * Created by dwardu on 25/01/2016.
 */
public class TestEvent implements Event{

    private final String message = "Hello, world";
    @Override
    public boolean isSynchronous() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestEvent userEvent = (TestEvent) o;

        if (isSynchronous() != userEvent.isSynchronous()) return false;
        return message != null ? message.equals(userEvent.message) : userEvent.message == null;

    }

    @Override
    public int hashCode() {
        int result = message != null ? message.hashCode() : 0;
        result = 31 * result + (isSynchronous() ? 1 : 0);
        return result;
    }
}
