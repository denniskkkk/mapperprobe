package UIMain;

/**
 *
 * @author de
 */
public class ScanAndTestDataException extends Exception {
    private static final long serialVersionUID = 6940670603294921653L;

    public ScanAndTestDataException() {
        super("pattern data exception");
    }

    public ScanAndTestDataException(String msg) {
        super("pattern data exception, " + msg);
    }
}
