package mt.edu.um.cs.rv.monitors.results;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static mt.edu.um.cs.rv.monitors.results.MonitorResultStatus.*;

/**
 * Created by dwardu on 07/03/2017.
 */
public class MonitorResult<P extends Serializable> implements Serializable {
    private MonitorResultStatus status;
    private Optional<P> payload;
    private Optional<String> throwableClassName;
    private Optional<String> throwableMessage;
    private Optional<StackTraceElement[]> throwableStackTrace;

    protected MonitorResult(MonitorResultStatus status, P payload) {
        this(status, payload, null, null, null);
    }

    protected MonitorResult(MonitorResultStatus status,
                            P payload,
                            String throwableClassName,
                            String throwableMessage,
                            StackTraceElement[] throwableStackTrace) {
        this.status = status;
        this.payload = Optional.ofNullable(payload);
        this.throwableClassName = Optional.ofNullable(throwableClassName);
        this.throwableMessage = Optional.ofNullable(throwableMessage);
        this.throwableStackTrace = Optional.ofNullable(throwableStackTrace);
    }

    public static MonitorResult<?> ok() {
        return new MonitorResult<>(OK, null);
    }

    public static <P extends Serializable> MonitorResult<P> ok(P payload) {
        return new MonitorResult<>(OK, payload);
    }

    public static MonitorResult<?> error() {
        return error(null);
    }

    public static <P extends Serializable> MonitorResult<P> error(P payload) {
        return error(payload, null);
    }

    public static <P extends Serializable> MonitorResult<P> error(P payload, Throwable throwable) {
        if (throwable == null) {
            throwable = new Throwable();
        }

        return new MonitorResult<>(ERROR, payload, throwable.getClass().getName(), throwable.getMessage(), throwable.getStackTrace());
    }

    public static MonitorResult<?> failure() {
        return failure(null);
    }

    public static <P extends Serializable> MonitorResult<P> failure(P payload) {
        return failure(payload, null);
    }

    public static <P extends Serializable> MonitorResult<P> failure(P payload, Throwable throwable) {
        if (throwable == null) {
            throwable = new Throwable();
        }

        return new MonitorResult<>(FAILURE, payload, throwable.getClass().getName(), throwable.getMessage(), throwable.getStackTrace());
    }


   
    public MonitorResultStatus getStatus() {
        return status;
    }

    protected void setStatus(MonitorResultStatus status) {
        this.status = status;
    }

    public Optional<P> getPayload() {
        return payload;
    }

    public Optional<String> getThrowableClassName() {
        return throwableClassName;
    }

    public Optional<String> getThrowableMessage() {
        return throwableMessage;
    }

    public Optional<StackTraceElement[]> getThrowableStackTrace() {
        return throwableStackTrace;
    }

    private String getThrowableStackTraceAsString() {
        if (!throwableStackTrace.isPresent()){
            return "";
        }
        else {
            StackTraceElement[] stes = this.throwableStackTrace.orElse(new StackTraceElement[0]);
            return Arrays.stream(stes)
                    .skip(0)
                    .map(StackTraceElement::toString)
                    .collect(Collectors.joining("\",\"", "\"", "\""))
                    ;
        }
    }

    protected String getPayloadAsString(){
        if (payload.isPresent()){
            return "\"" + payload.get().toString() + "\"";
        }
        return null;
    }

    @Override
    public String toString() {
        return "{" +
                "\"status\": \"" + status + "\"," +
                "\"statusDescription\": \"" + status.getDescription() + "\"," +
                "\"payload\": " + getPayloadAsString() + "," +
                "\"throwableClassName\": \"" + throwableClassName.orElse(null) + "\"," +
                "\"throwableMessage\": \"" + throwableMessage.orElse(null) + "\"," +
                "\"throwableStackTrace\": [" + getThrowableStackTraceAsString() + "]" +
                '}';
    }
}
