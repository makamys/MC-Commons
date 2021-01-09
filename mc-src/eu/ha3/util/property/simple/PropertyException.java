package eu.ha3.util.property.simple;

public abstract class PropertyException extends RuntimeException {
    public PropertyException() {
        super();
    }

    public PropertyException(String message) {
        super(message);
    }

    public PropertyException(Throwable cause) {
        super(cause);
    }
}
