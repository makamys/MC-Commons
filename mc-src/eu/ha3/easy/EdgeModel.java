package eu.ha3.easy;

/**
 * Edge trigger listener.
 */
public interface EdgeModel {
    /**
     * Triggered when the key goes down.
     */
    public void onTrueEdge();

    /**
     * Triggered when the key goes up.
     */
    public void onFalseEdge();

}
