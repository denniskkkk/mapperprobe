package start;

import UIMain.ScanSetMainUI;
import java.io.File;

/**
 *
 * @author xxenn
 */
public class StartUpOn implements InitConstants{

    private static Counter cnt = null;
    private static Counter10 cnt10 = null;

    /**
     * ui start bridge
     */
    public static void StartUpOn() {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                //System.out.println("look: " + info.getName());
                if ("Metal".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ScanSetMainUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ScanSetMainUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ScanSetMainUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ScanSetMainUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        // os detection
        
        String osname = System.getProperty("os.name");
        if (osname.toLowerCase().contains(reqOSname) || osname.toLowerCase().contains(reqOSname2)) {
            System.out.println("osname = " + osname);
            System.out.println("os not support, only linux");
            System.exit(0);
        }
        // searching all neccessary files config
        //String libpath = System.getProperty("java.library.path");
        //String[] libpaths = libpath.split(System.getProperty("path.separator"));
        boolean flag = true; // sample test flag
        //for (String lp : libpaths) {
        //    // System.out.println("libpath = " + lp);
        //}
        File flib = new File(libName);
        if (!flib.isFile()) {
            flag = false;
        }
        if (flag == false) {
            System.err.println("os not support");
            System.exit(0);
        }
        // start 1sec timer counter
        //cnt = new Counter();
        // 10 sec counter
        //cnt10 = new Counter10();
        try {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        new ScanSetMainUI().setVisible(true);
                    } catch (Exception ex) {
                        System.err.println("system start error");
                    }
                }
            });
        } catch (Exception e) {

        }
    }

    /**
     * @return the cnt
     */
    public static Counter getCnt() {
        return cnt;
    }

    /**
     * @return the cnt10
     */
    public static Counter10 getCnt10() {
        return cnt10;
    }

}
