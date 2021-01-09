package eu.ha3.util.property.simple;

public class PropertyTypeException extends PropertyException {
    private static final long serialVersionUID = -294806176942571859L;

    public PropertyTypeException() {
        this(null);
    }

    public PropertyTypeException(Throwable cause) {
        super(cause);
    }
}
