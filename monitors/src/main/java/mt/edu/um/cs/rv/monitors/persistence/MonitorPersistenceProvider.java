package mt.edu.um.cs.rv.monitors.persistence;

import mt.edu.um.cs.rv.monitors.Monitor;
import mt.edu.um.cs.rv.monitors.state.State;

import java.util.Optional;

/**
 * Created by dwardu on 29/04/2017.
 */
public interface MonitorPersistenceProvider {
    Optional<State> load(Class<? extends Monitor> monitorClass);
    Optional<State> load(Class<? extends Monitor> monitorClass, Object eventCategory);

    void save(Class<? extends Monitor> monitorClass, State state);
    void save(Class<? extends Monitor> monitorClass, Object eventCategory, State state);
}
