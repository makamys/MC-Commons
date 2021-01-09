package eu.ha3.util.property.simple;

public class PropertyMissingException extends PropertyException {
    private static final long serialVersionUID = -5281216564682832813L;

    public PropertyMissingException() {
        super();
    }

    public PropertyMissingException(String name) {
        super("Property \"" + name + "\" was not found.");
    }

    public PropertyMissingException(Throwable cause) {
        super(cause);
    }
}
