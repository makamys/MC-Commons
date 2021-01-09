package eu.ha3.util.property.contract;

/**
 * Configuration file that may be loaded from and save to a file.
 */
public interface ConfigSource {
    /**
     * Sets the source path.
     * 
     * @param path Path to file that will be used to persist this config's entries.
     */
    public void setSource(String path);

    /**
     * Loads entries from this config's source file.
     * 
     * @return True if load succeeded.
     */
    public boolean load();

    /**
     * Saves entries from from this config to the source file.
     * 
     * @return True if the save was successful.
     */
    public boolean save();
}