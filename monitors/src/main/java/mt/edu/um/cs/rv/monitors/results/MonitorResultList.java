package mt.edu.um.cs.rv.monitors.results;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static mt.edu.um.cs.rv.monitors.results.MonitorResultStatus.ERROR;
import static mt.edu.um.cs.rv.monitors.results.MonitorResultStatus.OK;

/**
 * Created by dwardu on 12/03/2017.
 */
public class MonitorResultList extends MonitorResult {

    List<MonitorResult> results;

    private MonitorResultList(MonitorResultStatus status, List<MonitorResult> results) {
        super(status);
        if (results != null) {
            this.results = results;
        } else {
            this.results = new ArrayList<>();
        }
    }


    public static MonitorResultList of(MonitorResult... monitorResults) {
        if (monitorResults == null) {
            monitorResults = new MonitorResult[0];
        }
        
        return MonitorResultList.of(Arrays.asList(monitorResults));
    }

    public static MonitorResultList of(List<MonitorResult> monitorResults) {
        if (monitorResults == null){
            monitorResults = new ArrayList<>();
        }
        
        Optional<MonitorResultStatus> status = monitorResults.stream()
                .map(MonitorResult::getStatus)
                .reduce((status1, status2) -> {
                            if (OK.equals(status1) && OK.equals(status2)) {
                                return OK;
                            } else {
                                return ERROR;
                            }
                        }
                );

        return new MonitorResultList(
                status.orElseGet(() -> OK),
                monitorResults
        );
    }

    @Override
    public String toString() {
        return "MonitorResultList{" +
                "status=" + this.getStatus() +
                ",results=" + results +
                '}';
    }
}
