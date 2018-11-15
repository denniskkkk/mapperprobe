/*
 * Mylab 
 * All right reserved  * 
 */
package mapper;

/**
 *
 * @author de
 */
public class MapperException extends Exception {

    public MapperException() {
        super("mapper exception ");
    }
    
    public MapperException (String msg) {
        super ("mapper exception : " + msg);
    }
}
