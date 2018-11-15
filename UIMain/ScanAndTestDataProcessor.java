package UIMain;

import java.awt.Color;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author de
 */
public class ScanAndTestDataProcessor extends ScanAndTestData implements ConstantsInterface {

    private static final long serialVersionUID = -4232971844894064435L;
    private int psize = 0;

    public ScanAndTestDataProcessor(int size) throws ScanAndTestDataException {
        super(size);
        psize = size;
    }

    /*-----------------scan pattern--------------------*/
    /**
     * get number of scan fail if any return
     *
     * @param proben driver number
     * @return
     */
    public int getScanFailProbe(int proben) throws ScanAndTestDataException {
        if (scanPatternData == null) {
            throw new ScanAndTestDataException("Error Scan not init!");
        }
        if (proben < 0 || proben > psize) {
            throw new ScanAndTestDataException("Error out of range!");
        }
        int scanFail = 0;
        for (int j = 0; j < psize; j++) {
            int dt = scanPatternData[proben][j] & 0xff;  // driver , probe pair
            if (dt == 0xff) {
                // nothing , default black;
            } else if ((dt & 0x09) == 0x09 || (dt & 0x11) == 0x11) {                   // check error state, if pwd + connected, gnd + connected
                scanFail++;
            } else if ((dt & 0x10) == 0x10) {                   // scan powered
                // Pink colour
            } else if ((dt & 0x08) == 0x08) {                   // scan grounded
                // brown colour
            } else if (proben == j) {
                if (dt == 0x40 || (dt & 0x80) == 0x80) {  // scan no loopback, error, powered, = error
                    scanFail++;                 // 0x40 probe loopback test error 
                } else if (dt == 0x41) {
                    //scan  0x41 probe lookback test ok
                }
            }/*else if ((dt & 0x10) > 0) {                   // scan leak detect, power , RED
             scanFail ++;         // 
             }*/ else if (dt == 0x01) {               // scan is connected
                // green
                int odt = scanPatternData[j][proben] & 0xff;  // orgthogonal connected pattern test by reverse x and y
                if (odt != dt) {
                    //System.out.println ("data = " + dt + ", odata =" + odt);
                    scanFail++;                   // if not orthogonal error
                }
            } else if (dt == 0x00) {               // scan is unconnected
                // blue
                int odt = scanPatternData[j][proben] & 0xff;  // not power or gnd orgthogonal unconnected pattern test by reverse x and y
                if (odt != 0x08 && odt != 0x10 && odt != dt) {
                    System.out.println("data = " + dt + ", odata =" + odt);
                    scanFail++;                   // if not orthogonal error
                }
            } else if ((dt & 0x80) == 0x80 || (dt & 0x04) == 0x04) { // scan is probe error
                scanFail++;                     // red
            } else if ((dt & 0x04) == 0x04) {                   // scan is leaked
                scanFail++;         // 
            } else {
                // scan is not any, black
            }
        }
        return scanFail;
    }

    /*-----------------scan pattern--------------------*/
    /**
     * get number of power and ground scan fail if any return
     *
     * @param proben driver number
     * @return
     */
    public int getScanFailGndPwr() throws ScanAndTestDataException {
        if (scanPatternData == null) {
            throw new ScanAndTestDataException("Error Scan not init!");
        }
        int scanFail = 0;
        for (int i = 0; i < psize; i++) {
            int dt = scanPatternData[0][i] & 0xff;  // driver , probe pair
            if (dt == 0xff) {
                // nothing , default black;
            }
            if ((dt & 0x10) == 0x10) {             // scan powered
                for (int j = 0; j < psize; j++) {
                    if (dt != (scanPatternData[j][i] & 0x10)) {   // check if all driver see same power
                        System.out.println("data = " + dt + ", odata =" + scanPatternData[j][i]);
                        scanFail++;
                    }
                }
            } else if ((dt & 0x08) == 0x08) {      // scan grounded
                for (int j = 0; j < psize; j++) {
                    if (dt != (scanPatternData[j][i] & 0x08)) { // check if all driver see same gnd
                        System.out.println("data = " + dt + ", odata =" + scanPatternData[j][i]);
                        scanFail++;
                    }
                }
            } else {
                // scan is not any, black
            }
        }
        return scanFail;
    }

