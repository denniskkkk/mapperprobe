package UIMain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 *
 * @author de
 */
public class ReadPatternForm implements ConstantsInterface {

    private String patternFileName = "pattern.xls";
    private static int size = 0;
    private HSSFSheet[] sheetlist = null;
    private String[] sheetName = null;
    private ReadPatternFormData[] patternFormData = null;
    private Date createDateTime = null;
    private ScanAndTestDataProcessor patternData = null; // where probe-driver data store

    private int lastRow = 0;

    /**
     * read test pattern file
     *
     * @param total number of probe
     */
    private ReadPatternForm() {

    }

    private static class ReadPatternFormHolder {

        public static final ReadPatternForm INSTANCE = new ReadPatternForm();
    }

    /**
     * get instance
     *
     * @param psize
     * @return
     */
    public static ReadPatternForm getInstance(int psize) {
        size = psize;
        return ReadPatternFormHolder.INSTANCE;
    }

    /**
     * patter data set
     *
     * @param pdata
     */
    public void setPatternDdata(ScanAndTestDataProcessor pdata) {
        patternData = pdata;
    }

    /**
     * get pattern data
     *
     * @return
     */
    public ScanAndTestData getPatternData() {
        return patternData;
    }

    /**
     * convert string array to hex look data
     *
     * @param da
     * @return
     */
    private String toHexDump(String[] da) {
        StringBuilder tmp = new StringBuilder();
        for (String p : da) {
            if (p != null) {
                tmp.append(p + ":");
            } else {
                tmp.append("-:");
            }
        }
        return tmp.toString();
    }

