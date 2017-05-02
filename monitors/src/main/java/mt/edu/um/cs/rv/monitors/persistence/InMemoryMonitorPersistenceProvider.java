package mt.edu.um.cs.rv.monitors.persistence;

import mt.edu.um.cs.rv.monitors.state.State;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dwardu on 29/04/2017.
 */
public class InMemoryMonitorPersistenceProvider implements MonitorPersistenceProvider{

    private Map<Key, State> stateMap = new HashMap<>();

    private static InMemoryMonitorPersistenceProvider singletonInstance = null;

    private InMemoryMonitorPersistenceProvider(){

    }

    public static synchronized InMemoryMonitorPersistenceProvider getInstance(){
        if (singletonInstance == null) {
            singletonInstance = new InMemoryMonitorPersistenceProvider();
        }
        return singletonInstance;
    }

    private static final class Key {
        private Object monitorType;
        private Object eventCategory;

        public Key(Object monitorType, Object eventCategory) {
            this.monitorType = monitorType;
            this.eventCategory = eventCategory;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (!monitorType.equals(key.monitorType)) return false;
            return eventCategory != null ? eventCategory.equals(key.eventCategory) : key.eventCategory == null;
        }

        @Override
        public int hashCode() {
            int result = monitorType.hashCode();
            result = 31 * result + (eventCategory != null ? eventCategory.hashCode() : 0);
            return result;
        }
    }

    @Override
    public State load(Object monitorType) {
        return load(monitorType, null);
    }

    @Override
    public State load(Object monitorType, Object eventCategory) {
        Key key = new Key(monitorType, eventCategory);
        return stateMap.get(key);
    }

    @Override
    public void save(Object monitorType, State state) {
        save(monitorType, null, state);
    }

    @Override
    public void save(Object monitorType, Object eventCategory, State state) {
        Key key = new Key(monitorType, eventCategory);
        stateMap.put(key, state);
    }
}
