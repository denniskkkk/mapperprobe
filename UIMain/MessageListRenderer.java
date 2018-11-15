package UIMain;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.text.Style;

/**
 *
 * @author de
 */
public class MessageListRenderer extends JLabel implements ListCellRenderer {

    private Color backgroundColor = null;
    private Style style, fontSize;  

    public MessageListRenderer() {
       // setOpaque(true);
        setFont (new Font("Arial",Font.PLAIN, 10));
        backgroundColor = getBackground();
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        MessageListData msgdata = (MessageListData ) value;
        setText(msgdata.getMsg());
        setForeground(msgdata.getColor());
        setBackground(Color.BLACK);
        return this;
    }

}
