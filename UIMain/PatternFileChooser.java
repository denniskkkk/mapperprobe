package UIMain;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.HeadlessException;
import java.io.File;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

/**
 *
 * @author de
 */
public class PatternFileChooser extends JFileChooser {

    private static final long serialVersionUID = 1L;

    public PatternFileChooser(File file, ScanSetMainUI.FolderRestrictView fview) {
        super(file, fview);

    }

    private static void recursivelySetFonts(Component comp, Font font) {
        comp.setFont(font);
        if (comp instanceof Container) {
            Container cont = (Container) comp;
            for (int j = 0, ub = cont.getComponentCount(); j < ub; ++j) {
                recursivelySetFonts(cont.getComponent(j), font);
            }
        }
    }

    @Override
    protected JDialog createDialog(Component parent) throws HeadlessException {
        JDialog dialog = super.createDialog(parent);
        Font font = new Font("Dialog", Font.PLAIN, 10);
        dialog.setLocation(180, 50);
        dialog.setResizable(false);
        dialog.setSize(800, 500);
        dialog.setModal(true);
        dialog.setBackground(Color.darkGray);
        recursivelySetFonts(dialog, font);
        return dialog;
    }

}
