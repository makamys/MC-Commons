package eu.ha3.util.property.contract;

/**
 * Supports multiple versions of itself.
 */
public interface Versionnable {
    /**
     * Saves all current changes and makes them permanent.
     * 
     * @return True if there are changes and that they were saved successfully,
     *         false otherwise.
     */
    public boolean commit();

    /**
     * Clears changes and loads the commited state.
     */
    public void revert();
}