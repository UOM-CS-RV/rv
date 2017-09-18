package mt.edu.um.cs.rv.monitors;

import mt.edu.um.cs.rv.events.CategorisedEvent;
import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.monitors.state.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Created by dwardu on 29/04/2017.
 */
public abstract class CategorisedStatefulMonitor<S extends State> extends StatefulMonitor<S> {

    private static Logger LOGGER = LoggerFactory.getLogger(CategorisedStatefulMonitor.class);

    @Override
    protected S loadOrCreateState(Class<? extends mt.edu.um.cs.rv.monitors.Monitor> c, Event e, State parentState){
        CategorisedEvent ce = null;
        if (e instanceof CategorisedEvent){
            ce = (CategorisedEvent) e;
        }
        else{
            LOGGER.error("Unable to load state for monitor {}, as supplied event [{}] is not an instance of CategorisedEvent", c.getName(), e);
        }

        Optional<State> optionalState = this.getMonitorPersistenceProvider().load(c, ce.categoriseEvent());

        State state;
        if (optionalState.isPresent()) {
            state = optionalState.get();
        } else {
            //create new state
            state = initialiseNewState();
        }

        state.setParentState(parentState);

        return (S) state;
    }

    @Override
    protected void persistState(Class<? extends mt.edu.um.cs.rv.monitors.Monitor> c, Event e, S s){

        CategorisedEvent ce = null;
        if (e instanceof CategorisedEvent){
            ce = (CategorisedEvent) e;
        }
        else{
            LOGGER.error("Unable to save state for monitor {}, as supplied event [{}] is not an instance of CategorisedEvent", c.getName(), e);
        }

        this.getMonitorPersistenceProvider().save(c, ce.categoriseEvent(), s);
    }
}