    /**
     * get number of scan fail if any return
     *
     * @return
     */
    public int getScanAnyErrorPattern() throws ScanAndTestDataException {
        if (scanPatternData == null) {
            throw new ScanAndTestDataException("Error Scan not init!");
        }
        int scanFail = 0;
        scanFail += getScanFailGndPwr();
        for (int i = 0; i < psize; i++) {
            scanFail += getScanFailProbe(i);
        }
        return scanFail;
    }

    /*-----------------test -----------------------*/
    /**
     * test number of fail per probe
     *
     * @param proben probe driver number 0 to N
     * @return
     */
    public int getTestProbeTotalFail(int drivern) throws ScanAndTestDataException {
        if (testData == null) {
            throw new ScanAndTestDataException("Error test data not init!");
        }
        if (drivern < 0 || drivern > psize) {
            throw new ScanAndTestDataException("Error out of range!");
        }
        int testprobeFail = 0;
        for (int j = 0; j < psize; j++) {
            if (isProbeFail_Error(drivern, j, false)) {  // false, set fail mode test
                testprobeFail++;
            }
        }
        return testprobeFail;
    }

    /**
     * test number of fail per card
     *
     * @param cardN 0 to cardN
     * @param proben probe driver number 0 to N
     * @return
     */
    public int getTestCardNTotalFail(int cardn, int drivern) throws ScanAndTestDataException {
        if (testData == null) {
            throw new ScanAndTestDataException("Error test data not init!");
        }
        if (cardn < 0 || drivern < 0 || cardn > psize || drivern > psize) {
            throw new ScanAndTestDataException("Error out of range!");
        }
        int testcardFail = 0;
        int startproben = cardn * probePerSheet;  // 24 probe per sheet
        int endproben = startproben + probePerSheet;
        for (int j = startproben; j < endproben; j++) {
            if (isProbeFail_Error(drivern, j, false)) {  // false, set fail mode test
                testcardFail++;
            }
        }
        return testcardFail;
    }

    /**
     * test number of error per probe
     *
     * @param proben probe driver number 0 to N
     * @return
     */
    public int getTestProbeTotalError(int drivern) throws ScanAndTestDataException {
        if (testData == null) {
            throw new ScanAndTestDataException("Error test data not init!");
        }
        if (drivern < 0 || drivern > psize) {
            throw new ScanAndTestDataException("Error out of range!");
        }
        int testprobeErr = 0;
        for (int j = 0; j < psize; j++) {
            if (isProbeFail_Error(drivern, j, true)) { // true, set error mode test
                testprobeErr++;
            }
        }
        return testprobeErr;
    }

    /**
     * test number of fail per card
     *
     * @param cardN 0 to cardN
     * @param proben probe driver number 0 to N
     * @return
     */
    public int getTestCardNTotalError(int cardn, int drivern) throws ScanAndTestDataException {
        if (testData == null) {
            throw new ScanAndTestDataException("Error test data not init!");
        }
        if (cardn < 0 || drivern < 0 || cardn > psize || drivern > psize) {
            throw new ScanAndTestDataException("Error out of range!");
        }
        int testcardFail = 0;
        int startproben = cardn * probePerSheet;  // 24 probe per sheet
        int endproben = startproben + probePerSheet;
        for (int j = startproben; j < endproben; j++) {
            if (isProbeFail_Error(drivern, j, true)) {  // true, set error mode test
                testcardFail++;
            }
        }
        return testcardFail;
    }

