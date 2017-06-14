package mt.edu.um.cs.rv.monitors.results;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static mt.edu.um.cs.rv.monitors.results.MonitorResultStatus.OK;

/**
 * Created by dwardu on 12/03/2017.
 */
public class MonitorResultList extends MonitorResult<ArrayList<MonitorResult<?>>> {

    private ArrayList<MonitorResult<?>> results;

    public MonitorResultList() {
        super(OK, new ArrayList<>());
        this.results = this.getPayload().get();
    }

    public void addMonitorResult(MonitorResult<?> monitorResult) {
        if (monitorResult != null) {
            MonitorResultStatus reduced = reduceStatus(monitorResult.getStatus(), this.getStatus());
            this.setStatus(reduced);
            this.results.add(monitorResult);
        }
    }

    protected MonitorResultStatus reduceStatus(MonitorResultStatus s1, MonitorResultStatus s2) {

        if ((s1 == null) && (s2 == null)) {
            return OK;
        } else if (s1 == null) {
            return s2;
        } else if (s2 == null) {
            return s1;
        } else if (s1.isMoreSevereThan(s2)) {
            return s1;
        } else {
            return s2;
        }

    }

    @Override
    protected String getPayloadAsString() {
        if (this.getPayload().isPresent()) {
            return this.getPayload().get()
                    .stream()
                    .map(MonitorResult::toString)
                    .collect(Collectors.joining(",", "[", "]"));
        }
        return "null";
    }
}
