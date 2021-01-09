package eu.ha3.easy;

/**
 * Simplistic edge triggered device.
 * 
 * @author Hurry
 */
public class EdgeTrigger {
    private boolean currentState;
    private EdgeModel triggerModel;
    private long time;

    /**
     * Creates an edge trigger with an initial state of false.
     * 
     * @param model
     */
    public EdgeTrigger(EdgeModel model) {
        this(model, false);
    }

    /**
     * Create an edge trigger.
     * 
     * @param model
     * @param initialState
     */
    public EdgeTrigger(EdgeModel model, boolean initialState) {
        triggerModel = model;
        currentState = initialState;
    }

    public boolean getCurrentState() {
        return currentState;
    }

    /**
     * Tell the edge trigger to evaluate with a new current state.
     * 
     * @param state The new state
     * @return True if this trigger's state changed.
     */
    public boolean signalState(boolean state) {
        if (state != currentState) {
            currentState = state;
            if (state) {
                triggerModel.onTrueEdge();
            } else {
                triggerModel.onFalseEdge();
            }
            return true;
        }
        return false;
    }

    /**
     * Attempts to toggle the state of this EdgeTrigger.
     * 
     * @return True if the state changed.
     */
    public boolean toggleState() {
        long newtime = System.currentTimeMillis();
        if (newtime - time < 1000) {
            time = newtime;
            return false;
        }
        time = newtime;
        return signalState(!currentState);
    }
}
