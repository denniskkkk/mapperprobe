package mapper;

import java.io.IOException;

/**
 *
 * @author de
 */
public class ProbeMapper implements Runnable, ProbeMapperConstants {

    public boolean kill = false;
    public int id;
    public int version;
    public String description;
    public int handle; // class handler
    private LoopBack levt = null; // loopback test event handler
    private ScanTestMap sevt = null; // scan test map event handler
    private MapTestProbes mevt = null; // map probes test event handler

    static {
        System.loadLibrary("probemapper");
    }

    private ProbeMapper() {

    }

    public static ProbeMapper getInstance() {
        return ProbeMapperFactory.instance;
    }

    private static class ProbeMapperFactory {

        private static ProbeMapper instance = new ProbeMapper();
    }

    public void init() {
        System.out.println("JavaCallBackINIT::");
    }

    public void exit() {
        System.out.println("JavaCallbackEXIT::");
    }

    public native int waitEvent();

    @Override
    public void run() {
        /*   while (true) {
         int et = waitEvent();
         if (kill) {
         break;
         } else {
         // dispatchEvent(et);
         }
         } */
    }

    /**
     * total number of probes from all cards
     *
     * @return
     */
    public native int getTotalProbes();

    /**
     * total number of cards
     *
     * @return
     */
    public native int getNumberOfCard();

    /**
     * each card contain number of port group
     *
     * @return
     */
    public native int getPortPerCard();

    /**
     * each card contain number of probe
     *
     * @return
     */
    public native int getProbePerCard();

    /**
     * usb device io driver version
     *
     * @return
     */
    public native int getLibraryVersion();

    /**
     * this mapper system id
     *
     * @return
     */
    public native String getSystemID();

    /**
     * get device list attach to this system
     *
     * @return
     * @throws IOException
     */
    public native String[] getDeviceList() throws IOException;

    /**
     * @param cards total number of card installed, with each card = 48 probe,.6
     * ports
     */
    public native void initProbes() throws IOException;

    /**
     * open all probe card
     *
     * @return status if any fail
     */
    public native int openProbes() throws IOException;

    /**
     * close all probe card
     *
     * @return status if any fail
     */
    public native int closeProbes() throws IOException;

    /**
     * return probe card 'n' description
     *
     * @param n
     * @return
     */
    public native String getProbeID(int n);

    /**
     * stop all loop test when invoke
     *
     */
    public native void stopAnyTestRun();

    /**
     * get any test running
     *
     * @return 0 = not run, > 0 test running
     */
    public native int getAnyTestRun();

    /**
     * for reading pattern, test probe and read result
     *
     * @param probeN probe to be enabled
     * @param delay, msec delay before take reading
     * @param rdata read back data
     * @param size rdata size, fix = 64
     * @return
     */
    public native int doTestOnProbe(int probeN, int delay, byte[] rdata, int size) throws IOException;

    /**
     * scan a test map from unknow uut and convert to a bit map rox x col size
     * map return a table with number of #probe x #probe bytes crossing map bits
     * of each byte 0 = connection, 0 = no connection, 1 = connected 1 =
     * grounded identification, 0 = not grounded, 1 = grounded 2 = Not used 3 =
     * Not used 4 = Not used 5 = Not used 6 = probe self identification, 0 =
     * connected to other, 1 = connected to self 7 = error in scan, 1 = error, 0
     * = no error
     *
     * @param delay, delaytime
     * @param rmapdata, result map data in row x col
     * @param rowsize, row size
     * @param colsize, col size
     * @return, total number of scan points
     * @throws IOException
     */
    public native int scanTestMapFromProbe(int delay, byte[][] rmapdata) throws IOException;

    /**
     * scan test event
    *  map data array format 
    *  bits of each byte
    * 0 = connection, 0 = no connection to probeN, 1 = connected
    * 1 = grounded identification, 0 = not grounded, 1 = grounded detected
    * 2 = leaked detected, 0 = not leaked, 1 = leaked voltage detected
    * 3 = GND power, 1 = GND, 0 = other, no auto scan user manual define
    * 4 = POWER, 1 = POWER, 0 = other, no auto scan user manual define
    * 5 = Not used
    * 6 = probe self identification, 0 = other, 1 = connected to self
    * 7 = error in scan, 1 = error, 0 = no error 
    * ------------------------------------
     * @param probeN
     * @param rdata
     * @param rowsize
     * @param colsize
     * @throws MapperException
     */
    public void scanTestMapEvent(int probeN, byte[] mapdata, int rowsize) throws MapperException {
        if (sevt != null) {           // check if a registered event handler otherwise exception
            sevt.eventHandler(probeN, mapdata, rowsize);    // execute the event impl handler method
        } else {
            stopAnyTestRun();        // stop test loop                     
            throw new MapperException("maptest event not define");
        }
    }

    /**
     * add scan eventhandler
     *
     * @param eventHandler, event object
     * @throws MapperException
     */
    public void addScanTestMapEventHandler(ScanTestMap eventHandler) throws MapperException {
        if (eventHandler == null) {
            throw new MapperException("null maptest handler");
        }
        if (sevt != null) {
            sevt = null;  // release old handler
        }
        sevt = eventHandler;   // assigned a event handler
    }

