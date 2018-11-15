/*
 * Mylab 
 * All right reserved  * 
 */
package mapper;

/**
 *
 * @author de
 */
public abstract class LoopBackEvent implements LoopBack {

    @Override
    public abstract void eventHandler( int probeN, byte [] tdata, int result);
}
