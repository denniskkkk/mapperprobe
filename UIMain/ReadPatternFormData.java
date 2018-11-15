package UIMain;

/**
 *
 * @author xxenn
 */
public class ReadPatternFormData implements java.io.Serializable  {
    private static final long serialVersionUID = -2734267241983681036L;

    private String row = null;
    private String probeNumber = null;
    private String probeCard = null;
    private String probePortNumber = null;
    private String uutCard = null;
    private String uutSignalName = null;

    /**
     * @return the row
     */
    public String getRow() {
        return row;
    }

    /**
     * @param row the row to set
     */
    public void setRow(String row) {
        this.row = row;
    }

    /**
     * @return the probeNumber
     */
    public String getProbeNumber() {
        return probeNumber;
    }

    /**
     * @param probeNumber the probeNumber to set
     */
    public void setProbeNumber(String probeNumber) {
        this.probeNumber = probeNumber;
    }

    /**
     * @return the probeCard
     */
    public String getProbeCard() {
        return probeCard;
    }

    /**
     * @param probeCard the probeCard to set
     */
    public void setProbeCard(String probeCard) {
        this.probeCard = probeCard;
    }

    /**
     * @return the probePortNumber
     */
    public String getProbePortNumber() {
        return probePortNumber;
    }

    /**
     * @param probePortNumber the probePortNumber to set
     */
    public void setProbePortNumber(String probePortNumber) {
        this.probePortNumber = probePortNumber;
    }

    /**
     * @return the uutCard
     */
    public String getUutCard() {
        return uutCard;
    }

    /**
     * @param uutCard the uutCard to set
     */
    public void setUutCard(String uutCard) {
        this.uutCard = uutCard;
    }

    /**
     * @return the uutSignalName
     */
    public String getUutSignalName() {
        return uutSignalName;
    }

    /**
     * @param uutSignalName the uutSignalName to set
     */
    public void setUutSignalName(String uutSignalName) {
        this.uutSignalName = uutSignalName;
    }
 }
