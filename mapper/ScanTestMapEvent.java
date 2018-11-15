/*
 * Mylab 
 * All right reserved  * 
 */
package mapper;

/**
 *
 * @author de
 */
public abstract class ScanTestMapEvent implements ScanTestMap{

    @Override
    public abstract void eventHandler(int probeN, byte[] rowdata, int result);
    
}
