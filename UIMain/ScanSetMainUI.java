package UIMain;

import static UIMain.ConstantsInterface.fileExtension;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import static java.util.Arrays.sort;
import java.util.Calendar;
import java.util.Date;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import mapper.MapTestProbesEvent;
import mapper.MapperException;
import mapper.ProbeMapper;
import mapper.ScanTestMapEvent;

/**
 *
 * @author den
 */
public class ScanSetMainUI extends javax.swing.JFrame implements ConstantsInterface {

    private static final long serialVersionUID = -7874959830679823315L;

    private static MapPanel mpanel = null;
    private static ImageIcon iconMain = null;
    private DefaultListModel msgModel = null;
    private static ProbeMapper pm = null;
    private static int nprobe;
    private static int currentprobe = 0;
    private static ScanAndTestDataProcessor dataMap = null;
    private static ScanPatternGen tg = null;
    private static ReportGen rgen = null;
    private static String userhome = null;
    private static String userreport = null;
    private static String usertemplate = null;
    private static String usersetup = null;
    private static boolean debug = false;  // system debug flag
    private static boolean saveOnlyFailflag = true; // test save report on fail test only
    private static HelpDialog dialog = null;
    private static ReadLabelFormData[] probeLabelData = null;
    private static ReadPatternForm readPatternForm = null;
    private static boolean loopStartStop = false;
    private static int numberOfLoop = 1;
    private MessageListRenderer msgRender = null;