    /**
     * test map data with all test probe data 
    * map data array format 
    *  bits of each byte
    * 0 = connection, 0 = no connection to probeN or VCC or GND, 1 = connected
    * 1 = grounded identification, 0 = not grounded, 1 = grounded detected
    * 2 = leaked detected, 0 = not leaked, 1 = leaked voltage detected
    * 3 = GND power, 1 = GND, 0 = other, no auto scan user manual define
    * 4 = POWER, 1 = POWER, 0 = other, no auto scan user manual define
    * 5 = Not used
    * 6 = probe self identification, 0 = other, 1 = connected to self
    * 7 = error in scan, 1 = error, 0 = no error 
    * ------------------------------------
    * test data array result format
    * bit location, set 1 if fault
    * 0. probe pull down test, driver disable, 1 = error, 0 = pass 
    * 1. probe GND or POWER test, 1 = error, 0 = pass
    * 2. driver N enable, pull down match with map, 1 = not match, 0 = match
    * 3. probe pull up test, driver disable, 1 = error, 0 = pass
    * 4. probe is 1 = loopback, 0 = other
    * 5. driver N enable, pull up match with inverted map, 1 = not match, 0 = match
    * 6. 0 = not test, 1 = tested
    * 7. test status, 1 = error, 0 = no error
     * @param delay in msec
     * @param mapdata test map data
     * @param rdata result data after match with map
     * @return number total number of error(s)
     * @throws IOException
     */
    public native int doTestMapWithAllProbes(int delay, byte[][] mapdata,
            byte[][] rdata) throws IOException;

    /**
     * scan test event
     *
     * @param probeN probe number 'x'
     * @param rdata probe 0 to N status with map[x] test
     * @param rowsize size of rdata
     * @throws MapperException
     */
    public void doTestMapWithAllProbesEvent(int probeN, byte[] rdata, int rowsize) throws MapperException {
        if (mevt != null) {           // check if a registered event handler otherwise exception
            mevt.eventHandler(probeN, rdata, rowsize);    // execute the event impl handler method
        } else {
            stopAnyTestRun();        // stop test loop                     
            throw new MapperException("testmap event not define");
        }
    }

    /**
     * add scan eventhandler
     *
     * @param eventHandler, event object
     * @throws MapperException
     */
    public void addDoTestMapWithAllProbesEventHandler(MapTestProbes eventHandler) throws MapperException {
        if (eventHandler == null) {
            throw new MapperException("null maptest handler");
        }
        if (mevt != null) {
            mevt = null;  // release old handler
        }
        mevt = eventHandler;   // assigned a event handler
    }

    /**
     * test probe pull down and up if any stuck probe and read result, a 0
     * indicate pass and 1 indicate fail
     *
     * @param probe N
     * @param delay delay in msec
     * @param rdata pull down , pull up test result data array
     * @param size data array size
     * @return number of stuck probe(s), 0 = all pass
     */
    public native int doPullDownUpTest(byte[] rdata) throws IOException;

    /**
     * loopback test probe driver and receiver
     *
     * @param probeN probe number 0-383
     * @return status, 0= pass, -1 = fail/high -2 = fail/low, -3= both
     * fail/(high/low);
     */
    public native int doLoopBackTestOnProbeN(int probeN) throws IOException;

    /**
     * test from probeN 0 to 1023, status,1 = testing, 0 = pass, -1 = fail/high
     * -2 = fail/low, -3 = both fail/(high/low); event LoopBackTestEvent, return
     * number of error information invoke stopAnyTestRun () will stop this test
     *
     * @param tdata array of byte [1024] size fix 1024, result of test
     * @param size number of probes
     * @return number of failed probe
     * @throws IOException
     *
     */
    public native int doLoopBackTestOnAllProbe(byte[] tdata) throws IOException;

    /**
     * test register event
     */
    //public native int leventregister ();
    /**
     * Event initiate on every probeN to be test
     *
     * @param probeN probe number
     * @param status
     * @param result test result code
     * @throws mapper.MapperException
     */
    public void loopBackTestEvent(int probeN, byte[] status, int result) throws MapperException {
        if (levt != null) {           // check if a registered event handler otherwise exception
            levt.eventHandler(probeN, status, result);    // execute the event impl handler method
        } else {
            stopAnyTestRun();        // stop test loop                     
            throw new MapperException("event not define");
        }
    }

    /**
     * Event add a handler for this event
     */
    public void addLoopBackEventHandler(LoopBackEvent handler) throws MapperException {
        if (handler == null) {
            throw new MapperException("null loopbackeventhandler");
        }
        if (levt != null) {
            levt = null;  // release old handler
        }
        levt = handler;   // assigned a event handler
    }

    /**
     * @param probeN number
     * @param delay in msec
     * @param mapdata compare map data array
     * @param readdata result read data array, 0= pass 1= fail
     * @return number of errors probe
     */
    public native int doProbeNTestWithMap(int probeN, int delay, byte[] mapdata, byte[] readdata) throws IOException;

    /**
     * test UUT with probe 'p' on and get result from all 384 probes
     *
     * @param p, porbe number 0-383
     * @param delay, delay in msec, beofre take reading
     * @param retdata return result
     * @retrun status
     */
    public native int doTestUUT(int p, int delay, byte[] retdata) throws IOException;

    /**
     * disable all probe outputs
     */
    public native void disableAllProbes() throws IOException;

    /**
     * enable all probe outputs
     */
    public native void enableAllProbes() throws IOException;
    /*
     * read all probes logic
     * @return probe data
     */

    public native int readAllProbes(byte[] data) throws IOException;
    /*write all probes output
     * @param data, date to probes
     */

    public native void writeAllProbes(byte[] data) throws IOException;

    /**
     * set output probes logic level
     *
     * @param b , logic output for all probes
     */
    public native void setProbeOutputLogic(boolean b) throws IOException;

    /**
     * set 7 segment LED display on card numeric character
     *
     * @param num 0-9 a-f
     */
    public native void setLEDDisplay(int num) throws IOException;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    /**
     *
     */
}
