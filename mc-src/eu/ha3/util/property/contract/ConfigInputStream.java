package eu.ha3.util.property.contract;

import java.io.InputStream;

/**
 * Config that may be loaded directly from a stream.
 */
public interface ConfigInputStream {
    /**
     * Loads entries from the given stream into this config.
     * 
     * @param stream The stream to load from.
     * @return True if the entries were loaded successfully.
     */
    public boolean loadStream(InputStream stream);
}