    /**
     * Creates new form ScanSetMainUI
     */
    public ScanSetMainUI() {
        //ProbeMapper pm;
        // ClassLoader cl = this.getClass().getClassLoader();
        // iconMain = new ImageIcon(cl.getResource("UIMain/res/icon.png"));
        setUndecorated(true);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        initComponents();
        dialog = new HelpDialog(new javax.swing.JFrame(), true);
        msgRender = new MessageListRenderer();
        messageList.setCellRenderer(msgRender);
        if (dialog == null) {
            addMessage("UI init error!!!", MessageListEnum.ERROR);
            exitOnError();
        }

        msgModel = new DefaultListModel();
        if (msgModel == null) {
            addMessage("Document init error!!!", MessageListEnum.ERROR);
            exitOnError();
        }
        //msgModel = (DefaultListModel) messageList.getModel();
        messageList.setModel(msgModel);
        java.net.URL url = ClassLoader.getSystemResource("UIMain/res/icon.png");
        if (url == null) {
            addMessage("System init error 2!!!", MessageListEnum.ERROR);
            exitOnError();
        }
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image img = kit.createImage(url);
        setIconImage(img);
        setLocation(screenX, screenY);
        setSize(screenWidth, screenHeight);

        messageList.ensureIndexIsVisible(msgModel.getSize() - 1);
        //
        try {
            pm = ProbeMapper.getInstance();
        } catch (Exception ex) {
            addMessage("System init error!!!", MessageListEnum.ERROR);
            exitOnError();
        }
        try {
            int pn = 0;
            pm.initProbes();
            String[] listdev = pm.getDeviceList();
            sort(listdev);
            for (String desc : listdev) {
                pn++;
                addMessage("probe adaptor number " + pn + " found and connected", MessageListEnum.INFO);
            }
        } catch (Exception ex) {
            addMessage("Probe adaptor error!!!", MessageListEnum.ERROR);
            exitOnError();
        }
        // handler for scan pattern
        try {
            nprobe = pm.getTotalProbes();
            try {
                dataMap = new ScanAndTestDataProcessor(nprobe);
            } catch (ScanAndTestDataException ex) {
                addMessage("Probe data init error!!!", MessageListEnum.ERROR);
                exitOnError();
            }
            readPatternForm = ReadPatternForm.getInstance(nprobe);
            if (readPatternForm == null) {
                addMessage("Probe data init error!!!", MessageListEnum.ERROR);
                exitOnError();
            }
            // add quick view panel 
            mpanel = new MapPanel();
            if (mpanel == null) {
                addMessage("UI init error!!!", MessageListEnum.ERROR);
                exitOnError();
            }
            mpanel.setData(dataMap, false);
            mpanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
            desktopPane.add(mpanel);
            // ---excel file generator
            tg = new ScanPatternGen();   //  pattern excel spreadsheet file generator
            if (tg == null) {
                addMessage("Document init error!!!", MessageListEnum.ERROR);
                exitOnError();
            }
            rgen = new ReportGen();  // result report excel spreadsheet generator
            if (rgen == null) {
                addMessage("Document init error!!!", MessageListEnum.ERROR);
                exitOnError();
            }
            // -- open usb probe adaptor here
            pm.openProbes();
            if (pm == null) {
                addMessage("Open Probe adaptor error!!!", MessageListEnum.ERROR);
                exitOnError();
            }
            pm.disableAllProbes();
            if (pm == null) {
                addMessage("Probe IO error!!!", MessageListEnum.ERROR);
                exitOnError();
            }
            pm.addScanTestMapEventHandler(new ScanTestMapEvent() {
                @Override
                public void eventHandler(int probeN, byte[] rowdata, int result) {
                    if (rowdata != null) {
                        currentprobe = probeN;
                        addScanMessage(probeN, rowdata);
                        mpanel.setData(dataMap, false);
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                mpanel.repaint();
                            }
                        });
                    }
                }
            });
        } catch (IOException ex) {
            addMessage("System io init error!!!", MessageListEnum.ERROR);
            exitOnError();
        } catch (MapperException ex) {
            addMessage("Probe system init error!!!", MessageListEnum.ERROR);
            exitOnError();
        }
        // event handler for map test
        try {
            pm.addDoTestMapWithAllProbesEventHandler(new MapTestProbesEvent() {

                @Override
                public void eventHandler(int probeN, byte[] redata, int result) {
                    if (redata != null) {
                        currentprobe = probeN;
                        addTestResultMessage(probeN, redata);
                        mpanel.setData(dataMap, true);
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                mpanel.repaint();
                            }
                        });
                    }
                }
            });
        } catch (MapperException ex) {
            addMessage("Probe init error!!!", MessageListEnum.ERROR);
            exitOnError();
        }

        // file folder setup
        userhome = System.getProperty("user.home");
        if (userhome == null) {
            addMessage("user folder not found!!!", MessageListEnum.ERROR);
            exitOnError();
        }
        //addMessage("user dir = " + userhome);
        File freport = new File(userhome + "/" + reportFolder);
        if (freport == null) {
            addMessage("Report folder error!!!", MessageListEnum.ERROR);
        }
        if (!freport.isDirectory()) {
            freport.delete();
            freport.mkdir();   // create a folder
            addMessage("report folder not found, create it!", MessageListEnum.ERROR);
        }
        File ftemplate = new File(userhome + "/" + templateFolder);
        if (ftemplate == null) {
            addMessage("Template folder error!!!", MessageListEnum.ERROR);
        }
        if (!ftemplate.isDirectory()) {
            ftemplate.delete();
            ftemplate.mkdir();   // create a folder
            addMessage("template folder not found, create it!", MessageListEnum.ERROR);
        }
        File fsetup = new File(userhome + "/" + setupFolder);
        if (fsetup == null) {
            addMessage("Setup folder error!!!", MessageListEnum.ERROR);
        }
        if (!fsetup.isDirectory()) {
            fsetup.delete();
            fsetup.mkdir();   // create a folder
            addMessage("setup folder not found, create it!", MessageListEnum.ERROR);
        }
        userreport = userhome + "/" + reportFolder;
        usertemplate = userhome + "/" + templateFolder;
        usersetup = userhome + "/" + setupFolder;
        //
        // read form label setup
        try {
            ReadLabelForm lform = ReadLabelForm.getInstance(nprobe);   // read form label from label file
            lform.readForm(usersetup);  // read file
            //addMessage("config file last row = " + lform.getLastRow());
            if (lform != null && lform.getLastRow() >= nprobe) {

                probeLabelData = lform.getLabelData();    // get result data
                for (ReadLabelFormData fm : probeLabelData) {
                    addMessage("Reading Config file : " + fm.getProbeNumber() + ", SignalName = " + fm.getUutSignalName(), MessageListEnum.INFO);
                }
                tg.setPatternData(probeLabelData);
                rgen.setPatternData(probeLabelData);
            } else {
                addMessage("Configuration File Error!!!", MessageListEnum.ERROR);
            }
        } catch (ReadLabelFormException ex) {
          //  ex.printStackTrace();
            addMessage("Label file in setup folder 'probenametable.xls' missing or error!!!", MessageListEnum.WARNING);
        } catch (FileNotFoundException ex) {
            addMessage("Label file in setup folder 'probenametable.xls' missing or error!!!", MessageListEnum.INFO);
        } catch (IOException ex) {
            addMessage("Read Label File Error!!!", MessageListEnum.ERROR);
        }
        addMessage("complete checking!", MessageListEnum.INFO);

    }

    /**
     * get scan pattern probe display messages with colour code in RGB
     *
     * @param driver
     **/
     
    public void addScanMessage(int driver, byte[] data) {
        int scan_grounded = 0;
        int scan_powered = 0;
        int scan_loopback = 0;
        int scan_loopbackerror = 0;
        int scan_error = 0;
        int scan_unknown = 0;
        for (int probe = 0; probe < data.length; probe++) {
            byte dt = data[probe];
            if ((dt & 0xff) == 0xff) {
                scan_unknown++;   // untested
            } else if ((dt & 0x09) == 0x09 || (dt & 0x11) == 0x11) {                   // check error state, if pwd + connected, gnd + connected
                scan_error ++;    // error in scan
            } else if ((dt & 0x10) > 0) {                   // powered
                scan_powered++;
            } else if ((dt & 0x08) > 0) {                   // grounded
                scan_grounded++;
            } else if (driver == probe) {
                if ((dt & 0xff) == 0x40 || (dt & 0x80) > 0 || (dt & 0x04) > 0) {
                    scan_loopbackerror++;                   // 0x40 probe loopback test error 
                } else if ((dt & 0xff) == 0x41) {
                    scan_loopback++;                  // 0x41 probe lookback test ok
                }
            } else if ((dt & 0x40) == 0x40) {                   // leak detect
                scan_error++;        // 
            } else if ((dt & 0x80) == 0x80 || (dt & 0x04) == 0x04) { // probe error
                scan_error++;                          // red
            } else if ((dt & 0x04) == 0x04) {                   // leaked
                scan_error++;        // 
            } else {
                scan_unknown++;   // not any, black
            }
        }
        if (scan_error > 0) {
            addMessage("Scan, driver number = " + (driver + 1) + ", Got Error!!", MessageListEnum.ERROR);
            return;
        }
        if (scan_loopbackerror > 0) {
            addMessage("Scan, driver number = " + (driver + 1) + ", Loopback Error!!!", MessageListEnum.ERROR);
            return;
        }
        if ((scan_error + scan_loopbackerror) == 0) {
            addMessage("Scan, driver number = " + (driver + 1), MessageListEnum.INFO);
        }
    } 

    /**
     * get scan pattern probe display colour code in RGB
     *
     * @param driver
     * @param data[]
     *
     */
    public void addTestResultMessage(int driver, byte[] sdata) {
        int test_loopbackerror = 0;
        int test_passed = 0;
        int test_fail = 0;
        int test_powered = 0;
        int test_grounded = 0;
        int test_unknown = 0;
        int test_error = 0;

        for (int probe = 0; probe < sdata.length; probe++) {
            int ds = sdata[probe] & 0xff;
            if (ds == 0xff) {
                test_unknown++;
            } else if ((ds & 0x40) == 0) {             //not tested
                test_error++;
            } else if ((ds & 0x90) == 0x90) {           // loopback error
                test_loopbackerror++;
            } else if ((ds & 0x80) == 0x80) {              // any error
                test_error++;
            }
        }
        if (test_error > 0) {
            addProbeMessage( driver + 1, "Test, driver number = " + (driver + 1) + ", Got Error or Fail!!!", MessageListEnum.ERROR);
        }
        if (test_loopbackerror > 0) {
            addProbeMessage( driver + 1, "Test, driver number = " + (driver + 1) + ", Loopback Error!!!", MessageListEnum.ERROR);
        }
        if ((test_error + test_loopbackerror) == 0) {
            addProbeMessage( driver + 1, "Test, driver number = " + (driver + 1), MessageListEnum.INFO);
        }
        //addMessage ("driver  = " + driver + ", test_error = " + error, MessageListEnum.INFO);
    }

    /**
     * reset scan data
     */
    private void clearMapBuffer() {
        try {
            dataMap.resetPatternData();
        } catch (ScanAndTestDataException e) {
            addMessage("System internal Error !!!", MessageListEnum.ERROR);
        }
    }

    /**
     * reset test data
     */
    private void clearPMapBuffer() {
        try {
            dataMap.resetTestData();
        } catch (ScanAndTestDataException e) {
            addMessage("System internal Error !!!", MessageListEnum.ERROR);
        }
    }

    private void delay(int msec) {
        try {
            Thread.sleep(msec);
        } catch (InterruptedException ex) {
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        saveOutputTypeGroup = new javax.swing.ButtonGroup();
        desktopPane = new javax.swing.JDesktopPane();
        msgPanel = new javax.swing.JScrollPane();
        messageList = new javax.swing.JList();
        canvas1 = new java.awt.Canvas();
        testStatusLabel = new javax.swing.JLabel();
        btmClear = new javax.swing.JButton();
        btmSave = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        keyLabelText = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btmScan = new javax.swing.JButton();
        saveFilenameLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btmLoadMap = new javax.swing.JButton();
        loadFileLabel = new javax.swing.JLabel();
        btmVerifty = new javax.swing.JButton();
        spinRunLap = new javax.swing.JSpinner();
        saveOnlyFail = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        btmStop = new javax.swing.JButton();
        outputNormal = new javax.swing.JRadioButton();
        outputDebug = new javax.swing.JRadioButton();
        menuBar = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        fileMenu = new javax.swing.JMenu();
        exitMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ScanSetTools");
        setBounds(new java.awt.Rectangle(0, 0, 0, 0));
        setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        setIconImage(getIconImage());
        setIconImages(null);
        setName("MainFrame"); // NOI18N
        setUndecorated(true);
        setResizable(false);

        desktopPane.setBackground(new java.awt.Color(0, 102, 102));

        msgPanel.setBackground(new java.awt.Color(0, 102, 102));
        msgPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Log", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10), new java.awt.Color(255, 255, 255))); // NOI18N
        msgPanel.setForeground(new java.awt.Color(255, 255, 255));
        msgPanel.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        msgPanel.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        msgPanel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N

        messageList.setBackground(new java.awt.Color(0, 0, 0));
        messageList.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        messageList.setForeground(new java.awt.Color(0, 0, 0));
        messageList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        messageList.setDoubleBuffered(true);
        msgPanel.setViewportView(messageList);

        desktopPane.add(msgPanel);
        msgPanel.setBounds(10, 280, 450, 370);
        desktopPane.add(canvas1);
        canvas1.setBounds(450, 160, 0, 0);

        testStatusLabel.setBackground(new java.awt.Color(0, 0, 0));
        testStatusLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        testStatusLabel.setForeground(java.awt.Color.yellow);
        testStatusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        testStatusLabel.setText("***");
        testStatusLabel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
        testStatusLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        desktopPane.add(testStatusLabel);
        testStatusLabel.setBounds(620, 450, 360, 50);
        testStatusLabel.getAccessibleContext().setAccessibleName("resultMsg");

        btmClear.setBackground(new java.awt.Color(0, 51, 51));
        btmClear.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btmClear.setForeground(new java.awt.Color(255, 255, 255));
        btmClear.setText("<html>Clear<br/>Log</html>");
        btmClear.setFocusPainted(false);
        btmClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btmClearActionPerformed(evt);
            }
        });
        desktopPane.add(btmClear);
        btmClear.setBounds(470, 450, 100, 50);

        btmSave.setBackground(new java.awt.Color(0, 51, 51));
        btmSave.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btmSave.setForeground(new java.awt.Color(255, 255, 255));
        btmSave.setText("-");
        btmSave.setFocusPainted(false);
        btmSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btmSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btmSaveActionPerformed(evt);
            }
        });
        desktopPane.add(btmSave);
        btmSave.setBounds(470, 510, 100, 50);

        jButton3.setBackground(new java.awt.Color(153, 51, 0));
        jButton3.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Restart");
        jButton3.setFocusPainted(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        desktopPane.add(jButton3);
        jButton3.setBounds(880, 510, 100, 50);

        jPanel1.setBackground(new java.awt.Color(102, 102, 102));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Quick View", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Dialog", 0, 11), new java.awt.Color(255, 255, 255))); // NOI18N
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N

        keyLabelText.setBackground(new java.awt.Color(102, 102, 102));
        keyLabelText.setFont(new java.awt.Font("Dialog", 1, 9)); // NOI18N
        keyLabelText.setForeground(new java.awt.Color(255, 255, 255));
        keyLabelText.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        keyLabelText.setToolTipText("");
        keyLabelText.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        keyLabelText.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Key dot colour", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10), new java.awt.Color(255, 255, 255))); // NOI18N
        keyLabelText.setFocusable(false);
        keyLabelText.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        keyLabelText.setRequestFocusEnabled(false);
        keyLabelText.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(keyLabelText, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 409, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(keyLabelText, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(208, Short.MAX_VALUE))
        );

        desktopPane.add(jPanel1);
        jPanel1.setBounds(470, 10, 510, 430);

        jPanel2.setBackground(new java.awt.Color(0, 102, 102));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Pattern Scan", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.ABOVE_BOTTOM, new java.awt.Font("Dialog", 0, 10), new java.awt.Color(255, 255, 255))); // NOI18N
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));

        btmScan.setBackground(new java.awt.Color(0, 51, 51));
        btmScan.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        btmScan.setForeground(new java.awt.Color(255, 255, 255));
        btmScan.setText("Scan");
        btmScan.setFocusPainted(false);
        btmScan.setMaximumSize(new java.awt.Dimension(80, 28));
        btmScan.setMinimumSize(new java.awt.Dimension(80, 28));
        btmScan.setPreferredSize(new java.awt.Dimension(100, 40));
        btmScan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btmScanActionPerformed(evt);
            }
        });

        saveFilenameLabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        saveFilenameLabel.setForeground(new java.awt.Color(255, 255, 255));
        saveFilenameLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        saveFilenameLabel.setText("-");
        saveFilenameLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Last save filename", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10), new java.awt.Color(255, 255, 255))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(117, Short.MAX_VALUE)
                .addComponent(btmScan, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveFilenameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btmScan, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveFilenameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        desktopPane.add(jPanel2);
        jPanel2.setBounds(10, 10, 450, 80);

        jPanel3.setBackground(new java.awt.Color(0, 102, 102));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Test", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.ABOVE_BOTTOM, new java.awt.Font("Dialog", 0, 10), new java.awt.Color(255, 255, 255))); // NOI18N
        jPanel3.setForeground(new java.awt.Color(255, 255, 255));

        btmLoadMap.setBackground(new java.awt.Color(0, 51, 51));
        btmLoadMap.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        btmLoadMap.setForeground(new java.awt.Color(255, 255, 255));
        btmLoadMap.setText("<html>Load<br/>Pattern<br/>File</html>");
        btmLoadMap.setFocusPainted(false);
        btmLoadMap.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btmLoadMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btmLoadMapActionPerformed(evt);
            }
        });

        loadFileLabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        loadFileLabel.setForeground(new java.awt.Color(255, 255, 255));
        loadFileLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        loadFileLabel.setLabelFor(btmLoadMap);
        loadFileLabel.setText("-");
        loadFileLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Last load filename", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10), new java.awt.Color(255, 255, 255))); // NOI18N
        loadFileLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        loadFileLabel.setName(""); // NOI18N

        btmVerifty.setBackground(new java.awt.Color(0, 51, 51));
        btmVerifty.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btmVerifty.setForeground(new java.awt.Color(255, 255, 255));
        btmVerifty.setText("<html>START<br/>Test</html>");
        btmVerifty.setEnabled(false);
        btmVerifty.setFocusPainted(false);
        btmVerifty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btmVeriftyActionPerformed(evt);
            }
        });

        spinRunLap.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        spinRunLap.setModel(new javax.swing.SpinnerNumberModel(1, 1, 9999, 1));
        spinRunLap.setAutoscrolls(true);
        spinRunLap.setEnabled(false);
        spinRunLap.setFocusable(false);
        spinRunLap.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinRunLapStateChanged(evt);
            }
        });

        saveOnlyFail.setBackground(new java.awt.Color(0, 102, 102));
        saveOnlyFail.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        saveOnlyFail.setForeground(new java.awt.Color(255, 255, 255));
        saveOnlyFail.setSelected(true);
        saveOnlyFail.setText("File save on FAIL!!!");
        saveOnlyFail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveOnlyFailActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btmLoadMap, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(spinRunLap, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(saveOnlyFail, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btmVerifty, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(loadFileLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(loadFileLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btmLoadMap, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btmVerifty, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinRunLap, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveOnlyFail))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        loadFileLabel.getAccessibleContext().setAccessibleDescription("");

        desktopPane.add(jPanel3);
        jPanel3.setBounds(10, 90, 450, 110);

        jPanel4.setBackground(new java.awt.Color(0, 102, 102));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Command", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.ABOVE_BOTTOM, new java.awt.Font("Dialog", 0, 10), new java.awt.Color(255, 255, 255))); // NOI18N
        jPanel4.setForeground(new java.awt.Color(255, 255, 255));

        btmStop.setBackground(new java.awt.Color(0, 51, 51));
        btmStop.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        btmStop.setForeground(new java.awt.Color(255, 255, 255));
        btmStop.setText("Stop");
        btmStop.setFocusPainted(false);
        btmStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btmStopActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(119, 119, 119)
                .addComponent(btmStop, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(221, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btmStop, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        desktopPane.add(jPanel4);
        jPanel4.setBounds(10, 200, 450, 80);

        outputNormal.setBackground(new java.awt.Color(0, 102, 102));
        saveOutputTypeGroup.add(outputNormal);
        outputNormal.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        outputNormal.setForeground(new java.awt.Color(255, 255, 255));
        outputNormal.setSelected(true);
        outputNormal.setFocusPainted(false);
        outputNormal.setLabel("Normal");
        outputNormal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputNormalActionPerformed(evt);
            }
        });
        desktopPane.add(outputNormal);
        outputNormal.setBounds(620, 510, 130, 21);

        outputDebug.setBackground(new java.awt.Color(0, 102, 102));
        saveOutputTypeGroup.add(outputDebug);
        outputDebug.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        outputDebug.setForeground(new java.awt.Color(255, 255, 255));
        outputDebug.setText("System Debug");
        outputDebug.setFocusPainted(false);
        outputDebug.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputDebugActionPerformed(evt);
            }
        });
        desktopPane.add(outputDebug);
        outputDebug.setBounds(620, 540, 150, 21);

        menuBar.setBackground(new java.awt.Color(0, 204, 204));
        menuBar.setForeground(new java.awt.Color(255, 255, 255));

        jMenu1.setText("ScanSet Utility V1.0 ");
        menuBar.add(jMenu1);

        fileMenu.setMnemonic('f');
        fileMenu.setText("System");

        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Restart");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText("Help");

        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(desktopPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1280, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(desktopPane, javax.swing.GroupLayout.DEFAULT_SIZE, 679, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * add message 
     * @param msg string message 
     * @param type  message type
     */
    private void addMessage(String msg, MessageListEnum type) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String tag = "";
                MessageListData msgData = new MessageListData();
                switch (type) {
                    // genreal message
                    case NORMAL:
                        msgData.setColor(Color.YELLOW);
                        break;
                    case INFO:
                        msgData.setColor(Color.YELLOW);
                        // tag = "INFO:";
                        break;
                    case WARNING:
                        msgData.setColor(Color.MAGENTA);
                        tag = "WARNING:";
                        break;
                    case ERROR:
                        msgData.setColor(Color.RED);
                        // tag = "ERROR:";
                        break;
                    //scan pattern message colour
                    case SCAN_CONNECTED:
                        msgData.setColor(Color.GREEN);
                        break;
                    case SCAN_LOOPBACK:
                        msgData.setColor(Color.WHITE);
                        break;
                    case SCAN_UNCONNECTED:
                        msgData.setColor(Color.BLUE);
                        break;
                    case SCAN_GROUND:
                        msgData.setColor(new Color(0xa0, 0x60, 0x10));
                        break;
                    case SCAN_ERROR:
                        msgData.setColor(Color.RED);
                        tag = "SCAN_FAIL:";
                        break;
                    // test mesage colour
                    case TEST_PASS:
                        msgData.setColor(Color.GREEN);
                        break;
                    case TEST_FAIL:
                        msgData.setColor(Color.RED);
                        tag = "TEST_FAIL:";
                        break;
                    case TEST_LOOPBACK:
                        msgData.setColor(Color.WHITE);
                        break;
                    case TEST_ERROR:
                        msgData.setColor(Color.RED);
                        tag = "TEST_FAIL:";
                        break;
                    // default
                    default:
                        msgData.setColor(Color.YELLOW);
                }
                msgData.setMsg(((debug) ? "DEBUG::" : "") + tag + msg);
                msgModel.addElement(msgData);
                if (msgModel.getSize() > messageLogSize) {
                    msgModel.removeRange(0, 20);
                    msgModel.trimToSize();
                }
                if (msgModel.getSize() >= 10) {
                    messageList.ensureIndexIsVisible(msgModel.getSize() - 1);
                }
            }
        });
    }

    /**
     * add probe scan and probe test message
     * 
     * @param probe number 1 to nsize  
     * @param msg string message
     * @param message type
     */
    private void addProbeMessage(int probe, String msg, MessageListEnum type) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String tag = "";
                MessageListData msgData = new MessageListData();
                switch (type) {
                    // genreal message
                    case NORMAL:
                        msgData.setColor(Color.YELLOW);
                        break;
                    case INFO:
                        msgData.setColor(Color.YELLOW);
                        // tag = "INFO:";
                        break;
                    case WARNING:
                        msgData.setColor(Color.MAGENTA);
                        tag = "WARNING:";
                        break;
                    case ERROR:
                        msgData.setColor(Color.RED);
                        // tag = "ERROR:";
                        break;
                    //scan pattern message colour
                    case SCAN_CONNECTED:
                        msgData.setColor(Color.GREEN);
                        break;
                    case SCAN_LOOPBACK:
                        msgData.setColor(Color.WHITE);
                        break;
                    case SCAN_UNCONNECTED:
                        msgData.setColor(Color.BLUE);
                        break;
                    case SCAN_GROUND:
                        msgData.setColor(new Color(0xa0, 0x60, 0x10));
                        break;
                    case SCAN_ERROR:
                        msgData.setColor(Color.RED);
                        tag = "SCAN_FAIL:";
                        break;
                    // test mesage colour
                    case TEST_PASS:
                        msgData.setColor(Color.GREEN);
                        break;
                    case TEST_FAIL:
                        msgData.setColor(Color.RED);
                        tag = "TEST_FAIL:";
                        break;
                    case TEST_LOOPBACK:
                        msgData.setColor(Color.WHITE);
                        break;
                    case TEST_ERROR:
                        msgData.setColor(Color.RED);
                        tag = "TEST_FAIL:";
                        break;
                    // default
                    default:
                        msgData.setColor(Color.YELLOW);
                }
                msgData.setMsg(((debug) ? "DEBUG::" : "") + tag + msg);
                msgData.setProbeNumber(probe);  // set probe number of this message
                msgModel.addElement(msgData);   // set message 
                if (msgModel.getSize() > messageLogSize) {
                    msgModel.removeRange(0, 20);
                    msgModel.trimToSize();
                }
                if (msgModel.getSize() >= 10) {
                    messageList.ensureIndexIsVisible(msgModel.getSize() - 1);
                }
            }
        });
    }    
    
    private void clearMessage() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                msgModel.clear();
            }
        });
    }

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        appExit();
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void appExit() {
        try {
            if (pm != null) {
                pm.disableAllProbes();
                pm.closeProbes();
            }
        } catch (IOException ex) {
            addMessage("close probe error!!!", MessageListEnum.ERROR);
        }
        System.exit(0);
    }

    private void btmStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btmStopActionPerformed
        addMessage("Stop !!!", MessageListEnum.INFO);
        setKeyLabelTextClear();
        loopStartStop = false;
        debug = false;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                stoploop();
                outputNormal.setSelected(true);
            }
        });
    }//GEN-LAST:event_btmStopActionPerformed

    private void spinRunLapStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinRunLapStateChanged
        //System.out.println(spinRunLap.getValue());
        numberOfLoop = (int) spinRunLap.getValue();
    }//GEN-LAST:event_spinRunLapStateChanged

    private void setKeyLabelTextScan() {

        keyLabelText.setText(keylabelScan);
    }

    private void setKeyLabelTextResult() {
        keyLabelText.setText(keylabelTest);
    }

    private void setKeyLabelTextClear() {
        //  keyLabelText.setText("");
    }

    private void btmScanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btmScanActionPerformed

        addMessage("Start scanning pattern", MessageListEnum.INFO);
        saveFilenameLabel.setText("-");
        loadFileLabel.setText("-");
        testStatusLabel.setForeground(Color.YELLOW);
        testStatusLabel.setText("***");
        String stringdatetime = getCurrentDateTime();
        String filename = "pattern-" + stringdatetime;
        tg.setCurrnetDateTimeString(stringdatetime);
        setKeyLabelTextScan();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    dataMap.resetPatternData();  // clear data store to 0xff
                } catch (ScanAndTestDataException e) {
                    addMessage("System internal Error !!!", MessageListEnum.ERROR);
                }
                Color tmp = btmScan.getBackground();
                Thread saveth = new Thread() {
                    public void run() {
                        try {
                            int failCount = dataMap.getScanAnyErrorPattern();
                            if (failCount > 0) {
                                addMessage("Scan Error!!!", MessageListEnum.ERROR);
                                // if error
                                testStatusLabel.setForeground(Color.RED);
                                testStatusLabel.setText("ERROR");
                            } else {
                                testStatusLabel.setForeground(Color.YELLOW);
                                testStatusLabel.setText("OK");
                            }
                            String filenameFlag = (failCount > 0) ? "F" : "P";
                            addMessage("Create pattern file and save, filename = " + filename + filenameFlag + fileExtension, MessageListEnum.INFO);
                            try {
                                tg.genTemplate(usertemplate + "/" + filename + filenameFlag, dataMap);
                            } catch (TemplateGenException e) {
                                addMessage("Error create Pattern file!!! ", MessageListEnum.ERROR);
                            }
                        } catch (ScanAndTestDataException e) {
                            addMessage("Internal Error!!!", MessageListEnum.ERROR);
                        }
                        btmScan.setBackground(tmp);
                        btmScan.setEnabled(true);
                        btmLoadMap.setEnabled(true);
                        btmVerifty.setEnabled(true);
                        spinRunLap.setEnabled(true);
                        outputNormal.setEnabled(true);
                        outputDebug.setEnabled(true);
                        saveFilenameLabel.setText(filename + fileExtension);
                        addMessage("File save OK!", MessageListEnum.INFO);
                    }
                };
                Thread scanth = new Thread() {
                    public void run() {
                        btmScan.setEnabled(false);
                        btmLoadMap.setEnabled(false);
                        btmVerifty.setEnabled(false);
                        outputNormal.setEnabled(false);
                        outputDebug.setEnabled(false);
                        btmScan.setBackground(Color.RED);
                        spinRunLap.setEnabled(false);
                        ScanMap();
                        if (currentprobe == (nprobe - 1)) {

                            saveth.start();
                            addMessage("Scan Completed", MessageListEnum.INFO);
                        } else {
                            addMessage("User Abort Scan!!!", MessageListEnum.INFO);
                            btmScan.setBackground(tmp);
                            btmScan.setEnabled(true);
                            btmLoadMap.setEnabled(true);
                            btmVerifty.setEnabled(false);
                            outputNormal.setEnabled(true);
                            outputDebug.setEnabled(true);
                        }
                        try {
                            pm.disableAllProbes();
                        } catch (IOException e) {
                            addMessage("Probe IO error!!!", MessageListEnum.ERROR);
                            exitOnError();
                        }
                    }
                };
                scanth.start();
            }
        });
    }//GEN-LAST:event_btmScanActionPerformed

    private void ScanMap() {
        try {
            pm.scanTestMapFromProbe(1, dataMap.getPatternData()); // delay, data, row, col
        } catch (IOException ex) {
            addMessage("IO Error!!!", MessageListEnum.ERROR);
            exitOnError();

        }
    }

    public String dumphex(byte[] data, int len) {
        StringBuffer t = new StringBuffer();
        for (int r = len - 1; r >= 0; r--) {
            t.append(String.format("%02X:", data[r]));
        }
        return t.toString();
    }

    public String dumpint(int[] data, int len) {
        StringBuffer t = new StringBuffer();
        for (int r = len - 1; r >= 0; r--) {
            t.append(String.format("%d:", data[r]));
        }
        return t.toString();
    }
    long error = 0L;

    private boolean matchhex(byte[] data1, byte[] data2) {
        if (data1.length != data2.length) {
            error++;
            return false;
        }
        for (int z = 0; z < data1.length; z++) {
            if ((data1[z] & 0xff) != (data2[z] & 0xff)) {
                error++;
                return false;
            }
        }
        return true;
    }

    private synchronized void stoploop() {
        if (pm != null) {
            pm.stopAnyTestRun();
        }
    }

    /**
     *
     * @param curdate
     */
    private String getCurrentDateTime() {
        Calendar cal = Calendar.getInstance();
        Date curdate = cal.getTime();
        SimpleDateFormat sf = new SimpleDateFormat(fileDateTimeFormatting);
        String cur = sf.format(curdate);
        return cur;
    }


    private void btmClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btmClearActionPerformed
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Color tmp = btmClear.getBackground();
                btmClear.setEnabled(false);
                btmClear.setBackground(Color.RED);
                clearMessage();
                testStatusLabel.setText("***");
                saveFilenameLabel.setText("-");
                loadFileLabel.setText("-");
                btmClear.setBackground(tmp);
                btmClear.setEnabled(true);
            }
        });
        addMessage("Clear message display", MessageListEnum.INFO);
    }//GEN-LAST:event_btmClearActionPerformed

    private void btmSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btmSaveActionPerformed
        String filename = userreport + "/test-" + getCurrentDateTime();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Color tmp = btmSave.getBackground();
                Thread saveth = new Thread() {
                    public void run() {
                        // addMessage("Generate Pattern File and Save");
                        btmSave.setEnabled(false);
                        btmSave.setBackground(Color.RED);
                        //  tg.genTemplate(filename, map);
                        btmSave.setBackground(tmp);
                        btmSave.setEnabled(true);
                        //  addMessage("File save OK!");
                        btmSave.setBackground(tmp);
                        btmSave.setEnabled(true);
                    }
                };
                saveth.start();
            }
        });
        //addMessage("Save test report to file, filename = " + filename + fileExtension);
    }//GEN-LAST:event_btmSaveActionPerformed

    class PatternFileFilter extends FileFilter {

        public boolean accept(File file) {
            String filename = file.getName();
            return filename.endsWith(fileExtension);
        }

        public String getDescription() {
            return "*" + fileExtension;
        }
    }

    class FolderRestrictView extends FileSystemView {

        private final File[] rootDirectories;

        FolderRestrictView(File rootDirectory) {
            this.rootDirectories = new File[]{rootDirectory};
        }

        FolderRestrictView(File[] rootDirectories) {
            this.rootDirectories = rootDirectories;
        }

        @Override
        public File[] getRoots() {
            return rootDirectories;
        }

        @Override
        public File getHomeDirectory() {
            return rootDirectories[0];
        }

        @Override
        public boolean isRoot(File file) {
            for (File root : rootDirectories) {
                if (root.equals(file)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public File createNewFolder(File containingDir) throws IOException {
            //throw new UnsupportedOperationException(""); 
            return null;
        }

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

    private void btmLoadMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btmLoadMapActionPerformed
        loadFileLabel.setText("-");
        testStatusLabel.setText("***");
        setKeyLabelTextScan();
        loopStartStop = false;   // stop any loop
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Color tmp = btmLoadMap.getBackground();
                Thread loadmapth = new Thread() {
                    public void run() {
                        String selectedfile = null;
                        btmLoadMap.setBackground(Color.RED);
                        btmScan.setEnabled(true);
                        btmLoadMap.setEnabled(false);
                        btmVerifty.setEnabled(false);
                        spinRunLap.setEnabled(false);
                        saveOnlyFail.setEnabled(false);
                        FolderRestrictView fs = new FolderRestrictView(new File(usertemplate));
                        JFileChooser openFile = new PatternFileChooser(fs.getHomeDirectory(), fs);
                        openFile.setDragEnabled(false);
                        openFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
                        openFile.setFileHidingEnabled(true);
                        openFile.setDialogTitle("Open Pattern File");
                        openFile.setFileFilter(new PatternFileFilter());
                        openFile.setAcceptAllFileFilterUsed(false);
                        openFile.setCurrentDirectory(new File(usertemplate));
                        openFile.setControlButtonsAreShown(true);
                        JDialog ndlg = new JDialog(ScanSetMainUI.this);
                        int ret = openFile.showOpenDialog(ndlg);

                        if (ret == JFileChooser.APPROVE_OPTION) {
                            selectedfile = openFile.getSelectedFile().getName();
                            addMessage("Pattern file selected " + selectedfile, MessageListEnum.INFO);
                            loadFileLabel.setText(selectedfile);
                            try {
                                dataMap.resetPatternData();
                            } catch (ScanAndTestDataException e) {
                                addMessage("System internal Error !!!", MessageListEnum.ERROR);
                            }
                            mpanel.setData(dataMap, false);  // show read map on quick view
                            mpanel.repaint();
                            // processing loaded file
                            readPatternForm.setPatternDdata(dataMap); // setup data store;
                            //addMessage ("full path = " + usertemplate + "/" + selectedfile);
                            try {
                                readPatternForm.readPatternForm(usertemplate + "/" + selectedfile);
                                addMessage("Done, Load Pattern File, filename = " + selectedfile, MessageListEnum.INFO);
                                if (dataMap.getScanAnyErrorPattern() > 0) {
                                    addMessage("Loaded Pattern file contain Error!!!", MessageListEnum.ERROR);
                                }
                                //setKeyLabelTextResult();  // load key dot colour table
                                mpanel.setData(dataMap, false);  // show read map on quick view
                                mpanel.repaint();
                                spinRunLap.setEnabled(true);
                                btmVerifty.setEnabled(true);
                                saveOnlyFail.setEnabled(true);
                            } catch (ReadPatternFormException e) {
                                addMessage("Read Pattern form error!!!", MessageListEnum.ERROR);
                            } catch (FileNotFoundException e) {
                                addMessage("Pattern File not found.", MessageListEnum.INFO);
                            } catch (IOException e) {
                                addMessage("Read Pattern File Error!!!", MessageListEnum.ERROR);
                            } catch (ScanAndTestDataException ex) {
                                addMessage("System Data Error!!!", MessageListEnum.ERROR);
                            }
                        } else {
                            addMessage("No pattern file load", MessageListEnum.INFO);
                        }
                        btmLoadMap.setBackground(tmp);
                        btmScan.setEnabled(true);
                        btmLoadMap.setEnabled(true);
                        saveOnlyFail.setEnabled(true);

                    }
                };
                loadmapth.start();
            }
        });

    }//GEN-LAST:event_btmLoadMapActionPerformed

    private void btmVeriftyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btmVeriftyActionPerformed
        loopStartStop = true;  // enable looping 
        Color tmp = btmVerifty.getBackground();
        btmVerifty.setBackground(Color.RED);
        btmVerifty.setEnabled(false);
        btmScan.setEnabled(false);
        btmLoadMap.setEnabled(false);
        spinRunLap.setEnabled(false);
        saveOnlyFail.setEnabled(false);
        //loadFileLabel.setText("-");
        setKeyLabelTextResult();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Thread() {
                    public void run() {
                        int loopCount = 0;
                        int errorCount = 0;
                        int passCount = 0;
                        int failCount = 0;
                        int initialLoopCount = numberOfLoop;
                        testStatusLabel.setForeground(Color.YELLOW);
                        testStatusLabel.setText("***");
                        for (; numberOfLoop > 0 && loopStartStop; numberOfLoop--) {
                            // if (numberOfLoop == 1) {  // keep of clear status if loop count =1 
                            //      testStatusLabel.setForeground(Color.YELLOW);
                            //      testStatusLabel.setText("***");
                            //  }
                            loopCount++;
                            spinRunLap.setValue(numberOfLoop);
                            //spinRunLap.invalidate();
                            try {
                                dataMap.resetTestData();
                            } catch (ScanAndTestDataException e) {
                                addMessage("System internal Error !!!", MessageListEnum.ERROR);
                            }
                            String stringdatetime = getCurrentDateTime();
                            String filename = "report-" + stringdatetime ;
                            String fillpath = userreport + "/" + filename;
                            addMessage("Start test, remain Lap = " + numberOfLoop, MessageListEnum.INFO);
                            try {
                                // test and match with pattern data and pass fail as result 
                                pm.doTestMapWithAllProbes(20, dataMap.getPatternData(), dataMap.getTestData());
                                pm.disableAllProbes();    // disable all driver outputs
                                try {
                                    int totalfault = dataMap.getTestError() + dataMap.getTestFail();
                                    if (dataMap.getTestError() > 0) {
                                        addMessage("Test Error!!!", MessageListEnum.ERROR);
                                    }
                                    if (dataMap.getTestFail() > 0) {
                                        addMessage("Test Fail!!!", MessageListEnum.ERROR);
                                    }
                                    if (currentprobe == (nprobe - 1)) {
                                        addMessage("Creating test report", MessageListEnum.INFO);
                                        // look for error in test result testMap data array 
                                        if (initialLoopCount == 1) {
                                            if (totalfault == 0) {
                                                // if pass
                                                testStatusLabel.setForeground(Color.YELLOW);
                                                testStatusLabel.setText("PASS");
                                            } else if (dataMap.getTestFail() > 0) {
                                                // if fail
                                                testStatusLabel.setForeground(Color.RED);
                                                testStatusLabel.setText("FAIL");
                                            } else if (totalfault > 0) {
                                                // if error
                                                testStatusLabel.setForeground(Color.RED);
                                                testStatusLabel.setText("ERROR");
                                            }
                                        } else {
                                            if (dataMap.getTestError() > 0) {
                                                testStatusLabel.setForeground(Color.RED);
                                                testStatusLabel.setText("ERROR");
                                                addMessage("Test Error stop test run!!!", MessageListEnum.ERROR);
                                                loopStartStop = false;
                                            }
                                            if (totalfault > 0) {
                                                failCount++;
                                            } else {
                                                passCount++;
                                            }
                                            // pass and fail status
                                            if (failCount > 0) {
                                                testStatusLabel.setForeground(Color.RED);
                                            } else {
                                                testStatusLabel.setForeground(Color.YELLOW);
                                            }
                                            testStatusLabel.setText("PASS=" + passCount + ", FAIL=" + failCount);
                                        }

                                        if (initialLoopCount == 1) {
                                            String fstr = String.format("_%s", ((totalfault == 0) ? "P" : "F"));
                                            rgen.setCurrnetDateTimeString(stringdatetime);
                                            rgen.setLap(0);
                                            rgen.genReport(fillpath + fstr, dataMap);
                                            addMessage("Test Report file save Ok! Filename =" + filename + fstr + ".xls", MessageListEnum.INFO);
                                        } else if (initialLoopCount > 1) {
                                            if ((saveOnlyFail.isSelected() && (dataMap.getTestFail() + dataMap.getTestError()) > 0)
                                                    || !saveOnlyFail.isSelected()) {
                                                String fstr = String.format("_%04d%s", loopCount, ((totalfault == 0) ? "P" : "F"));
                                                rgen.setCurrnetDateTimeString(stringdatetime);
                                                rgen.setLap(loopCount);
                                                rgen.genReport(fillpath + fstr, dataMap);
                                                addMessage("Test Report file save Ok! Filename = " + filename + fstr + ".xls", MessageListEnum.INFO);
                                            }
                                        }
                                    } else {
                                        addMessage("User Abort Test!!!", MessageListEnum.INFO);
                                    }
                                } catch (ReportGenException e) {
                                    addMessage("Error create report!!!", MessageListEnum.ERROR);
                                }
                            } catch (IOException e) {
                                addMessage("Probe IO Error!!!", MessageListEnum.ERROR);
                            } catch (ScanAndTestDataException e) {
                                addMessage("Internal Error!!!", MessageListEnum.ERROR);
                            }
                            try {
                                pm.disableAllProbes();  // switch off all probe before exit;
                            } catch (IOException e) {
                                addMessage("Probe IO error!!!", MessageListEnum.ERROR);
                                exitOnError();
                            }
                        }
                        btmVerifty.setBackground(tmp);
                        btmVerifty.setEnabled(true);
                        btmScan.setEnabled(true);
                        btmLoadMap.setEnabled(true);
                        spinRunLap.setEnabled(true);
                        saveOnlyFail.setEnabled(true);
                        spinRunLap.setValue(1);
                        numberOfLoop = 1;
                    }
                }.start();
            }
        });
    }//GEN-LAST:event_btmVeriftyActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        appExit();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // HelpDialog dialog = new HelpDialog(new javax.swing.JFrame(), true);
                dialog.setModal(true);
                dialog.setVisible(true);
            }
        });
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void outputNormalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputNormalActionPerformed
        debug = false;
        dataMap.setDebug(false);
    }//GEN-LAST:event_outputNormalActionPerformed

    private void outputDebugActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputDebugActionPerformed
        debug = true;
        dataMap.setDebug(true);
    }//GEN-LAST:event_outputDebugActionPerformed

    private void saveOnlyFailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveOnlyFailActionPerformed
        saveOnlyFailflag = saveOnlyFail.isSelected();
    }//GEN-LAST:event_saveOnlyFailActionPerformed

    private void exitOnError() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ToolsErrorDialog dialog = new ToolsErrorDialog(new javax.swing.JFrame(), true);
                dialog.setModal(true);
                dialog.setVisible(true);
                appExit();
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JButton btmClear;
    private javax.swing.JButton btmLoadMap;
    private javax.swing.JButton btmSave;
    private javax.swing.JButton btmScan;
    private javax.swing.JButton btmStop;
    private javax.swing.JButton btmVerifty;
    private java.awt.Canvas canvas1;
    private javax.swing.JDesktopPane desktopPane;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JButton jButton3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel keyLabelText;
    private javax.swing.JLabel loadFileLabel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JList messageList;
    private javax.swing.JScrollPane msgPanel;
    private javax.swing.JRadioButton outputDebug;
    private javax.swing.JRadioButton outputNormal;
    private javax.swing.JLabel saveFilenameLabel;
    private javax.swing.JCheckBox saveOnlyFail;
    private javax.swing.ButtonGroup saveOutputTypeGroup;
    private javax.swing.JSpinner spinRunLap;
    private javax.swing.JLabel testStatusLabel;
    // End of variables declaration//GEN-END:variables

}
