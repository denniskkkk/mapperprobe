/*
 * Mylab 
 * All right reserved  * 
 */
package UIMain;

/**
 *
 * @author de
 */
public class ReportGenException extends Exception {

    /**
     * Creates a new instance of <code>ReportGenException</code> without detail
     * message.
     */
    public ReportGenException() {
        super ("error create report file!!!");
    }

    /**
     * Constructs an instance of <code>ReportGenException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ReportGenException(String msg) {
        super ("error create report file!!! " + msg);
    }
}