    /**
     * read test pattern and parse into data array
     *
     * @param path
     * @throws ReadPatternFormException
     */
    public void readPatternForm(String file) throws ReadPatternFormException, FileNotFoundException, IOException, ScanAndTestDataException {
        if (file == null) {
            throw new ReadPatternFormException("Filename not define error !");
        }        
        FileInputStream fis = null;
        File excel = new File(file);
        if (excel == null || !excel.isFile()) {
            throw new ReadPatternFormException("Pattern name error !");
        }
        fis = new FileInputStream(excel);
        HSSFWorkbook wb = new HSSFWorkbook(fis);
        int sheetN = wb.getNumberOfSheets();
        if (sheetN < 2) {
            throw new ReadPatternFormException("WorkSheet not found  !");
        }
        setSheetlist(new HSSFSheet[sheetN]);
        setSheetName(new String[sheetN]);
        for (int i = 0; i < sheetN; i++) {
            getSheetlist()[i] = wb.getSheetAt(i);
            getSheetName()[i] = getSheetlist()[i].getSheetName();
            if (getSheetName()[i] == null || getSheetName()[i].length() < 1) {
                throw new ReadPatternFormException("Pattern name error !");
            }
        }
        HSSFSheet configsheet = getSheetlist()[0];
        lastRow = configsheet.getLastRowNum() + 1;
        //System.out.println("row size = " + lastRow);
        if (lastRow != size + 5) {
            throw new ReadPatternFormException("Form number of row mismatch!");
        }
        patternFormData = new ReadPatternFormData[getLastRow() - getTableoffs()];
        for (int r = getTableoffs(); r < getLastRow(); r++) {
            int nprobe = r - getTableoffs();
            HSSFRow lrow = null;
            getFormPatternData()[nprobe] = new ReadPatternFormData();
            if (configsheet != null) {
                lrow = configsheet.getRow(r);
            }
            if (lrow == null) {
                throw new ReadPatternFormException("Form read row error!!!");
            }
            if (lrow != null) {
                HSSFCell lcell0 = lrow.getCell(0);
                HSSFCell lcell1 = lrow.getCell(1);
                HSSFCell lcell2 = lrow.getCell(2);
                HSSFCell lcell3 = lrow.getCell(3);
                HSSFCell lcell4 = lrow.getCell(4);
                // System.out.println("row = " + nprobe);
                if (lcell0 != null) {
                    String pn = lcell0.getStringCellValue();
                    if (pn != null && pn.length() < 1) {
                        throw new ReadPatternFormException("Form Port number cell error");
                    }
                    getFormPatternData()[nprobe].setProbeNumber(pn);
                } else {
                    throw new ReadPatternFormException("Form Cell data error");
                }
                if (lcell1 != null) {
                    String pc = lcell1.getStringCellValue();
                    if (pc != null && pc.length() < 1) {
                        throw new ReadPatternFormException("Probe card cell error");
                    }
                    getFormPatternData()[nprobe].setProbeCard(pc);
                } else {
                    throw new ReadPatternFormException("Form Cell data error");
                }
                if (lcell2 != null) {
                    String ppn = lcell2.getStringCellValue();
                    if (ppn != null && ppn.length() < 1) {
                        throw new ReadPatternFormException("Probe port number cell error");
                    }
                    getFormPatternData()[nprobe].setProbePortNumber(ppn);
                } else {
                    throw new ReadPatternFormException("Form Cell data error");
                }
                if (lcell3 != null) {
                    String uuc = lcell3.getStringCellValue();
                    if (uuc != null && uuc.length() < 1) {
                        throw new ReadPatternFormException("UUTcard cell error");
                    }
                    getFormPatternData()[nprobe].setUutCard(uuc);
                } else {
                    throw new ReadPatternFormException("Form Cell data error");
                }
                if (lcell4 != null) {
                    String ssn = lcell4.getStringCellValue();
                    if (ssn != null && ssn.length() < 1) {
                        throw new ReadPatternFormException("UUT signal name cell error");
                    }
                    getFormPatternData()[nprobe].setUutSignalName(ssn);
                } else {
                    throw new ReadPatternFormException("Form Cell data error");
                }
            } else {
                throw new ReadPatternFormException("Form cell error!!!");
            }
        }
        // parse sheet by sheet except first
        int snumber = 0;
        int tmplastrow = 0;
        for (HSSFSheet hsheet : sheetlist) {
            if (snumber == 0) {
                System.out.println("config sheet skip ****");
                snumber++;
                continue;
            }
            int lastrow = hsheet.getLastRowNum() + 1;
            if (lastrow < size) {
                throw new ReadPatternFormException("Pattern form size error!!!");
            }
            if (tmplastrow == 0) {
                tmplastrow = lastrow;
            }
            if (tmplastrow != lastrow) {
                throw new ReadPatternFormException("Pattern sheet unequal row size!!!");
            }
            tmplastrow = lastrow;
            for (int q = getTableoffs(); q < lastrow; q++) {
                HSSFRow prow = hsheet.getRow(q);
                int nprobe = q - getTableoffs();
                if (prow == null) {
                    throw new ReadPatternFormException("Pattern file cell error:");
                }
                int lastcol = prow.getLastCellNum();
                if (lastcol != (totalCol + tableColOffset)) {
                    throw new ReadPatternFormException("Pattern file colunm size error:");
                }
                // 
                int totalprobe = lastcol - tableColOffset;
                for (int z = tableRowOffset; z < lastcol; z++) {
                    int ndriver = z - tableColOffset + totalCol * (snumber - 1);
                    HSSFCell pcell = prow.getCell(z);
                    if (pcell == null) {
                        throw new ReadPatternFormException("Pattern file cell error:");
                    }
                    String pn = pcell.getStringCellValue();
                    //System.out.println ("pattern data  size = " + pn.length());
                    if (pn == null || pn.length() < 1) {
                        throw new ReadPatternFormException("Pattern data error:");
                    }
                    //System.out.println ("sheet = " + snumber + ", driver = " + ndriver + ", probe = " + nprobe + ", data = " + cdata); 
                    //patternData.setData(ndriver , nprobe, (byte) 0x01);
                    boolean state = patternData.setLoadExcelCodeToMapperData(ndriver, nprobe, pn.toUpperCase().trim());
                    if (state ) {
                        throw new  ReadPatternFormException("Pattern data error:");
                    }
                }
            }
            snumber++;
        }
        fis.close();
    }

    /**
     * @return the patternFileName
     */
    public String getPatternFileName() {
        return patternFileName;
    }

    /**
     * @param patternFileName the patternFileName to set
     */
    public void setPatternFileName(String patternFileName) {
        this.patternFileName = patternFileName;
    }

    /**
     * @return the sheetlist
     */
    public HSSFSheet[] getSheetlist() {
        return sheetlist;
    }

    /**
     * @param sheetlist the sheetlist to set
     */
    public void setSheetlist(HSSFSheet[] sheetlist) {
        this.sheetlist = sheetlist;
    }

    /**
     * @return the sheetName
     */
    public String[] getSheetName() {
        return sheetName;
    }

    /**
     * @param sheetName the sheetName to set
     */
    public void setSheetName(String[] sheetName) {
        this.sheetName = sheetName;
    }

    /**
     * @return the labelData
     */
    public ReadPatternFormData[] getFormPatternData() {
        return patternFormData;
    }

    /**
     * @param labelData the labelData to set
     */
    public void setFormPatternData(ReadPatternFormData[] patternFormata) {
        this.patternFormData = patternFormData;
    }

    /**
     * @return the tableoffs
     */
    private int getTableoffs() {
        return tableRowOffset;
    }

    /**
     * @return the lastRow
     */
    public int getLastRow() {
        return lastRow;
    }

    /**
     * @param lastRow the lastRow to set
     */
    public void setLastRow(int lastRow) {
        this.lastRow = lastRow;
    }

}