    /**
     * test if driver to probe error or fail
     *
     * @param drivern driver N 0 to nsize
     * @param proben probe N 0 to nsize
     * @param errorOrFail false = fail, true = error
     * @return
     * @throws ScanAndTestDataException
     */
    public boolean isProbeFail_Error(int drivern, int proben, boolean failOrError) throws ScanAndTestDataException {
        if (testData == null) {
            throw new ScanAndTestDataException("Error test data not init!");
        }
        if (drivern < 0 || proben < 0 || drivern > psize || proben > psize) {
            throw new ScanAndTestDataException("Error out of range!");
        }
        int dt = testData[drivern][proben] & 0xff;
        if (failOrError) {    // if true  Error
            if ((dt & 0x40) != 0x40) {
                return true;
                // untested has error, RED 
            } else if (dt == 0xff) {
                // unknown
            } else if (dt == 0xCA) {
                // has error, gnd test error, RED, 
            } else if (dt == 0xC3) {
                // has error, power test error, RED 
            } else if (dt == 0xD0) {
                return true;
                // has error, loopback test erro, RED 
            } else if ((dt & 0x80) == 0x80) {
                // connection test has error, RED 
            } else if ((dt & 0x90) == 0x10) {
                // loopback test passed, white
            } else if ((dt & 0x80) == 0) {
                // connection test passed, GREEN
            }
        } else {
            if ((dt & 0x40) != 0x40) {
                // untested has error, RED 
            } else if (dt == 0xff) {
                // unknown
            } else if (dt == 0xCA) {
                return true;
                // has error, gnd test error, RED, 
            } else if (dt == 0xC3) {
                return true;
                // has error, power test error, RED 
            } else if (dt == 0xD0) {
                // has error, loopback test erro, RED 
            } else if ((dt & 0x80) == 0x80) {
                return true;
                // connection test fail, RED 
            } else if ((dt & 0x90) == 0x10) {
                // loopback test passed, white
            } else if ((dt & 0x80) == 0) {
                // connection test passed, GREEN
            }
        }
        return false;
    }

    /**
     * get number of test error if any return
     *
     * @return
     */
    public int getTestError() throws ScanAndTestDataException {
        int testError = 0;
        for (int i = 0; i < psize; i++) {
            testError += getTestProbeTotalError(i);
        }
        return testError;
    }

    /**
     * get number of test fail if any return
     *
     * @return
     */
    public int getTestFail() throws ScanAndTestDataException {
        int testFailCount = 0;
        for (int i = 0; i < psize; i++) {
            testFailCount += getTestProbeTotalFail(i);
        }
        return testFailCount;
    }

    /**
     * get scan pattern probe display colour code in RGB
     *
     * @param driver
     * @param probe
     * @return
     */
    public Color getScanResultViewColor(int driver, int probe) throws ScanAndTestDataException {
        if (scanPatternData == null) {
            throw new ScanAndTestDataException("Error Scan not init!");
        }
        if (driver < 0 || probe < 0 || driver > psize || probe > psize) {
            throw new ScanAndTestDataException("Error out of range!");
        }
        int dt = getPatternData(driver, probe);
        if ((dt & 0xff) == 0xff) {
            return Color.BLACK;  // nothing , default black;
        } else if ((dt & 0x09) == 0x09 || (dt & 0x11) == 0x11) {     // check error state, if pwd + connected, gnd + connected
            return (new Color(0xff, 0x00, 0x00));            // red colour
        } else if ((dt & 0x10) == 0x10) {                   // scan powered
            return (new Color(0xff, 0x60, 0xff));        // Pink colour
        } else if ((dt & 0x08) == 0x08) {                   // scan grounded
            return (new Color(0xa0, 0x60, 0x10));        // brown colour
        } else if (driver == probe) {
            if ((dt & 0xff) == 0x40 || (dt & 0x80) == 0x80) {  // scan no loopback, error, powered, = error
                return (Color.RED);                    // 0x40 probe loopback test error 
            } else if ((dt & 0xff) == 0x41) {
                return (Color.WHITE);                  //scan  0x41 probe lookback test ok
            }
        }/*else if ((dt & 0x10) > 0) {                   // scan leak detect, power , RED
         color = new Color(0xff, 0x00, 0x00);        // 
         }*/ else if ((dt & 0xff) == 0x01) {               // scan is connected
            return (new Color(0x00, 0xff, 0x00));        // green
        } else if ((dt & 0xff) == 0x00) {               // scan is unconnected
            return (new Color(0x00, 0x00, 0xff));        // blue
        } else if ((dt & 0x80) == 0x80 || (dt & 0x04) == 0x04) { // scan is probe error
            return (Color.RED);                          // red
        } else if ((dt & 0x04) == 0x04) {                   // scan is leaked
            return (new Color(0xa0, 0x20, 0x00));        // 
        }
        return (new Color(0x00, 0x00, 0x00));   // scan is not any, black
    }

