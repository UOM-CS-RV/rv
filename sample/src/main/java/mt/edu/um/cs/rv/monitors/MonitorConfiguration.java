package mt.edu.um.cs.rv.monitors;

import mt.edu.um.cs.rv.eventmanager.monitors.registry.MonitorRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by dwardu on 18/01/2016.
 */
@Configuration
public class MonitorConfiguration {

    @Autowired
    Executor executor;

    @Autowired
    MonitorRegistry monitorRegistry;


    @Bean
    public Monitor globalLoginMonitor() {
        Monitor monitor = new GlobalUserLoginCounterMonitor();

        monitorRegistry.registerNewMonitor(monitor);

        return monitor;
    }


    @Bean
    public DelegatingUserLoginLogoutMonitor forEachUserMonitor() {

        List userMonitors = Arrays.asList(new UserLoginLogoutMonitor("a@example.com"), new UserLoginLogoutMonitor("b@example.com"));

        DelegatingUserLoginLogoutMonitor monitor = new DelegatingUserLoginLogoutMonitor(userMonitors);

        monitorRegistry.registerNewMonitor(monitor);

        return monitor;
    }


    @Bean
    public Monitor userCreationMonitor() {
        Monitor monitor = new UserCreationMonitor(forEachUserMonitor());
        monitorRegistry.registerNewMonitor(monitor);
        return monitor;
    }


}
