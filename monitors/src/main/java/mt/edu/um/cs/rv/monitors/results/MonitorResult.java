package mt.edu.um.cs.rv.monitors.results;

import java.io.Serializable;

/**
 * Created by dwardu on 07/03/2017.
 */
public class MonitorResult implements Serializable {
    private MonitorResultStatus status;

    protected MonitorResult(MonitorResultStatus status) {
        this.status = status;
    }

    public static MonitorResult ok() {
        return new MonitorResult(MonitorResultStatus.OK);
    }

    public static MonitorResult error() {
        return new MonitorResult(MonitorResultStatus.ERROR);
    }
    
    public MonitorResultStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "MonitorResult{" +
                "status=" + status +
                '}';
    }
}
