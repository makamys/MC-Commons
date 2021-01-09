package eu.ha3.util.property.simple;

import java.util.HashMap;
import java.util.Map;

import eu.ha3.util.property.contract.PropertyHolder;

public class PropertyCell implements PropertyHolder {
    private Map<String, String> properties;

    public PropertyCell() {
        properties = new HashMap<String, String>();
    }

    @Override
    public String getString(String name) {
        if (properties.containsKey(name)) {
            return properties.get(name);
        }
        throw new PropertyMissingException(name);
    }

    @Override
    public boolean getBoolean(String name) {
        try {
            return Boolean.parseBoolean(getString(name));
        } catch (NumberFormatException e) {
            throw new PropertyTypeException(e);
        }
    }

    @Override
    public int getInteger(String name) {
        try {
            return Integer.parseInt(getString(name));
        } catch (NumberFormatException e) {
            throw new PropertyTypeException(e);
        }
    }

    @Override
    public float getFloat(String name) {
        try {
            return Float.parseFloat(getString(name));
        } catch (NumberFormatException e) {
            throw new PropertyTypeException(e);
        }
    }

    @Override
    public long getLong(String name) {
        try {
            return Long.parseLong(getString(name));
        } catch (NumberFormatException e) {
            throw new PropertyTypeException(e);
        }
    }

    @Override
    public double getDouble(String name) {
        try {
            return Double.parseDouble(getString(name));
        } catch (NumberFormatException e) {
            throw new PropertyTypeException(e);
        }
    }

    @Override
    public void setProperty(String name, Object o) {
        properties.put(name, o.toString());
    }

    @Override
    public Map<String, String> getAllProperties() {
        return properties;
    }
}
