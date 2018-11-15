package UIMain;

/**
 *
 * @author xxenn
 */
public class ReadPatternFormException extends Exception {
    private static final long serialVersionUID = 130722154332955139L;

    /**
     * Creates a new instance of <code>ReadPatternFormException</code> without
     * detail message.
     */
    public ReadPatternFormException() {
       super ("Read Pattern Form Error");
    }

    /**
     * Constructs an instance of <code>ReadPatternFormException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ReadPatternFormException(String msg) {
        super("Read Pattern Form Error :" + msg);
    }
}
