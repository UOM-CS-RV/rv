package mt.edu.um.cs.rv.eventmanager.performance;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.annotation.AxisRange;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkMethodChart;
import mt.edu.um.cs.rv.eventmanager.engine.CustomRecipientListRouter;
import mt.edu.um.cs.rv.eventmanager.engine.config.EventManagerConfigration;
import mt.edu.um.cs.rv.eventmanager.integration.events.EventA;
import mt.edu.um.cs.rv.eventmanager.integration.events.EventB;
import mt.edu.um.cs.rv.eventmanager.integration.events.EventC;
import mt.edu.um.cs.rv.eventmanager.integration.monitors.ReleasingMonitor;
import mt.edu.um.cs.rv.eventmanager.monitors.registry.MonitorRegistry;
import mt.edu.um.cs.rv.eventmanager.observers.DirectInvocationEventObserver;
import org.junit.*;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Scanner;
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
public class ScalingMonitorsPerformanceIntegrationTest
{

    private static Logger LOGGER = LoggerFactory.getLogger(ScalingMonitorsPerformanceIntegrationTest.class);

    @Autowired
    MonitorRegistry monitorRegistry;

    @Autowired
    CustomRecipientListRouter customRecipientListRouter;

    @Autowired
    DirectInvocationEventObserver directInvocationEventObserver;

    @Rule
    public TestName name = new TestName();

    @Rule
    public BenchmarkRule benchmarkRun = new BenchmarkRule();

    private Semaphore semaphore;

    private int numberOfMonitors;

    private int numberOfEventsForEachType = 30;


    @BeforeClass
    public static void beforeClass() throws InterruptedException
    {
        LOGGER.info("SLEEPING");
//        Thread.sleep(30000);
        LOGGER.info("GOING");
    }

    @Before
    public void setup() throws Exception
    {

        String methodName = name.getMethodName();
        Pattern methodNamePattern = Pattern.compile("(test)(\\d+)(\\D+)");
        Matcher m = methodNamePattern.matcher(methodName);
        if (m.matches())
        {
            this.numberOfMonitors = Integer.parseInt(m.group(2));
        }

        int numberOfTotalEventsToBeProcessed =
                numberOfEventsForEachType
                        * numberOfMonitors
                        * 1;


        if (methodName.endsWith("WithLookupMap"))
        {
            customRecipientListRouter.setUseLookupMap(true);
        }

        //set not autostartup to false
        monitorRegistry.setAutoStartup(false);

        semaphore = new Semaphore(1 - numberOfTotalEventsToBeProcessed);
        for (int i = 0; i < numberOfMonitors; i++)
        {
            ReleasingMonitor newMonitor = createNewMonitor(i + 1);
            newMonitor.setSemaphore(semaphore);
            LOGGER.info("Registering new monitor {}", i);
            monitorRegistry.registerNewMonitor(newMonitor);
        }

        LOGGER.info("Starting monitor registry");
        //start monitor registry
        monitorRegistry.start();
        LOGGER.info("Started monitor registry");
    }


    private ReleasingMonitor createNewMonitor(int id)
    {
        String monitorName = "RR" + String.format("%05d", id);

        Class[] events = null;
        switch (id % 3)
        {
            case 0:
                events = new Class[]{EventA.class};
                break;
            case 1:
                events = new Class[]{EventB.class};
                break;
            case 2:
                events = new Class[]{EventC.class};
                break;
        }

        ReleasingMonitor releasingMonitor = new ReleasingMonitor(monitorName, events);
        return releasingMonitor;
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test00100Monitors() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test00100MonitorsWithLookupMap() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }


    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test00200Monitors() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test00200MonitorsWithLookupMap() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }


    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test00300Monitors() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test00300MonitorsWithLookupMap() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }


    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test00400Monitors() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test00400MonitorsWithLookupMap() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }


    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test00500Monitors() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test00500MonitorsWithLookupMap() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }


    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test01000Monitors() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test01000MonitorsWithLookupMap() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test05000Monitors() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test05000MonitorsWithLookupMap() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }


    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test09999Monitors() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test09999MonitorsWithLookupMap() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test20000Monitors() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test20000MonitorsWithLookupMap() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test50000Monitors() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }

    @Test
    @DirtiesContext //ensure full context is reloaded
    public void test50000MonitorsWithLookupMap() throws InterruptedException
    {
        testEnsureMonitorsReceiveEventsInTheRightOrder(numberOfEventsForEachType);
    }


    private void testEnsureMonitorsReceiveEventsInTheRightOrder(int numberOfEventsForEachType) throws InterruptedException
    {
        System.out.println();
        System.out.println("=======================================================");
        System.out.println(name.getMethodName());
        long start = System.currentTimeMillis();
        System.out.println("Start: " + start);

        for (int i = 0; i < numberOfEventsForEachType; i++)
        {
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

        long end = System.currentTimeMillis();
        System.out.println("End: " + end);
        System.out.println("Duration: " + (end-start));
        System.out.println("=======================================================");

    }

}
