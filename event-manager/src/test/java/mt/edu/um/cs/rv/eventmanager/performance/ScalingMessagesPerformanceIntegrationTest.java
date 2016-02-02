package mt.edu.um.cs.rv.eventmanager.performance;

import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.annotation.AxisRange;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkHistoryChart;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkMethodChart;
import com.carrotsearch.junitbenchmarks.annotation.LabelType;
import mt.edu.um.cs.rv.eventmanager.engine.config.EventManagerConfigration;
import mt.edu.um.cs.rv.eventmanager.integration.events.EventA;
import mt.edu.um.cs.rv.eventmanager.integration.events.EventB;
import mt.edu.um.cs.rv.eventmanager.integration.events.EventC;
import mt.edu.um.cs.rv.eventmanager.integration.monitors.BlockingAndRememberingMonitor;
import mt.edu.um.cs.rv.eventmanager.integration.monitors.ReleasingAndRememberingMonitor;
import mt.edu.um.cs.rv.eventmanager.integration.monitors.RememberingMonitor;
import mt.edu.um.cs.rv.eventmanager.monitors.registry.MonitorRegistry;
import mt.edu.um.cs.rv.eventmanager.observers.DirectInvocationEventObserver;
import mt.edu.um.cs.rv.events.Event;
import org.junit.*;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dwardu on 26/01/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration({EventManagerConfigration.class})
@AxisRange(min = 0, max = 1)
@BenchmarkMethodChart(filePrefix = "scaling-messages")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ScalingMessagesPerformanceIntegrationTest {

    @Autowired
    MonitorRegistry monitorRegistry;

    @Autowired
    DirectInvocationEventObserver directInvocationEventObserver;

    @Rule
    public TestName name = new TestName();

    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();

    ReleasingAndRememberingMonitor releasingAndRememberingMonitor1;
    ReleasingAndRememberingMonitor releasingAndRememberingMonitor2;
    ReleasingAndRememberingMonitor releasingAndRememberingMonitor3;

    private int numberOfEventsForEachType;

    @Before
    public void setup(){
        //create monitors
        releasingAndRememberingMonitor1 = new ReleasingAndRememberingMonitor("RR1", new Class[]{EventA.class, EventB.class});
        monitorRegistry.registerNewMonitor(releasingAndRememberingMonitor1);

        releasingAndRememberingMonitor2 = new ReleasingAndRememberingMonitor("RR2", new Class[]{EventB.class, EventC.class});
        monitorRegistry.registerNewMonitor(releasingAndRememberingMonitor2);

        releasingAndRememberingMonitor3 = new ReleasingAndRememberingMonitor("RR3", new Class[]{EventA.class, EventC.class});
        monitorRegistry.registerNewMonitor(releasingAndRememberingMonitor3);

        String methodName = name.getMethodName();
        Pattern methodNamePattern = Pattern.compile("(test)(\\d+)(\\D+)");
        Matcher m = methodNamePattern.matcher(methodName);
        if (m.matches()) {
            this.numberOfEventsForEachType = Integer.parseInt(m.group(2));
        }

    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test100Events() throws InterruptedException {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }


    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test200Events() throws InterruptedException {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test300Events() throws InterruptedException {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test400Events() throws InterruptedException {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test500Events() throws InterruptedException {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test999Events() throws InterruptedException {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    private void testEnsureMonitorsReceiveEventsInTheRightOrder(int numberOfEventsForEachType) throws InterruptedException {

        int numberOfTotalEventsToBeProcessed =
                (numberOfEventsForEachType * 2) //monitor RR1 interested in A & B
                + (numberOfEventsForEachType * 2) //monitor RR2 interested in B & C
                + (numberOfEventsForEachType * 2); //monitor RR3 interested in A & C

        //create sempahore
        Semaphore semaphore = new Semaphore(1-numberOfTotalEventsToBeProcessed);
        releasingAndRememberingMonitor1.setSemaphore(semaphore);
        releasingAndRememberingMonitor2.setSemaphore(semaphore);
        releasingAndRememberingMonitor3.setSemaphore(semaphore);


        List<Event> expectedForMonitorRR1 = new ArrayList<>();
        List<Event> expectedForMonitorRR2 = new ArrayList<>();
        List<Event> expectedForMonitorRR3 = new ArrayList<>();

        for (int i = 0; i < numberOfEventsForEachType; i++){
            //send A event
            EventA eventA = new EventA(false, i);
            expectedForMonitorRR1.add(eventA);
            expectedForMonitorRR3.add(eventA);
            directInvocationEventObserver.observeEvent(eventA);

            //send B event
            EventB eventB = new EventB(false, i);
            expectedForMonitorRR1.add(eventB);
            expectedForMonitorRR2.add(eventB);
            directInvocationEventObserver.observeEvent(eventB);

            //send C event
            EventC eventC = new EventC(false, i);
            expectedForMonitorRR2.add(eventC);
            expectedForMonitorRR3.add(eventC);
            directInvocationEventObserver.observeEvent(eventC);
        }

        //wait for all processing to finish
        semaphore.acquire();

        //assert the right order for all monitors
        Assert.assertEquals("Events as observed by monitor RR1 are not the expected events or were not observed in the right order",
                expectedForMonitorRR1, releasingAndRememberingMonitor1.getAllOrderedEvents());
        Assert.assertEquals("Events as observed by monitor RR2 are not the expected events or were not observed in the right order",
                expectedForMonitorRR2, releasingAndRememberingMonitor2.getAllOrderedEvents());
        Assert.assertEquals("Events as observed by monitor RR3 are not the expected events or were not observed in the right order",
                expectedForMonitorRR3, releasingAndRememberingMonitor3.getAllOrderedEvents());

    }

}