    /**
     * ref method for all similar get test Result report color
     *
     * @param driver
     * @param probe
     * @return
     */
    public Color getTestResultViewColor(int driver, int probe) throws ScanAndTestDataException {
        if (testData == null) {
            throw new ScanAndTestDataException("Error test data not init!");
        }
        if (driver < 0 || probe < 0 || driver > psize || probe > psize) {
            throw new ScanAndTestDataException("Error out of range!");
        }
        int dt = getTestData(driver, probe) & 0xff;
        if ((dt & 0x40) != 0x40) {
            return new Color(0xff, 0x00, 0x00);    // has error, RED 
        } else if (dt == 0xff) {
            return new Color(0x00, 0x00, 0x00);    // unknown
        } else if (dt == 0xCA) {
            return new Color(0xff, 0x00, 0x00);    // has error, gnd test error, RED, 
        } else if (dt == 0xC3) {
            return new Color(0xff, 0x00, 0x00);    // has error, power test error, RED 
        } else if (dt == 0xD0) {
            return new Color(0xff, 0x00, 0x00);    // has error, loopback test erro, RED 
        } else if ((dt & 0x80) == 0x80) {
            return new Color(0xff, 0x00, 0x00);    // connection test has error, RED 
        } else if ((dt & 0x90) == 0x10) {
            return new Color(0xff, 0xff, 0xff);    // loopback test passed, white
        } else if ((dt & 0x80) == 0) {
            return new Color(0x00, 0xff, 0x00);     // connection test passed, GREEN
        }
        return new Color(0x00, 0x00, 0x00);     // unknown not test, Black
    }

    /**
     * return scan pattern form character code excel form
     *
     * @param driver
     * @param probe
     * @return
     */
    public String getScanSaveToExcelCode(int driver, int probe) throws ScanAndTestDataException {
        if (scanPatternData == null) {
            throw new ScanAndTestDataException("Error Scan not init!");
        }
        if (driver < 0 || probe < 0 || driver > psize || probe > psize) {
            throw new ScanAndTestDataException("Error out of range!");
        }
        int dt = getPatternData(driver, probe);
        if ((dt & 0xff) == 0xff) {
            return ("U");  // unknown nothing
        } else if ((dt & 0x09) == 0x09 || (dt & 0x11) == 0x11) {                   // check error state, if pwd + connected, gnd + connected
            return ("F");   // impossible state
        } else if (dt == 0x10 || dt == 0x11 || dt == 0x51) {                   // powered
            return ("P");
        } else if (dt == 0x08 || dt == 0x48) {                   // grounded
            return ("G");
        } else if (driver == probe) {
            if ((dt & 0xff) == 0x40 || (dt & 0x80) > 0 || (dt & 0x04) > 0) {
                return ("E");                            // 0x40 probe loopback test error 
            } else if ((dt & 0xff) == 0x41) {
                return ("L");                  // 0x41 probe lookback test ok
            }
        } else if ((dt & 0x40) > 0) {                   // leak detect
            return ("E");
        } else if ((dt & 0xff) == 0x01) {               // connected
            return ("C");
        } else if ((dt & 0xff) == 0x00) {               // unconnected
            return ("N");
        } else if ((dt & 0x80) > 0 || (dt & 0x04) > 0) { // probe error
            return ("E");
        } else if ((dt & 0x04) > 0) {                   // leaked
            return ("E");
        }
        return "U";
    }

    /**
     * Load excel form, and convert test cell data to probe code in hex
     *
     * @param driver
     * @param probe
     * @return boolean unknown unknown state
     */
    public boolean setLoadExcelCodeToMapperData(int driver, int probe, String code) throws ScanAndTestDataException {
        boolean status = false;
        if (scanPatternData == null) {
            throw new ScanAndTestDataException("Error Scan not init!");
        }
        if (code == null) {
            throw new ScanAndTestDataException("Error Scan data!");
        }
        if (driver < 0 || probe < 0 || driver > psize || probe > psize) {
            throw new ScanAndTestDataException("Error out of range!");
        }
        int dc = 0xff;
        switch (code.trim().toUpperCase().charAt(0)) {  // remove whitespace and get first character
            case 'U':   // unknown
                status = true;
                dc = 0xff;
                break;
            case 'P':   // Powered
                dc = 0x10;
                break;
            case 'G':   // Grounded
                dc = 0x08;
                break;
            case 'E':
                if (driver == probe) {
                    dc = 0x40;
                } else {
                    dc = 0x80;
                }
                break;
            case 'L':   // loopback
                dc = 0x41;
                break;
            case 'C':   // connected
                dc = 0x01;
                break;
            case 'N':   // not connected
                dc = 0x00;
                break;
            default:    // not any of these
                dc = 0xff;
                status = true;
                break;
        }
        setPatternData(driver, probe, (byte) dc);
        return status;
    }

