package UIMain;

/**
 *
 * @author de
 */
public class ScanAndTestData implements java.io.Serializable {

    private static final long serialVersionUID = -1873278894750864750L;

    protected byte[][] scanPatternData = null;   // pattern data
    protected byte[][] testData = null;   // test data
    private int psize;
    private boolean debug = false;

    public ScanAndTestData(int size) throws ScanAndTestDataException {
        psize = size;
        if (size < 48 || size > (48 * 32) || (size %48) !=0 ) {  // size is multiple of 8
            //  System.out.println("nukk pattern data error ");
            throw new ScanAndTestDataException("Size of data error");
        }        
        debug = false;
        scanPatternData = new byte[size][size];
        testData = new byte[size][size];

        if (scanPatternData == null) {
            //  System.out.println("nukk pattern data error ");
            throw new ScanAndTestDataException("null pattern data");
        }

        if (scanPatternData.length != scanPatternData[0].length) {
            //  System.out.println("non square data error");
            throw new ScanAndTestDataException("non square pattern data");
        }
        if (testData == null) {
            // System.out.println("nukk test data error ");
            throw new ScanAndTestDataException("null test data");
        }

        if (testData.length != testData[0].length) {
            //  System.out.println("non square data error");
            throw new ScanAndTestDataException("non square test data");
        }
        resetPatternData();
    }

    /*  pattern data -------------------------*/
    /**
     * return pattern data array
     *
     * @return
     */
    public byte[][] getPatternData() {
        return scanPatternData;
    }

    /**
     * get pattern data size
     *
     * @return
     */
    public int getPatternSize() {
        return scanPatternData.length;
    }

    /**
     * get pattern probe data
     *
     * @param driver
     * @param probe
     * @return
     */
    public byte getPatternData(int driver, int probe) throws ScanAndTestDataException {
        if (scanPatternData == null
                || driver >= psize || probe >= psize
                || driver < 0 || probe < 0) {
            throw new ScanAndTestDataException(" Read scan pattern data Error !");
        }        
        return scanPatternData[driver][probe];
    }

    public void setPatternData(int driver, int probe, byte data) throws ScanAndTestDataException{
        if (scanPatternData == null
                || driver >= psize || probe >= psize
                || driver < 0 || probe < 0) {
            throw new ScanAndTestDataException(" Write Test data Error !");
        }        
        scanPatternData[driver][probe] = data;
    }

    /**
     * clear pattern data to 0x00
     */
    public void clearPatternData() throws ScanAndTestDataException {
        if (scanPatternData == null ) {
            throw new ScanAndTestDataException(" Clear scan pattern data Error !");
        }            
        for (int i = 0; i < psize; i++) {
            for (int j = 0; j < psize; j++) {
                scanPatternData[i][j] = (byte) 0x00;
            }
        }
    }

    /**
     * reset pattern data to default state
     */
    public void resetPatternData() throws ScanAndTestDataException {
        if (scanPatternData == null) {
            throw new ScanAndTestDataException(" Reset scan pattern data Error !");
        }            
        for (int i = 0; i < psize; i++) {
            for (int j = 0; j < psize; j++) {
                scanPatternData[i][j] = (byte) 0xff;
            }
        }
    }

    /*    Test data ---------------------------*/
    /**
     * return test data array
     *
     * @return
     */
    public byte[][] getTestData() {
        return testData;
    }

    /**
     * get test data size
     *
     * @return
     */
    public int getTestSize() {
        return testData.length;
    }

    /**
     * get test probe data
     *
     * @param driver
     * @param probe
     * @return
     */
    public byte getTestData(int driver, int probe) throws ScanAndTestDataException {
        if (testData == null
                || driver >= psize || probe >= psize
                || driver < 0 || probe < 0) {
            throw new ScanAndTestDataException(" Read Test data Error !");
        }
        return testData[driver][probe];
    }

    public void setTestData(int driver, int probe, byte data) throws ScanAndTestDataException {
        if (testData == null
                || driver >= psize || probe >= psize
                || driver < 0 || probe < 0) {
            throw new ScanAndTestDataException();
        }
        testData[driver][probe] = data;
    }

    /**
     * clear test data to 0x00
     */
    public void clearTestData() throws ScanAndTestDataException {
        if (testData == null) {
            throw new ScanAndTestDataException(" Clear Test data Error !");
        }
        for (int i = 0; i < psize; i++) {
            for (int j = 0; j < psize; j++) {
                testData[i][j] = (byte) 0x00;
            }
        }
    }

    /**
     * reset test data to default state
     */
    public void resetTestData() throws ScanAndTestDataException{
        if (testData == null) {
            throw new ScanAndTestDataException("Reset Test data error !");
        }
        for (int i = 0; i < psize; i++) {
            for (int j = 0; j < psize; j++) {
                testData[i][j] = (byte) 0xff;
            }
        }
    }

    /*-----debug ----------------------------*/
    /**
     * @return the debug
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * @param debug the debug to set
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}

/*
 scan map return a table with number of #probe x #probe bytes crossing map bits of each byte
 0 = connection, 0 = no connection, 1 = connected
 1 = grounded identification, 0 = not grounded, 1 = grounded
 2 = leaked detected, 0 = not leaked, 1 = leaked voltage detected
 3 = GND power, 1 = GND, 0 = other, no auto scan user manual define
 4 = POWER, 1 = POWER, 0 = other, no auto scan user manual define
 5 = Not used
 6 = probe self identification, 0 = connected to other, 1 = connected to self
 7 = error in scan, 1 = error, 0 = no error
 -------------------------------------------------------------------------------
 test all probes with map table.
 return map result data.
 Map per probe bits format, all phase map test probe 0...N, bits of each byte
 0 = connection, 0 = no connection to probeN or VCC or GND, 1 = connected
 1 = ground short identification, 0 = not grounded, 1 = grounded detected
 2 = leaked detected, 0 = not leaked, 1 = leaked voltage detected
 3 = GND power, 1 = GND, 0 = other, no auto scan user manual define
 4 = POWER, 1 = POWER, 0 = other, no auto scan user manual define
 5 = Not used
 6 = probe self identification, 0 = other, 1 = connected to self
 7 = error in scan, 1 = error, 0 = no error 
 ******
 output result bits format, rdata report test result
 0. probe pull down test, driver disable, 1 = error, 0 = pass 
 1. probe GND or POWER test, 1 = error, 0 = pass
 2. driver N enable, pull down match with map, 1 = not match error, 0 = match
 3. probe pull up test, driver disable, 1 = error, 0 = pass
 4. probe is a, 1 = loopback, 0 = other
 5. driver N enable, pull up match with inverted map, 1 = not match, 0 = match
 6. 0 = not test, 1 = tested
 7. status 1 = has error, 0 = no error
 */
