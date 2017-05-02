package mt.edu.um.cs.rv.monitors;

import mt.edu.um.cs.rv.events.CategorisedEvent;
import mt.edu.um.cs.rv.monitors.state.State;

/**
 * Created by dwardu on 29/04/2017.
 */
public abstract class CategorisedStatefulMonitor<S extends State> extends StatefulMonitor<S> {

    protected S loadOrCreateState(Class<? extends mt.edu.um.cs.rv.monitors.Monitor> c, CategorisedEvent e, State parentState){
        State state = this.getMonitorPersistenceProvider().load(c.getTypeName(), e.categoriseEvent());

        if (state == null){
            //create new state
            state = initialiseNewState();
        }

        state.setParentState(parentState);

        return (S) state;
    }

    protected void persistState(Class<? extends mt.edu.um.cs.rv.monitors.Monitor> c, CategorisedEvent e, S s){
        this.getMonitorPersistenceProvider().save(c.getTypeName(), e.categoriseEvent(), s);
    }
}
