package UIMain;

import java.awt.Color;

/**
 *
 * @author de
 */
public class MessageListData {
    private String msg = null;
    private Color color = null;
    private int probeNumber = 0;

    /**
     * @return the probeNumber
     */
    public int getProbeNumber() {
        return probeNumber;
    }

    /**
     * @param probeNumber the probeNumber to set
     */
    public void setProbeNumber(int probeNumber) {
        this.probeNumber = probeNumber;
    }

    public enum MESSAGE_TYPE {
        NORMAL, INFO, ERROR
    } 
    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(Color color) {
        this.color = color;
    }
            
}
