package eu.ha3.mc.quick.configurable;

import eu.ha3.util.property.simple.ConfigProperty;

/**
 * Config file provider.
 */
public interface HasConfiguration {
    /**
     * Gets the associated config.
     */
    public ConfigProperty getConfig();

    /**
     * Saves any config changes.
     */
    public void saveConfig();
}
