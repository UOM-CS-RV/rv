package mt.edu.um.cs.rv.monitors.persistence;

import mt.edu.um.cs.rv.monitors.state.State;

/**
 * Created by dwardu on 29/04/2017.
 */
public interface MonitorPersistenceProvider {
    State load(Object monitorType);
    State load(Object monitorType, Object eventCategory);

    void save(Object monitorType, State state);
    void save(Object monitorType, Object eventCategory, State state);
}
