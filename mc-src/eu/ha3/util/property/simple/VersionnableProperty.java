package eu.ha3.util.property.simple;

import java.util.Map;

import eu.ha3.util.property.contract.PropertyHolder;
import eu.ha3.util.property.contract.Versionnable;

/**
 * Versionable properties file.
 */
public class VersionnableProperty implements PropertyHolder, Versionnable {
    private PropertyHolder soft;
    private PropertyHolder hard;

    public VersionnableProperty() {
        soft = new PropertyCell();
        hard = new PropertyCell();
    }

    @Override
    public boolean commit() {
        if (soft.getAllProperties().size() > 0) {
            hard.getAllProperties().putAll(this.soft.getAllProperties());
            soft.getAllProperties().clear();
            return true;
        }
        return false;
    }

    @Override
    public void revert() {
        soft.getAllProperties().clear();
    }

    @Override
    public String getString(String name) {
        try {
            return soft.getString(name);
        } catch (PropertyException e) {
            return hard.getString(name);
        }
    }

    @Override
    public boolean getBoolean(String name) {
        try {
            return soft.getBoolean(name);
        } catch (PropertyException e) {
            return hard.getBoolean(name);
        }
    }

    @Override
    public int getInteger(String name) {
        try {
            return soft.getInteger(name);
        } catch (PropertyException e) {
            return hard.getInteger(name);
        }
    }

    @Override
    public float getFloat(String name) {
        try {
            return soft.getFloat(name);
        } catch (PropertyException e) {
            return hard.getFloat(name);
        }
    }

    @Override
    public long getLong(String name) {
        try {
            return soft.getLong(name);
        } catch (PropertyException e) {
            return hard.getLong(name);
        }
    }

    @Override
    public double getDouble(String name) {
        try {
            return soft.getDouble(name);
        } catch (PropertyException e) {
            return hard.getDouble(name);
        }
    }

    @Override
    public void setProperty(String name, Object o) {
        soft.setProperty(name, o);
    }

    @Override
    public Map<String, String> getAllProperties() {
        return hard.getAllProperties();
    }

}
