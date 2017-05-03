package mt.edu.um.cs.rv.monitors;

import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.monitors.persistence.InMemoryMonitorPersistenceProvider;
import mt.edu.um.cs.rv.monitors.persistence.MonitorPersistenceProvider;
import mt.edu.um.cs.rv.monitors.results.MonitorResult;
import mt.edu.um.cs.rv.monitors.state.State;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by dwardu on 29/04/2017.
 */
public abstract class StatefulMonitor<S extends State> implements Monitor{
    private Set<Class<? extends Event>> requiredEvents = new HashSet<>();

    private Map<Class<? extends Event>, List<Class<? extends Monitor>>> interestedMonitorTypesForEvent = new HashMap<>();

    private MonitorPersistenceProvider monitorPersistenceProvider = null;

    public StatefulMonitor(){
        this.initialiseRequiredEvents();
        this.initialiseInterestedMonitorTypesForEvent();
    }

    protected abstract void initialiseRequiredEvents();

    protected void addRequiredEvent(Class<? extends Event> eventClass){
        this.requiredEvents.add(eventClass);
    }

    protected void addInterestedMonitorTypesForEvent(Class<? extends Event> eventClass, List<Class<? extends Monitor>> monitorClasses){
        this.interestedMonitorTypesForEvent.put(eventClass, monitorClasses);
    }

    protected abstract void initialiseInterestedMonitorTypesForEvent();

    public void setMonitorPersistenceProvider(MonitorPersistenceProvider monitorPersistenceProvider){
        this.monitorPersistenceProvider = monitorPersistenceProvider;
    }

    protected MonitorPersistenceProvider getMonitorPersistenceProvider(){
        if (this.monitorPersistenceProvider == null) {
             monitorPersistenceProvider = InMemoryMonitorPersistenceProvider.getInstance();
        }
        return monitorPersistenceProvider;
    }

    protected abstract S initialiseNewState();

    protected S loadOrCreateState(Class<? extends mt.edu.um.cs.rv.monitors.Monitor> c, Event e, State parentState){
        State state = getMonitorPersistenceProvider().load(c.getTypeName());

        if (state == null){
            //create new state
            state = initialiseNewState();
        }

        state.setParentState(parentState);

        return (S) state;
    }

    protected void persistState(Class<? extends mt.edu.um.cs.rv.monitors.Monitor> c, Event e, S s){
        this.getMonitorPersistenceProvider().save(c.getTypeName(), s);
    }

    private List<Class<? extends Monitor>> getInterestedMonitorTypes(final Event e) {
        return interestedMonitorTypesForEvent.get(e.getClass());
    }

    @Override
    public final MonitorResult handleEvent(final Event e, final State parentState) {

        if (e == null){
            //TODO this should never happen
            //TODO handle this cleanly ??
            throw new RuntimeException("Unable to handle null event");
        }

        List<Class<? extends mt.edu.um.cs.rv.monitors.Monitor>> interestedMonitorTypes = getInterestedMonitorTypes(e);

        List<mt.edu.um.cs.rv.monitors.results.MonitorResult> results = new ArrayList<>();

        for (Class<? extends mt.edu.um.cs.rv.monitors.Monitor> c : interestedMonitorTypes){

            //TODO this needs to be improved
            S state = this.loadOrCreateState(c, e, parentState);

            //create new monitor with the given state object
            Monitor monitor = null;
            try {
                java.lang.reflect.Constructor<? extends Monitor> cons = c.getConstructor();
                monitor = cons.newInstance();
            } catch (InstantiationException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (InvocationTargetException e1) {
                e1.printStackTrace();
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            }

            //ensure using the same persistence provider
            if (monitor instanceof StatefulMonitor) {
                ((StatefulMonitor) monitor).setMonitorPersistenceProvider(this.getMonitorPersistenceProvider());
            }

            MonitorResult monitorResult = monitor.handleEvent(e, state);
            results.add(monitorResult);

            this.persistState(c, e, state);
        }

        return mt.edu.um.cs.rv.monitors.results.MonitorResultList.of(results);
    }

    @Override
    public Set<Class<? extends Event>> requiredEvents() {
        return requiredEvents;
    }
}
