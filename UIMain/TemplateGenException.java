package UIMain;

/**
 *
 * @author de
 */
public class TemplateGenException extends Exception {
    private static final long serialVersionUID = -3161002601878116056L;

    /**
     * Creates a new instance of <code>TemplateGenException</code> without
     * detail message.
     */
    public TemplateGenException() {
        super ("Create Pattern File Error!!!");
    }

    /**
     * Constructs an instance of <code>TemplateGenException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public TemplateGenException(String msg) {
        super("Create Pattern File Error!!! " + msg);
    }
}
