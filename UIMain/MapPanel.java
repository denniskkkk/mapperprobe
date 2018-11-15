package UIMain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author xxenn
 */
public class MapPanel extends JPanel implements ConstantsInterface {

    private static final long serialVersionUID = -1817887223464663239L;
    private Graphics2D g2d = null;
    private ScanAndTestDataProcessor data = null;
    private boolean mode = false;   // scan mode = false, test mode = true;

    public MapPanel() {
        setBackground(Color.BLACK);
        //setPreferredSize(new java.awt.Dimension(400, 400));
        setBounds(mpanelXPos, mpanelYPos, mpanelSize + 8, mpanelSize + 8);
    }

    public void drawMap() {
        int size = data.getPatternSize();  // get number of probes in dots
        if (g2d != null && data != null && size > 0) {
            for (int i = 0; i < size && i < mpanelSize; i++) {
                for (int j = 0; j < size && j < mpanelSize; j++) {
                    try {
                        if (mode == false) {   // scan mode display colour 
                            g2d.setColor(data.getScanResultViewColor(i, j));
                        } else {    // test mode display colour
                            g2d.setColor(data.getTestResultViewColor(i, j));
                        }
                    } catch (ScanAndTestDataException e) {
                        g2d.setColor(Color.darkGray);
                    }
                    g2d.drawLine(i + 4, j + 4, i + 4, j + 4);
                }
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paintComponent(g);
        if (g != null) {
            g2d = (Graphics2D) g;
            drawMap();
        }
    }

    public void setData(ScanAndTestDataProcessor d, boolean m) {
        data = d;
        mode = m;
    }
}
