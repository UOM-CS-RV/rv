package mt.edu.um.cs.rv.eventmanager.integration;

import mt.edu.um.cs.rv.eventmanager.engine.EventMessageSender;
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
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;

/**
 * Created by dwardu on 26/01/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration({EventManagerConfigration.class})
public class ScatterGatherIntegrationTest {

    @Autowired
    MonitorRegistry monitorRegistry;

    @Autowired
    DirectInvocationEventObserver directInvocationEventObserver;


    @Test
    @DirtiesContext //ensure full context is reloaded
    public void testSetupWithOneMonitorAndOneEvent() {
        //register RememberingMonitor
        RememberingMonitor rememberingMonitor = new RememberingMonitor("R1", new Class[]{EventA.class});

        monitorRegistry.registerNewMonitor(rememberingMonitor);

        int iterations = 10;
        for (int i = 0; i < iterations; i++) {
            directInvocationEventObserver.observeEvent(new EventA(true, i));
        }

        Assert.assertEquals(iterations, rememberingMonitor.getAllEvents().size());
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void testSetupWithOneMonitorAndMultipleEvents() {
        //register RememberingMonitor
        RememberingMonitor rememberingMonitor = new RememberingMonitor("R1", new Class[]{EventA.class, EventB.class});

        monitorRegistry.registerNewMonitor(rememberingMonitor);

        int iterations = 10;
        for (int i = 0; i < iterations; i++) {
            directInvocationEventObserver.observeEvent(new EventA(true, i));
            directInvocationEventObserver.observeEvent(new EventB(true, i));
        }

        Assert.assertEquals(iterations*2, rememberingMonitor.getAllEvents().size());
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void testSetupWithOneMonitorAndMultipleEventsAndUnwantedEvents() {
        //register RememberingMonitor
        RememberingMonitor rememberingMonitor = new RememberingMonitor("R1", new Class[]{EventA.class, EventB.class});

        monitorRegistry.registerNewMonitor(rememberingMonitor);

        int iterations = 10;
        for (int i = 0; i < iterations; i++) {
            directInvocationEventObserver.observeEvent(new EventA(true, i));
            directInvocationEventObserver.observeEvent(new EventB(true, i));
            directInvocationEventObserver.observeEvent(new EventC(true, i));
        }

        Assert.assertEquals(iterations*2, rememberingMonitor.getAllEvents().size());
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void testSetupWithTwoMonitorsAndMultipleEventsAndUnwantedEvents() {
        //register RememberingMonitor
        RememberingMonitor rememberingMonitor1 = new RememberingMonitor("R1", new Class[]{EventA.class, EventB.class});
        RememberingMonitor rememberingMonitor2 = new RememberingMonitor("R2", new Class[]{EventA.class, EventC.class});

        monitorRegistry.registerNewMonitor(rememberingMonitor1);
        monitorRegistry.registerNewMonitor(rememberingMonitor2);

        int iterations = 10;
        for (int i = 0; i < iterations; i++) {
            directInvocationEventObserver.observeEvent(new EventA(true, i));
            directInvocationEventObserver.observeEvent(new EventB(true, i));
            directInvocationEventObserver.observeEvent(new EventC(true, i));
        }

        Assert.assertEquals(iterations*2, rememberingMonitor1.getAllEvents().size());
        Assert.assertEquals(iterations*2, rememberingMonitor2.getAllEvents().size());
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void testEnsureSystemWaitsForAllMonitorsToCompleteBeforeContinuing() {

        int numberOfMonitors = 5;

        Semaphore semaphore = new Semaphore(1-numberOfMonitors);

        for (int i = 0; i < numberOfMonitors; i++){
            ReleasingAndRememberingMonitor releasingAndRememberingMonitor = new ReleasingAndRememberingMonitor("RR"+i, new Class[]{EventA.class}, semaphore);
            monitorRegistry.registerNewMonitor(releasingAndRememberingMonitor);

        }

        //send A events - this should activate all monitors
        EventA eventA = new EventA(true, 1);
        directInvocationEventObserver.observeEvent(eventA);

        Assert.assertTrue("Semaphore acquire should be successful as all monitors should have completed before getting here", semaphore.tryAcquire());
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void testEnsureMultipleMonitorsAreServedConcurrently() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);

        //register RememberingMonitor
        BlockingAndRememberingMonitor blockingAndRememberingMonitor = new BlockingAndRememberingMonitor("BR1", new Class[]{EventA.class}, semaphore);
        RememberingMonitor rememberingMonitor1 = new RememberingMonitor("R1", new Class[]{EventA.class, EventB.class});
        RememberingMonitor rememberingMonitor2 = new RememberingMonitor("R2", new Class[]{EventA.class, EventC.class});

        monitorRegistry.registerNewMonitor(blockingAndRememberingMonitor);
        monitorRegistry.registerNewMonitor(rememberingMonitor1);
        monitorRegistry.registerNewMonitor(rememberingMonitor2);

        //send A events - this should activate all monitors
        EventA eventA = new EventA(false, 1);
        directInvocationEventObserver.observeEvent(eventA);

        //wait a bit to ensure the events have been processed by R1 and R2
        Thread.sleep(100);
        //assert that events have been processed by R1 and R2
        Assert.assertEquals(1, rememberingMonitor1.getAllEvents().size());
        Assert.assertEquals(1, rememberingMonitor2.getAllEvents().size());

        //release semaphore
        semaphore.release();
        //wait a bit for BR1 to process event
        Thread.sleep(100);
        //ensure BR1 has process event
        Assert.assertEquals(1, blockingAndRememberingMonitor.getAllEvents().size());
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void testEnsureMonitorsReceiveEventsInTheRightOrder() throws InterruptedException {
        int numberOfEventsForEachType = 10;

        int numberOfTotalEventsToBeProcessed =
                (numberOfEventsForEachType * 2) //monitor RR1 interested in A & B
                + (numberOfEventsForEachType * 2) //monitor RR2 interested in B & C
                + (numberOfEventsForEachType * 2); //monitor RR3 interested in A & C

        //create sempahore
        Semaphore semaphore = new Semaphore(1-numberOfTotalEventsToBeProcessed);

        //create monitors
        ReleasingAndRememberingMonitor releasingAndRememberingMonitor1 = new ReleasingAndRememberingMonitor("RR1", new Class[]{EventA.class, EventB.class}, semaphore);
        monitorRegistry.registerNewMonitor(releasingAndRememberingMonitor1);

        ReleasingAndRememberingMonitor releasingAndRememberingMonitor2 = new ReleasingAndRememberingMonitor("RR2", new Class[]{EventB.class, EventC.class}, semaphore);
        monitorRegistry.registerNewMonitor(releasingAndRememberingMonitor2);

        ReleasingAndRememberingMonitor releasingAndRememberingMonitor3 = new ReleasingAndRememberingMonitor("RR3", new Class[]{EventA.class, EventC.class}, semaphore);
        monitorRegistry.registerNewMonitor(releasingAndRememberingMonitor3);


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
