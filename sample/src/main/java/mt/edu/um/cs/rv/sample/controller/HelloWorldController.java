package mt.edu.um.cs.rv.sample.controller;

import mt.edu.um.cs.rv.eventmanager.observers.DirectInvocationEventObserver;
import mt.edu.um.cs.rv.events.LoginEvent;
import mt.edu.um.cs.rv.events.LogoutEvent;
import mt.edu.um.cs.rv.events.UserCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dwardu on 19/01/2016.
 */
@RestController
public class HelloWorldController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldController.class);
    @Autowired
    DirectInvocationEventObserver directInvocationEventAdaptor;

    private Boolean synchronous = Boolean.TRUE;

    @RequestMapping("/login/{username:.+}")
    public String login(@PathVariable String username) {
        LOGGER.info("================================================================================================");
        LOGGER.info("Handling request for User {} log in", username);
        directInvocationEventAdaptor.observeEvent(new LoginEvent(username, synchronous));
        LOGGER.info("Handled request for User {} log in", username);
        LOGGER.info("================================================================================================");

        return "OK";
    }

    @RequestMapping("/logout/{username:.+}")
    public String logout(@PathVariable String username) {
        LOGGER.info("================================================================================================");
        LOGGER.info("Handling request for User {} log out", username);
        directInvocationEventAdaptor.observeEvent(new LogoutEvent(username, synchronous));
        LOGGER.info("Handled request for User {} log out", username);
        LOGGER.info("================================================================================================");

        return "OK";
    }

    @RequestMapping("/create/{username:.+}")
    public String newUser(@PathVariable String username) {
        LOGGER.info("================================================================================================");
        LOGGER.info("Handling request for User {} creation", username);
        directInvocationEventAdaptor.observeEvent(new UserCreatedEvent(username, synchronous));
        LOGGER.info("Handled request for User {} creation", username);
        LOGGER.info("================================================================================================");

        return "OK";
    }


    @RequestMapping("/system/sync")
    public Boolean synchronous() {
        return synchronous;
    }

    @RequestMapping(value = "/system/sync", method = RequestMethod.POST)
    public Boolean setSystemToSynchronous() {
        this.synchronous = Boolean.TRUE;
        return synchronous;
    }

    @RequestMapping(value = "/system/async", method = RequestMethod.POST)
    public Boolean setSystemToASynchronous() {
        this.synchronous = Boolean.FALSE;
        return synchronous;
    }

}