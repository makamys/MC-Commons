package eu.ha3.util.property.contract;

import java.util.Map;

/**
 * Provider for property entries.
 */
public interface PropertyHolder {
    /**
     * Gets a property value as a raw string.
     * 
     * @param name Name of property to retrieve
     */
    public String getString(String name);

    /**
     * Gets a property value as a boolean
     * 
     * @param name Name of property to retrieve
     */
    public boolean getBoolean(String name);

    /**
     * Gets a property value as an integer.
     * 
     * @param name Name of property to retrieve
     */
    public int getInteger(String name);

    /**
     * Gets a property value as a float.
     * 
     * @param name Name of property to retrieve
     */
    public float getFloat(String name);

    /**
     * Gets a property value as a long.
     * 
     * @param name Name of property to retrieve
     */
    public long getLong(String name);

    /**
     * Gets a property value as a double.
     * 
     * @param name Name of property to retrieve
     */
    public double getDouble(String name);

    /**
     * Updates a property value.
     * 
     * @param name  Name of property to set.
     * @param value The value to assign.
     */
    public <T> void setProperty(String name, T value);

    /**
     * Gets a map of all properties this holder currently contains.
     */
    public Map<String, String> getAllProperties();
}
