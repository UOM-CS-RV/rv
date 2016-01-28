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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
    EventMessageSender eventMessageSender;


    @Test
    @DirtiesContext //ensure full context is reloaded
    public void testSetupWithOneMonitorAndOneEvent() {
        //register RememberingMonitor
        RememberingMonitor rememberingMonitor = new RememberingMonitor("R1", new Class[]{EventA.class});

        monitorRegistry.registerNewMonitor(rememberingMonitor);

        int iterations = 10;
        for (int i = 0; i < iterations; i++) {
            eventMessageSender.send(new EventA(true, i));
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
            eventMessageSender.send(new EventA(true, i));
            eventMessageSender.send(new EventB(true, i));
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
            eventMessageSender.send(new EventA(true, i));
            eventMessageSender.send(new EventB(true, i));
            eventMessageSender.send(new EventC(true, i));
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
            eventMessageSender.send(new EventA(true, i));
            eventMessageSender.send(new EventB(true, i));
            eventMessageSender.send(new EventC(true, i));
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
            ReleasingAndRememberingMonitor releasingAndRememberingMonitor = new ReleasingAndRememberingMonitor("RR1", new Class[]{EventA.class}, semaphore);
            monitorRegistry.registerNewMonitor(releasingAndRememberingMonitor);

        }

        //send A events - this should activate all monitors
        EventA eventA = new EventA(true, 1);
        eventMessageSender.send(eventA);

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
        eventMessageSender.send(eventA);

        //wait a bit to ensure the events have been processed by R1 and R2
        Thread.sleep(10);
        //assert that events have been processed by R1 and R2
        Assert.assertEquals(1, rememberingMonitor1.getAllEvents().size());
        Assert.assertEquals(1, rememberingMonitor2.getAllEvents().size());

        //release semaphore
        semaphore.release();
        //wait a bit for BR1 to process event
        Thread.sleep(10);
        //ensure BR1 has process event
        Assert.assertEquals(1, blockingAndRememberingMonitor.getAllEvents().size());

    }

}