    /**
     * test result error code note: result;expected;<tested> << this one @p
     *
     *
     * aram driver @param probe @return
     */
    public String getTestResultErrorCode(int driver, int probe) throws ScanAndTestDataException {
        if (scanPatternData == null || testData == null) {
            throw new ScanAndTestDataException("Error Scan or test data not init!");
        }
        if (driver < 0 || probe < 0 || driver > psize || probe > psize) {
            throw new ScanAndTestDataException("Error out of range!");
        }
        int dtest = getTestData(driver, probe) & 0xff;
        int dpattern = getPatternData(driver, probe) & 0xff;
        if ((dtest & 0x40) != 0x40) {
            return "E";    // has error, RED 
        } else if (dtest == 0xff) {
            return "U";    // unknown
        } else if (dtest == 0xCA) {
            // return "G";    // has error, gnd test error, RED, 
            return "";
        } else if (dtest == 0xC3) {
            // return "P";   // has error, power test error, RED 
            return "";
        } else if (dtest == 0xD0) {
            return "L";   // has error, loopback test erro, RED 
        } else if ((dtest & 0x80) == 0x80) {
            if (dpattern == 0x01) {
                return "N";  // connection test has error, RED 
            } else if (dpattern == 0x00) {
                return "C";  // disconnection test has error, RED
            }
        } else if ((dtest & 0x90) == 0x10) {
            return "";  // loopback test passed, white
        } else if ((dtest & 0x80) == 0) {
            return "";    // connection test passed, GREEN
        }
        return "";     // unknown not test, Black
    }

    /**
     * excel test report code note <test result>; expected; tested ,
     *
     * @param driver
     * @param probe
     * @return
     */
    public String getResultPassFailCode(int driver, int probe) throws ScanAndTestDataException {
        if (testData == null) {
            throw new ScanAndTestDataException("Error test data not init!");
        }
        if (driver < 0 || probe < 0 || driver > psize || probe > psize) {
            throw new ScanAndTestDataException("Error out of range!");
        }
        int dt = getTestData(driver, probe) & 0xff;
        if ((dt & 0x40) != 0x40) {                // not tested
            return "E";
        } else if (dt == 0xff) {
            return "U";  // unknown nothing
        } else if (dt == 0xCA) {                   // gnd test fail
            return "F";
        } else if (dt == 0xC3) {                   // power test fail
            return "F";
        } else if (dt == 0xd0) {                   // loopback test fail
            return "E";
        } else if ((dt & 0x80) > 0) {                   // connection test fail
            return "F";
        } else if (dt == 0x40 || dt == 0x50) {               // test passed
            return "P";
        }
        return "U";
    }

    /**
     * get scan data checksum
     *
     * @return
     * @throws ScanAndTestDataException
     */
    public String getScanDataCheckSum() throws ScanAndTestDataException {
        if (scanPatternData == null) {
            throw new ScanAndTestDataException("Error Scan not init!");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            int nread = 0;
            for (int driver = 0; driver < psize; driver++) {
                for (int probe = 0; probe < psize; probe++) {
                    byte dt = (byte) getPatternData(driver, probe);
                    md.update(dt);
                }
            }
            byte[] mdbytes = md.digest();
            //convert the byte to hex 
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < mdbytes.length; i++) {
                String hex = Integer.toHexString(0xff & mdbytes[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();  // return the hex 
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ScanAndTestDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ScanAndTestDataException ex) {
            Logger.getLogger(ScanAndTestDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     *
     * @return @throws ScanAndTestDataException
     */
    public String getTestDataCheckSum() throws ScanAndTestDataException {
        if (scanPatternData == null) {
            throw new ScanAndTestDataException("Error Scan not init!");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            int nread = 0;
            for (int driver = 0; driver < psize; driver++) {
                for (int probe = 0; probe < psize; probe++) {
                    byte dt = (byte) getTestData(driver, probe);
                    md.update(dt);
                }
            }
            byte[] mdbytes = md.digest();
            //convert the byte to hex 
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < mdbytes.length; i++) {
                String hex = Integer.toHexString(0xff & mdbytes[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();  // return the hex 
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ScanAndTestDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ScanAndTestDataException ex) {
            Logger.getLogger(ScanAndTestDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
