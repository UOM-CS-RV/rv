package mt.edu.um.cs.rv.sample;

import mt.edu.um.cs.rv.eventmanager.si.EventManagerConfigration;
import mt.edu.um.cs.rv.monitors.MonitorConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * Created by dwardu on 19/01/2016.
 */
@SpringBootApplication
@Import({EventManagerConfigration.class, MonitorConfiguration.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
