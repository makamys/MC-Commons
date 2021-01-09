package eu.ha3.mc.convenience;

public class Ha3StaticUtilities {
    /**
     * Checks if a certain class name exists in a certain object context's class
     * loader.
     */
    public static boolean classExists(String className, Object context) {
        try {
            return Class.forName(className, false, context.getClass().getClassLoader()) != null;
        } catch (Throwable e) {
            // Normally throws checked ClassNotFoundException
            // This also throws unchecked security exceptions
        }
        return false;
    }
}
