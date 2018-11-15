package mapper;

/**
 *
 * @author de
 */
public abstract class MapTestProbesEvent implements MapTestProbes{

    public abstract void eventHandler(int probeN, byte[] rowdata, int result); 
    
}
