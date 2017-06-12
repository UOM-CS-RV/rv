package mt.edu.um.cs.rv.monitors.results;

/**
 * Created by dwardu on 07/03/2017.
 */
public enum MonitorResultStatus {
    OK("OK", 0),
    ERROR("The monitor returned with an error while handling an event, indicating that an expected error condition was met.", 999),
    FAILURE("A unexpected failure occurred while handling an event.", Integer.MAX_VALUE),
    ;

    private String description;
    private Integer severity;

    MonitorResultStatus(String description, Integer severity){
        this.description = description;
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public Integer getSeverity() {
        return severity;
    }

    public Boolean isMoreSevereThan(MonitorResultStatus that){
        if (this.severity > that.severity){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
