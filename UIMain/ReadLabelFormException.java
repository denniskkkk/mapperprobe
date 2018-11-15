package UIMain;

/**
 *
 * @author de
 */
public class ReadLabelFormException extends Exception {
    private static final long serialVersionUID = 1645419741560239351L;

    /**
     * Creates a new instance of <code>ReadLabelFormException</code> without
     * detail message.
     */
    public ReadLabelFormException() {
        super ("Read label form error:");
    }

    /**
     * Constructs an instance of <code>ReadLabelFormException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ReadLabelFormException(String msg) {
        super("Read label Form Error: " + msg);
    }
}
