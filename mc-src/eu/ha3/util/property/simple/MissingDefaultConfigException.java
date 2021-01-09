package eu.ha3.util.property.simple;

public class MissingDefaultConfigException extends PropertyException {
    private static final long serialVersionUID = -5912047935272087268L;

    public MissingDefaultConfigException() {
        this(null);
    }

    public MissingDefaultConfigException(Throwable cause) {
        super(cause);
    }
}
