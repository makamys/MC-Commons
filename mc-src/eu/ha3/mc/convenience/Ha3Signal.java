package eu.ha3.mc.convenience;

/**
 * Object that can react to an anonymouse signal.
 */
public interface Ha3Signal {
    /**
     * Called to trigger a state change.
     */
    void signal();
}
