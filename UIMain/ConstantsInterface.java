package UIMain;

/**
 *
 * @author de
 */
public interface ConstantsInterface {

    // useful folder for this application
    static final String setupFolder = "setup";
    static final String templateFolder = "template";
    static final String reportFolder = "report";
    static final String fileExtension = ".xls";
    
    // GUI defailts

    static final int screenX = 0; // top left position
    static final int screenY = 0;
    static final int screenWidth = 1000; // screen size
    static final int screenHeight = 700;
    static final int probePerAdaptor = 48; // number of probe per adaptor
    static final int probePerSheet = probePerAdaptor / 2; //number of probe per sheet
    static final int totalAdaptor = 7; // number of adaptor installed

    // 
    static final int messageLogSize = 2000; // static disaplay length

    // mpanel color key code in scan and load pattern file
    static final String keylabelScan = "<html>\n"
            + "<span color='#00ff00'>CONNECTED</span><br/>\n"
            + "<span color='#ffffff'>LOOPBACK</span><br/>\n"
            + "<span color='#0000ff'>UNCONNECT</span><br/>\n"
            //   + " <span color='#ff60ff'>POWERED</span><br/>  \n"
            + "<span color='#a06010'>GROUND</span><br/>\n"
            //   + "<span color='black'>NOT TEST</span><br/>  \n"
            + "<span color='#ff0000'>ERROR</span><br/>\n"
            + "</html>\n";

    // mpanel color key code in test
    static final String keylabelTest = "<html>\n"
            + "<span color='#00ff00'>PASSED</span><br/>\n" // green
            + "<span color='#ffffff'>LOOPBACK</span><br/>\n" // white
            + "<span color='#ff0000'>FAIL/ERROR</span><br/>\n" // red
            + "</html>\n";

    // mpanel size
    static final int mpanelXPos =580;
    static final int mpanelYPos = 36;
    static final int mpanelSize = 384 ; // number of dot per probe = 48 X number of adaptoras
    // file output data time formatting
    static final String fileDateTimeFormatting = "yyyy-MM-dd-HH-mm-ss";

    // help dialog text
    static final String texthelp = "<html> \n"
            + "<br/>\n"
            + "For ScanOS Linux V1.0 Embedded<br/>\n"
            + "https://www.gnu.org/licenses/license-list.html<br/>\n"
            + "http://www.linuxfoundation.org/<br/>\n"
            + "http://www.libreoffice.org<br/>\n"
            + "</html>";

    // class ToolsError Dialog text
    static final String toolsErrorText
            = "<html> \n"
            + "Problem start application<br/> \n"
            + "Missing probe adaptor <br/> \n"
            + "Please check!!!<br/> \n"
            + "Press 'Exit'<br> \n"
            + "<html> \n";

    //Class ReadLabelForm
    String labelFileName = "probenametable.xls";

    //Class ReadPatternForm
    static final int tableRowOffset = 5; // table row offset
    static final int tableColOffset = 5; // table col offset
    static final int totalCol = 24;      // total probe per sheet of table

   

}
