package mt.edu.um.cs.rv.eventmanager.observers;

import mt.edu.um.cs.rv.events.triggers.TriggerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by edwardmallia on 19/01/2017.
 */
public class IxarisSPIExternalEventObserver extends ExternalEventObserver<Object, IxarisSPIExternalEventObserver.Tr, Boolean>
{

    public class Tr implements TriggerData
    {

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(IxarisSPIExternalEventObserver.class);

    public boolean foo(Object o) {
        return true;
    }


    @Override
    public Tr generateTrigger(Object o)
    {
        return null;
    }

    @Override
    public Boolean generateResponse(Object o, Tr tr)
    {
        return null;
    }

    @Override
    public void sendResponse(Boolean aBoolean)
    {

    }

}
