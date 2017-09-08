package mt.edu.um.cs.rv.monitors.state;

import java.io.Serializable;

/**
 * Created by dwardu on 29/04/2017.
 */
public abstract class State<P extends State> implements Serializable{
    private P parentState;

    public final P getParentState() {
        return parentState;
    }

    public final void setParentState(P parentState) {
        this.parentState = parentState;
    }
}
