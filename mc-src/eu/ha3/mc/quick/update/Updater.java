package eu.ha3.mc.quick.update;

import eu.ha3.util.property.simple.ConfigProperty;

public interface Updater {

    /**
     * Starts an update check
     */
    public void attempt();

    /**
     * Saves the default update stats and configurations
     * 
     * @param configuration Config property to save into
     */
    public void fillDefaults(ConfigProperty configuration);

    /**
     * Loads update stats and configurations
     * 
     * @param configuration Config to load from
     */
    public void loadConfig(ConfigProperty configuration);
}
