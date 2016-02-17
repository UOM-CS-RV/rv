package mt.edu.um.cs.rv.eventmanager.performance;

import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.annotation.AxisRange;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkMethodChart;
import mt.edu.um.cs.rv.eventmanager.engine.CustomRecipientListRouter;
import mt.edu.um.cs.rv.eventmanager.engine.config.EventManagerConfigration;
import mt.edu.um.cs.rv.eventmanager.integration.events.EventA;
import mt.edu.um.cs.rv.eventmanager.integration.events.EventB;
import mt.edu.um.cs.rv.eventmanager.integration.events.EventC;
import mt.edu.um.cs.rv.eventmanager.integration.monitors.ReleasingAndRememberingMonitor;
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
public class ScalingMessagesPerformanceIntegrationTest
{

    @Autowired
    MonitorRegistry monitorRegistry;

    @Autowired
    DirectInvocationEventObserver directInvocationEventObserver;

    @Autowired
    CustomRecipientListRouter customRecipientListRouter;

    @Rule
    public TestName name = new TestName();

    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();

    ReleasingAndRememberingMonitor releasingAndRememberingMonitor1;

    ReleasingAndRememberingMonitor releasingAndRememberingMonitor2;

    ReleasingAndRememberingMonitor releasingAndRememberingMonitor3;

    ReleasingAndRememberingMonitor releasingAndRememberingMonitor4;

    ReleasingAndRememberingMonitor releasingAndRememberingMonitor5;

    ReleasingAndRememberingMonitor releasingAndRememberingMonitor6;

    private int numberOfEventsForEachType;

    @Before
    public void setup()
    {
        String methodName = name.getMethodName();
        Pattern methodNamePattern = Pattern.compile("(test)(\\d+)(\\D+)");
        Matcher m = methodNamePattern.matcher(methodName);
        if (m.matches())
        {
            this.numberOfEventsForEachType = Integer.parseInt(m.group(2));
        }

        if (methodName.endsWith("WithLookupMap")) {
            customRecipientListRouter.setUseLookupMap(true);
        }

        //create monitors
        releasingAndRememberingMonitor1 = new ReleasingAndRememberingMonitor("RR1", new Class[]{EventA.class});
        monitorRegistry.registerNewMonitor(releasingAndRememberingMonitor1);

        releasingAndRememberingMonitor2 = new ReleasingAndRememberingMonitor("RR2", new Class[]{EventB.class});
        monitorRegistry.registerNewMonitor(releasingAndRememberingMonitor2);

        releasingAndRememberingMonitor3 = new ReleasingAndRememberingMonitor("RR3", new Class[]{EventC.class});
        monitorRegistry.registerNewMonitor(releasingAndRememberingMonitor3);

        releasingAndRememberingMonitor4 = new ReleasingAndRememberingMonitor("RR4", new Class[]{EventA.class});
        monitorRegistry.registerNewMonitor(releasingAndRememberingMonitor4);

        releasingAndRememberingMonitor5 = new ReleasingAndRememberingMonitor("RR5", new Class[]{EventB.class});
        monitorRegistry.registerNewMonitor(releasingAndRememberingMonitor5);

        releasingAndRememberingMonitor6 = new ReleasingAndRememberingMonitor("RR6", new Class[]{EventC.class});
        monitorRegistry.registerNewMonitor(releasingAndRememberingMonitor6);

    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test0100Events() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test0100EventsWithLookupMap() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test0200Events() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test0200EventsWithLookupMap() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }


    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test0300Events() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test0300EventsWithLookupMap() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test0400Events() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test0400EventsWithLookupMap() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test0500Events() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test0500EventsWithLookupMap() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test1000Events() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test1000EventsWithLookupMap() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test2000Events() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test2000EventsWithLookupMap() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test3000Events() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test3000EventsWithLookupMap() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test4000Events() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test4000EventsWithLookupMap() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test5000Events() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test5000EventsWithLookupMap() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    private void testEnsureMonitorsReceiveEventsInTheRightOrder(int numberOfEventsForEachType) throws InterruptedException
    {

        int numberOfTotalEventsToBeProcessed =
                (numberOfEventsForEachType) //monitor RR1 interested in A
                        + (numberOfEventsForEachType) //monitor RR2 interested in B
                        + (numberOfEventsForEachType) //monitor RR3 interested in C
                        + (numberOfEventsForEachType) //monitor RR4 interested in A
                        + (numberOfEventsForEachType) //monitor RR5 interested in B
                        + (numberOfEventsForEachType); //monitor RR6 interested in C

        //create sempahore
        Semaphore semaphore = new Semaphore(1 - numberOfTotalEventsToBeProcessed);
        releasingAndRememberingMonitor1.setSemaphore(semaphore);
        releasingAndRememberingMonitor2.setSemaphore(semaphore);
        releasingAndRememberingMonitor3.setSemaphore(semaphore);
        releasingAndRememberingMonitor4.setSemaphore(semaphore);
        releasingAndRememberingMonitor5.setSemaphore(semaphore);
        releasingAndRememberingMonitor6.setSemaphore(semaphore);


        List<Event> expectedForMonitorRR1 = new ArrayList<>();
        List<Event> expectedForMonitorRR2 = new ArrayList<>();
        List<Event> expectedForMonitorRR3 = new ArrayList<>();
        List<Event> expectedForMonitorRR4 = new ArrayList<>();
        List<Event> expectedForMonitorRR5 = new ArrayList<>();
        List<Event> expectedForMonitorRR6 = new ArrayList<>();

        for (int i = 0; i < numberOfEventsForEachType; i++)
        {
            //send A event
            EventA eventA = new EventA(false, i);
            expectedForMonitorRR1.add(eventA);
            expectedForMonitorRR4.add(eventA);
            directInvocationEventObserver.observeEvent(eventA);

            //send B event
            EventB eventB = new EventB(false, i);
            expectedForMonitorRR2.add(eventB);
            expectedForMonitorRR5.add(eventB);
            directInvocationEventObserver.observeEvent(eventB);

            //send C event
            EventC eventC = new EventC(false, i);
            expectedForMonitorRR3.add(eventC);
            expectedForMonitorRR6.add(eventC);
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
        Assert.assertEquals("Events as observed by monitor RR4 are not the expected events or were not observed in the right order",
                            expectedForMonitorRR4, releasingAndRememberingMonitor4.getAllOrderedEvents());
        Assert.assertEquals("Events as observed by monitor RR5 are not the expected events or were not observed in the right order",
                            expectedForMonitorRR5, releasingAndRememberingMonitor5.getAllOrderedEvents());
        Assert.assertEquals("Events as observed by monitor RR6 are not the expected events or were not observed in the right order",
                            expectedForMonitorRR6, releasingAndRememberingMonitor6.getAllOrderedEvents());

    }

}
