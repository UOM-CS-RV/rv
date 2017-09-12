package mt.edu.um.cs.rv.monitors;


import mt.edu.um.cs.rv.events.CategorisedEvent;
import mt.edu.um.cs.rv.events.Event;
import mt.edu.um.cs.rv.monitors.results.MonitorResult;
import mt.edu.um.cs.rv.monitors.state.State;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class StatefulMonitorTest {

    public static class State1 extends State {
        private int counter = 0;

        public int getCounter() {
            return counter;
        }

        public void incCounter() {
            counter++;
        }
    }

    public static class State2 extends State {
        private int counter = 0;

        public int getCounter() {
            return counter;
        }

        public void incCounter() {
            counter++;
        }
    }

    public static class Event1 implements Event {
        @Override
        public boolean isSynchronous() {
            return true;
        }
    }

    public static class CategorisedEvent1 implements CategorisedEvent<Long> {
        private Long l;

        public CategorisedEvent1(Long l) {
            this.l = l;
        }

        @Override
        public boolean isSynchronous() {
            return true;
        }

        @Override
        public Long categoriseEvent() {
            return l;
        }
    }

    public static class Monitor1 implements Monitor<State1> {

        @Override
        public String getName() {
            return this.getClass().getTypeName();
        }

        @Override
        public Set<Class<? extends Event>> requiredEvents() {
            Set<Class<? extends Event>> s = new HashSet<>();
            s.add(Event1.class);
            return s;
        }

        @Override
        public MonitorResult handleEvent(Event e, State1 state1) {
            System.out.println(state1.getCounter());
            state1.incCounter();
            return MonitorResult.ok();
        }
    }

    public static class Monitor2 extends mt.edu.um.cs.rv.monitors.StatefulMonitorTest.Monitor1 {

    }

    public static class StatefulMonitor1 extends StatefulMonitor<State1> {

        private final List<State1> stateList;

        public StatefulMonitor1(List<State1> stateList) {
            super();
            this.stateList = stateList;
        }

        @Override
        protected void initialiseRequiredEvents() {
            this.addRequiredEvent(Event1.class);
        }

        @Override
        protected void initialiseInterestedMonitorTypesForEvent() {
            List<Class<? extends Monitor>> l = new ArrayList();
            l.add(Monitor1.class);
            l.add(Monitor2.class);
            this.addInterestedMonitorTypesForEvent(Event1.class, l);
        }

        @Override
        protected State1 initialiseNewState() {
            State1 state1 = new State1();
            stateList.add(state1);
            return state1;
        }

        @Override
        public String getName() {
            return this.getClass().getTypeName();
        }
    }

    public static class Monitor3 implements Monitor<State2> {

        @Override
        public String getName() {
            return this.getClass().getTypeName();
        }

        @Override
        public Set<Class<? extends Event>> requiredEvents() {
            Set<Class<? extends Event>> s = new HashSet<>();
            s.add(CategorisedEvent1.class);
            return s;
        }

        @Override
        public MonitorResult handleEvent(Event e, State2 state2) {
            System.out.println(state2.getCounter());
            state2.incCounter();
            return MonitorResult.ok();
        }
    }

    public static class Monitor4 extends Monitor3 {

    }

    public static class ForEachMonitor1 extends CategorisedStatefulMonitor<State2> {

        private final List<State2> stateList;

        public ForEachMonitor1(List<State2> stateList) {
            super();
            this.stateList = stateList;
        }

        @Override
        protected void initialiseRequiredEvents() {
            this.addRequiredEvent(CategorisedEvent1.class);
        }

        @Override
        protected void initialiseInterestedMonitorTypesForEvent() {
            List<Class<? extends Monitor>> l = new ArrayList();
            l.add(Monitor3.class);
            l.add(Monitor4.class);
            this.addInterestedMonitorTypesForEvent(CategorisedEvent1.class, l);
        }

        @Override
        protected State2 initialiseNewState() {
            State2 state2 = new State2();
            stateList.add(state2);
            return state2;
        }

        @Override
        public String getName() {
            return this.getClass().getTypeName();
        }
    }


    @Test
    public void testStatefulMonitor() {
        List<State1> stateList = new ArrayList<>();
        StatefulMonitor1 statefulMonitor1 = new StatefulMonitor1(stateList);

        Event1 event1 = new Event1();

        statefulMonitor1.handleEvent(event1, null);
        statefulMonitor1.handleEvent(event1, null);
        statefulMonitor1.handleEvent(event1, null);
        statefulMonitor1.handleEvent(event1, null);

        assertEquals(1, stateList.size());

        State1 state1 = stateList.get(0);
        assertEquals(8, state1.getCounter());
    }

    @Test
    public void testCategorisedStatefulMonitor() {
        List<State2> stateList = new ArrayList<>();
        ForEachMonitor1 forEachMonitor1 = new ForEachMonitor1(stateList);

        CategorisedEvent1 categorisedEvent1 = new CategorisedEvent1(10L);
        CategorisedEvent1 categorisedEvent2 = new CategorisedEvent1(20L);

        forEachMonitor1.handleEvent(categorisedEvent1, null);
        forEachMonitor1.handleEvent(categorisedEvent1, null);
        forEachMonitor1.handleEvent(categorisedEvent1, null);
        forEachMonitor1.handleEvent(categorisedEvent1, null);

        assertEquals("Expected 1 state instances - one for every 'categorised' event", 1, stateList.size());
        assertEquals(8, stateList.get(0).getCounter());

        forEachMonitor1.handleEvent(categorisedEvent2, null);
        forEachMonitor1.handleEvent(categorisedEvent2, null);
        forEachMonitor1.handleEvent(categorisedEvent2, null);
        forEachMonitor1.handleEvent(categorisedEvent2, null);

        assertEquals("Expected 2 state instances - one for every 'categorised' event", 2, stateList.size());
        assertEquals(8, stateList.get(0).getCounter());
        assertEquals(8, stateList.get(1).getCounter());


    }
}
