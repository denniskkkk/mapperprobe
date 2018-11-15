package UIMain;

/**
 *
 * @author de
 */
public class MapPanelException extends Exception {

    /**
     * Creates a new instance of <code>MapPanelException</code> without detail
     * message.
     */
    public MapPanelException() {
        super ("quick view exception");
    }

    /**
     * Constructs an instance of <code>MapPanelException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public MapPanelException(String msg) {
        super("quick view exception, " + msg);
    }
}
