package mt.edu.um.cs.rv.eventmanager.performance;

import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.annotation.AxisRange;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkMethodChart;
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
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
@BenchmarkMethodChart(filePrefix = "scaling-monitors")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ScalingMonitorsPerformanceIntegrationTest {

    @Autowired
    MonitorRegistry monitorRegistry;

    @Autowired
    DirectInvocationEventObserver directInvocationEventObserver;

    @Rule
    public TestName name = new TestName();

    @Rule
    public BenchmarkRule benchmarkRun = new BenchmarkRule();

    private Semaphore semaphore;
    private int numberOfMonitors;
    private int numberOfEventsForEachType = 100;

    @Before
    public void setup() throws Exception {

        String methodName = name.getMethodName();
        Pattern methodNamePattern = Pattern.compile("(test)(\\d+)(\\D+)");
        Matcher m = methodNamePattern.matcher(methodName);
        if (m.matches()) {
            this.numberOfMonitors = Integer.parseInt(m.group(2));
        }

        int numberOfTotalEventsToBeProcessed =
                numberOfEventsForEachType
                        * numberOfMonitors
                        * 3;


        semaphore = new Semaphore(1-numberOfTotalEventsToBeProcessed);
        for (int i = 0; i < numberOfMonitors; i++){
            ReleasingAndRememberingMonitor newMonitor = createNewMonitor(i + 1);
            newMonitor.setSemaphore(semaphore);
            monitorRegistry.registerNewMonitor(newMonitor);
        }
    }


    private ReleasingAndRememberingMonitor createNewMonitor(int id){
        String monitorName = "RR" + String.format("%05d",id);
        Class[] events = {EventA.class, EventB.class, EventC.class};
        ReleasingAndRememberingMonitor releasingAndRememberingMonitor = new ReleasingAndRememberingMonitor(monitorName, events);
        return releasingAndRememberingMonitor;
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test10Monitors() throws InterruptedException {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test20Monitors() throws InterruptedException {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test30Monitors() throws InterruptedException {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test40Monitors() throws InterruptedException {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    private void testEnsureMonitorsReceiveEventsInTheRightOrder(int numberOfEventsForEachType) throws InterruptedException {

        for (int i = 0; i < numberOfEventsForEachType; i++){
            //send A event
            EventA eventA = new EventA(false, i);
            directInvocationEventObserver.observeEvent(eventA);

            //send B event
            EventB eventB = new EventB(false, i);
            directInvocationEventObserver.observeEvent(eventB);

            //send C event
            EventC eventC = new EventC(false, i);
            directInvocationEventObserver.observeEvent(eventC);
        }

        //wait for all processing to finish
        semaphore.acquire();

    }

}
