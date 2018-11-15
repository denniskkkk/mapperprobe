package UIMain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 *
 * @author de
 */
public class ReadLabelForm implements ConstantsInterface {

    private HSSFSheet[] sheetlist = null;
    private String[] sheetName = null;        // sheet name list
    private ReadLabelFormData[] labelData = null;     // label list
    private final int tableoffs = 5;   // form row offset 
    private static int size = 0;    // number of prove

    private int lastRow = 0;

    public static ReadLabelForm getInstance(int psize) {
        size = psize;
        return ReadLabelFormHolder.INSTANCE;
    }

    private static class ReadLabelFormHolder {

        public static final ReadLabelForm INSTANCE = new ReadLabelForm();

    }

    /**
     * read excel output file label setting file
     */
    private ReadLabelForm() {

    }

    /**
     * read label form "probenametable.xls
     *
     * @param path
     * @throws ReadLabelFormException
     */
    public void readForm(String path) throws ReadLabelFormException, FileNotFoundException, IOException {
        FileInputStream fis = null;
        File excel = new File(path + "/" + getLabelFileName());
        if (excel == null || !excel.isFile()) {
            throw new ReadLabelFormException("pattern name error !");
        }
        fis = new FileInputStream(excel);
        HSSFWorkbook wb = new HSSFWorkbook(fis);
        int sheetN = wb.getNumberOfSheets();
        if (sheetN < 1) {
            throw new ReadLabelFormException("Worksheet not found!!!");
        }
        sheetlist = new HSSFSheet[sheetN];
        sheetName = new String[sheetN];
        for (int i = 0; i < sheetN; i++) {
            getSheetlist()[i] = wb.getSheetAt(i);
            getSheetName()[i] = getSheetlist()[i].getSheetName();
            // System.out.println ("index = " + i  + ", sheetname = " +  getSheetlist()[i].getSheetName());
            if (getSheetName()[i] == null || getSheetName()[i].length() < 1) {
                throw new ReadLabelFormException("Sheetname error !");
            }
        }
        HSSFSheet configsheet = getSheetlist()[0];
        if (configsheet == null ) {
             throw new ReadLabelFormException("Configuration WorkSheet not found !");
        }
        lastRow = configsheet.getLastRowNum() + 1;
        //System.out.println("row size = " + lastRow);
        if (lastRow - tableoffs < size) {
            throw new ReadLabelFormException("Form number of row mismatch !");
        }
        setLabelData(new ReadLabelFormData[getLastRow() - tableoffs]);
        //System.out.println("lastrow = " + lastRow + ", " + labelData);
        for (int r = tableoffs; r < lastRow ; r++) {
            //System.out.println  ("reading row data = " + r);
            int nprobe = r - tableoffs;
            HSSFRow lrow = null;
            labelData[nprobe] = new ReadLabelFormData();
            if (configsheet != null) {
                lrow = configsheet.getRow(r);
            }
            if (lrow == null) {
                throw new ReadLabelFormException("Form read row error ! ");
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
                        throw new ReadLabelFormException("Port number cell error");
                    }
                    getLabelData()[nprobe].setProbeNumber(pn);
                } else {
                    throw new ReadLabelFormException("Form Cell data error");
                }
                if (lcell1 != null) {
                    String pc = lcell1.getStringCellValue();
                    if (pc != null && pc.length() < 1) {
                        throw new ReadLabelFormException("Probe card cell error");
                    }
                    getLabelData()[nprobe].setProbeCard(pc);
                } else {
                    throw new ReadLabelFormException("Form Cell data error");
                }
                if (lcell2 != null) {
                    String ppn = lcell2.getStringCellValue();
                    if (ppn != null && ppn.length() < 1) {
                        throw new ReadLabelFormException("Probe port number cell error");
                    }
                    getLabelData()[nprobe].setProbePortNumber(ppn);
                } else {
                    throw new ReadLabelFormException("Form Cell data error");
                }
                if (lcell3 != null) {
                    String uuc = lcell3.getStringCellValue();
                    if (uuc != null && uuc.length() < 1) {
                        throw new ReadLabelFormException("UUTcard cell error");
                    }
                    getLabelData()[nprobe].setUutCard(uuc);
                } else {
                    throw new ReadLabelFormException("Form Cell data error");
                }
                if (lcell4 != null) {
                    String ssn = lcell4.getStringCellValue();
                    if (ssn != null && ssn.length() < 1) {
                        throw new ReadLabelFormException("UUT signal name cell error");
                    }
                    getLabelData()[nprobe].setUutSignalName(ssn);
                } else {
                    throw new ReadLabelFormException("Form Cell data error");
                }
            } else {
                throw new ReadLabelFormException("table cell error!!!");
            }
        }
        fis.close();
    }

    /**
     * @return the labelFileName
     */
    public String getLabelFileName() {
        return labelFileName;
    }

    /**
     * @param labelFileName the labelFileName to set
     */
    public void setLabelFileName(String labelFileName) {
        labelFileName = ConstantsInterface.labelFileName;
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
    public ReadLabelFormData[] getLabelData() {
        return labelData;
    }

    /**
     * @param labelData the labelData to set
     */
    public void setLabelData(ReadLabelFormData[] labelData) {
        this.labelData = labelData;
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
