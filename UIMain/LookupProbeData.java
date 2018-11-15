package UIMain;

/**
 *
 * @author de
 */
public class LookupProbeData  implements java.io.Serializable {
    private static final long serialVersionUID = -4650760290706019970L;
    
    private LookupProbeData() {
    }
    
    public String dataLookup () {
        return null;
    }
    
    public static LookupProbeData getInstance() {
        return LookupProbeDataHolder.INSTANCE;
    }
    
    private static class LookupProbeDataHolder {

        private static final LookupProbeData INSTANCE = new LookupProbeData();
    }
}
