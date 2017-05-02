package mt.edu.um.cs.rv.monitors.state;

/**
 * Created by dwardu on 29/04/2017.
 */
public abstract class State<P extends State> {
    private P parentState;

    public final P getParentState() {
        return parentState;
    }

    public final void setParentState(P parentState) {
        this.parentState = parentState;
    }
}
