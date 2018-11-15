package UIMain;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 *
 * @author de
 */
public class ReportGen extends FormUtility implements ConstantsInterface {

    private int nsize;   // each pod probe number
    private int probeTotal;  // total number of probes
    private int numberCard;  // total number of cards
    private String tag3 = "-";
    private HSSFSheet[] hsheet = null;
    private HSSFCellStyle lockCellStyle = null;
    private HSSFCellStyle cellStyle = null;
    private HSSFCellStyle cellStyle2 = null;
    private HSSFCellStyle cellStyle3 = null;
    private HSSFCellStyle cellStyle5 = null;
    private HSSFCellStyle cellStyle4 = null;
    private HSSFCellStyle numberStyle = null;
    private HSSFCellStyle VstringStyle = null;
    private HSSFCellStyle stringStyle = null;
    private HSSFCellStyle stringStyle_fail = null;
    private HSSFCellStyle stringStyle1 = null;
    private HSSFCellStyle stringStyle2 = null;
    private HSSFCellStyle stringStyle3 = null;
    private HSSFCellStyle hiddenString = null;
    private int rp;
    private ReadLabelFormData[] labelData = null;
    private String[] cardName = null;
    private int lap = 0;  // number of test lap
    private String stringdatetime = null;


    public ReportGen() {

    }

    /**
     *
     * @param filename
     * @param mdata ,, scan pattern data
     * @param data test data
     * @throws ReportGenException
     */
    public void genReport(String filename, ScanAndTestDataProcessor data) throws ReportGenException, ScanAndTestDataException {
        int rowcount = 0;
        int colcount = 0;
        nsize = 24;   // each pod probe number
        probeTotal = data.getPatternSize();  // total number of probes

        numberCard = probeTotal / nsize;  // total number of cards
        hsheet = new HSSFSheet[numberCard + 1];
        cardName = new String[numberCard + 1];
        cardName[0] = "Configure";
        int ct = 1;

        for (int c = 1; c < numberCard + 1; c++) {
            String tag = "";
            if (c == 7) {
                tag = "A";
            }
            if (c == 8) {
                tag = "B";
            }
            cardName[c] = "card-" + ct + tag;
            if (c != 7) {
                ct++;
            }
        }
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet worksheet = workbook.createSheet(cardName[0]);
        worksheet.setHorizontallyCenter(true);
        worksheet.setVerticallyCenter(true);
        hsheet[0] = worksheet;

        // cell comment helper
        CreationHelper factory = workbook.getCreationHelper();
        Drawing drawing = worksheet.createDrawingPatriarch();

        // date formatter
        DataFormat format = workbook.createDataFormat();

        DataFormatter objDefaultFormat = new DataFormatter();
        FormulaEvaluator objFormulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);

        // worksheet 1--------------
        worksheet.addMergedRegion(CellRangeAddress.valueOf("A1:E1"));
        worksheet.addMergedRegion(CellRangeAddress.valueOf("G1:I1"));
        worksheet.addMergedRegion(CellRangeAddress.valueOf("J1:L1"));

        worksheet.addMergedRegion(CellRangeAddress.valueOf("A2:D2"));
        worksheet.addMergedRegion(CellRangeAddress.valueOf("E2:G2"));
        worksheet.addMergedRegion(CellRangeAddress.valueOf("H2:J2"));

        //  worksheet.addMergedRegion(CellRangeAddress.valueOf("A3:C3"));
        //  worksheet.addMergedRegion(CellRangeAddress.valueOf("D3:E3"));
        worksheet.addMergedRegion(CellRangeAddress.valueOf("F3:AD3"));

        worksheet.addMergedRegion(CellRangeAddress.valueOf("A4:C4"));
        worksheet.addMergedRegion(CellRangeAddress.valueOf("D4:E4"));
        //  worksheet.addMergedRegion(CellRangeAddress.valueOf("F4:Z4"));
        // worksheet.createSplitPane(2000, 2000, 4, 1, HSSFSheet.PANE_LOWER_RIGHT );

        worksheet.createFreezePane(0, 5, 0, 5);
        worksheet.createFreezePane(5, 5, 5, 5);

        CreationHelper createHelper = workbook.getCreationHelper();
        worksheet.setDefaultRowHeight((short) 300);

        int wcount = 0;
        worksheet.setColumnWidth(wcount++, 2800);// 
        worksheet.setColumnWidth(wcount++, 4000);// 
        worksheet.setColumnWidth(wcount++, 4200);// 
        worksheet.setColumnWidth(wcount++, 5000);// 
        worksheet.setColumnWidth(wcount++, 5000);// 
        for (int z = 0; z < nsize + tableColOffset + 20; z++) {
            worksheet.setColumnWidth(wcount++, 1800);// 
        }

        worksheet.setDefaultColumnWidth(14);
        worksheet.setHorizontallyCenter(true);

        HSSFPalette palette = workbook.getCustomPalette();
        HSSFColor hssfColor = null;

        lockCellStyle = workbook.createCellStyle();
        lockCellStyle.setLocked(true);

        cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.RED.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setLocked(false);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);

        cellStyle2 = workbook.createCellStyle();
        cellStyle2.setFillForegroundColor(HSSFColor.GREEN.index);
        cellStyle2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle2.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle2.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle2.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle2.setTopBorderColor(HSSFColor.BLACK.index);

        cellStyle3 = workbook.createCellStyle();
        cellStyle3.setDataFormat(createHelper.createDataFormat().getFormat("hh:mm:ss"));
        cellStyle3.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle3.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle3.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle3.setTopBorderColor(HSSFColor.BLACK.index);

        cellStyle5 = workbook.createCellStyle();
        cellStyle5.setDataFormat(createHelper.createDataFormat().getFormat("d/m/yyyy"));
        cellStyle5.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle5.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle5.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle5.setTopBorderColor(HSSFColor.BLACK.index);

        cellStyle4 = workbook.createCellStyle();
        cellStyle4.setFillForegroundColor(HSSFColor.ORANGE.index);
        cellStyle4.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle4.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle4.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle4.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle4.setTopBorderColor(HSSFColor.BLACK.index);

        numberStyle = workbook.createCellStyle();
        numberStyle.setDataFormat(format.getFormat("0"));
        numberStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        numberStyle.setRightBorderColor(HSSFColor.BLACK.index);
        numberStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        numberStyle.setTopBorderColor(HSSFColor.BLACK.index);

        VstringStyle = workbook.createCellStyle();
        VstringStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        VstringStyle.setShrinkToFit(true);
        VstringStyle.setRotation((short) -90);
        VstringStyle.setDataFormat(HSSFCellStyle.VERTICAL_TOP);
        VstringStyle.setBorderBottom(HSSFCellStyle.BORDER_HAIR);
        VstringStyle.setBorderTop(HSSFCellStyle.BORDER_HAIR);
        VstringStyle.setBorderRight(HSSFCellStyle.BORDER_HAIR);
        VstringStyle.setBorderLeft(HSSFCellStyle.BORDER_HAIR);
        VstringStyle.setBorderBottom(HSSFCellStyle.BORDER_HAIR);
        VstringStyle.setBorderTop(HSSFCellStyle.BORDER_HAIR);
        VstringStyle.setBorderRight(HSSFCellStyle.BORDER_HAIR);
        VstringStyle.setBorderLeft(HSSFCellStyle.BORDER_HAIR);
        VstringStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        VstringStyle.setRightBorderColor(HSSFColor.BLACK.index);
        VstringStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        VstringStyle.setTopBorderColor(HSSFColor.BLACK.index);

        stringStyle = workbook.createCellStyle();
        stringStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        stringStyle.setDataFormat(HSSFCellStyle.VERTICAL_CENTER);
        stringStyle.setBorderBottom(HSSFCellStyle.BORDER_HAIR);
        stringStyle.setBorderTop(HSSFCellStyle.BORDER_HAIR);
        stringStyle.setBorderRight(HSSFCellStyle.BORDER_HAIR);
        stringStyle.setBorderLeft(HSSFCellStyle.BORDER_HAIR);
        stringStyle.setBorderBottom(HSSFCellStyle.BORDER_HAIR);
        stringStyle.setBorderTop(HSSFCellStyle.BORDER_HAIR);
        stringStyle.setBorderRight(HSSFCellStyle.BORDER_HAIR);
        stringStyle.setBorderLeft(HSSFCellStyle.BORDER_HAIR);
        stringStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        stringStyle.setRightBorderColor(HSSFColor.BLACK.index);
        stringStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        stringStyle.setTopBorderColor(HSSFColor.BLACK.index);
        stringStyle.setFillForegroundColor(HSSFColor.WHITE.index);
        stringStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        stringStyle_fail = workbook.createCellStyle();
        stringStyle_fail.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        stringStyle_fail.setDataFormat(HSSFCellStyle.VERTICAL_CENTER);
        stringStyle_fail.setBorderBottom(HSSFCellStyle.BORDER_HAIR);
        stringStyle_fail.setBorderTop(HSSFCellStyle.BORDER_HAIR);
        stringStyle_fail.setBorderRight(HSSFCellStyle.BORDER_HAIR);
        stringStyle_fail.setBorderLeft(HSSFCellStyle.BORDER_HAIR);
        stringStyle_fail.setBorderBottom(HSSFCellStyle.BORDER_HAIR);
        stringStyle_fail.setBorderTop(HSSFCellStyle.BORDER_HAIR);
        stringStyle_fail.setBorderRight(HSSFCellStyle.BORDER_HAIR);
        stringStyle_fail.setBorderLeft(HSSFCellStyle.BORDER_HAIR);
        stringStyle_fail.setLeftBorderColor(HSSFColor.BLACK.index);
        stringStyle_fail.setRightBorderColor(HSSFColor.BLACK.index);
        stringStyle_fail.setBottomBorderColor(HSSFColor.BLACK.index);
        stringStyle_fail.setTopBorderColor(HSSFColor.BLACK.index);
        stringStyle_fail.setFillForegroundColor(HSSFColor.RED.index);
        stringStyle_fail.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        stringStyle1 = workbook.createCellStyle();
        stringStyle1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        stringStyle1.setDataFormat(HSSFCellStyle.VERTICAL_CENTER);
        hssfColor = palette.getColor(HSSFColor.LIGHT_GREEN.index);
        stringStyle1.setFillForegroundColor(hssfColor.getIndex());
        stringStyle1.setFillPattern(CellStyle.SOLID_FOREGROUND);
        stringStyle1.setBorderBottom(HSSFCellStyle.BORDER_HAIR);
        stringStyle1.setBorderTop(HSSFCellStyle.BORDER_HAIR);
        stringStyle1.setBorderRight(HSSFCellStyle.BORDER_HAIR);
        stringStyle1.setBorderLeft(HSSFCellStyle.BORDER_HAIR);
        stringStyle1.setLeftBorderColor(HSSFColor.BLACK.index);
        stringStyle1.setRightBorderColor(HSSFColor.BLACK.index);
        stringStyle1.setBottomBorderColor(HSSFColor.BLACK.index);
        stringStyle1.setTopBorderColor(HSSFColor.BLACK.index);

        stringStyle2 = workbook.createCellStyle();
        stringStyle2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        stringStyle2.setDataFormat(HSSFCellStyle.VERTICAL_CENTER);
        hssfColor = palette.getColor(HSSFColor.LIGHT_ORANGE.index);
        stringStyle2.setFillForegroundColor(hssfColor.getIndex());
        stringStyle2.setFillPattern(CellStyle.SOLID_FOREGROUND);
        stringStyle2.setBorderBottom(HSSFCellStyle.BORDER_HAIR);
        stringStyle2.setBorderTop(HSSFCellStyle.BORDER_HAIR);
        stringStyle2.setBorderRight(HSSFCellStyle.BORDER_HAIR);
        stringStyle2.setBorderLeft(HSSFCellStyle.BORDER_HAIR);
        stringStyle2.setLeftBorderColor(HSSFColor.BLACK.index);
        stringStyle2.setRightBorderColor(HSSFColor.BLACK.index);
        stringStyle2.setBottomBorderColor(HSSFColor.BLACK.index);
        stringStyle2.setTopBorderColor(HSSFColor.BLACK.index);

        stringStyle3 = workbook.createCellStyle();
        stringStyle3.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        stringStyle3.setDataFormat(HSSFCellStyle.VERTICAL_CENTER);
        hssfColor = palette.getColor(HSSFColor.LIGHT_YELLOW.index);
        stringStyle3.setFillForegroundColor(hssfColor.getIndex());
        stringStyle3.setFillPattern(CellStyle.SOLID_FOREGROUND);
        stringStyle3.setBorderBottom(HSSFCellStyle.BORDER_HAIR);
        stringStyle3.setBorderTop(HSSFCellStyle.BORDER_HAIR);
        stringStyle3.setBorderRight(HSSFCellStyle.BORDER_HAIR);
        stringStyle3.setBorderLeft(HSSFCellStyle.BORDER_HAIR);
        stringStyle3.setLeftBorderColor(HSSFColor.BLACK.index);
        stringStyle3.setRightBorderColor(HSSFColor.BLACK.index);
        stringStyle3.setBottomBorderColor(HSSFColor.BLACK.index);
        stringStyle3.setTopBorderColor(HSSFColor.BLACK.index);

        hiddenString = workbook.createCellStyle();
        hiddenString.setLocked(true);
        hiddenString.setHidden(true);

        for (int c = 1; c <= numberCard; c++) {
            hsheet[c] = workbook.cloneSheet(0);
            workbook.setSheetName(c, cardName[c]);
        }

        CreateWorksheetConfig(worksheet, rowcount, data);

        // create card sheet
        rp = 0;
        for (int c = 1; c < hsheet.length; c++) {
            CreateWorksheetCard(hsheet[c], rowcount, data);
            rp = rp + nsize;
        }
        workbook.setActiveSheet(0);
        workbook.setForceFormulaRecalculation(true);
        //
        try {
            File actual = new File(filename + fileExtension);  // actual file name for output
            if (actual.exists()) {
                actual = new File(filename + fileExtension);
            }
            FileOutputStream fileOut = new FileOutputStream(actual);
            workbook.write(fileOut);
            fileOut.flush();
            fileOut.close();
            //System.out.println("file write out " + filename);
            fileOut = null;
            actual = null;
        } catch (Exception e) {
            throw new ReportGenException();
        }
    }

    public void CreateWorksheetConfig(HSSFSheet worksheet, int rowcount, ScanAndTestDataProcessor data) throws ScanAndTestDataException {
        HSSFRow row1 = null;
        HSSFCell cell = null;
        int colcount = 0;
        int lastCol = 0;   // last column location using top label

        // chapter-1
        row1 = worksheet.createRow(rowcount++);
        cell = row1.createCell(0);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellStyle(stringStyle);
        cell.setCellValue("Test Configuration Card");
        cell = row1.createCell(2);
        cell.setCellStyle(stringStyle);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("");

        // chapter-2, date time of report
        row1 = worksheet.createRow(rowcount++);
        cell = row1.createCell(0);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellStyle(stringStyle);
        cell.setCellValue("Created on date-time");
        cell = row1.createCell(4);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellStyle(stringStyle);
        if (stringdatetime == null) {
            cell.setCellValue(getCurrentDateTime());
        } else {
            cell.setCellValue(stringdatetime);
        }
        cell.setCellStyle(cellStyle5);
        // chapter-3  , pass fail status 
        row1 = worksheet.createRow(rowcount++);
        cell = row1.createCell(0);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellStyle(stringStyle);
        cell.setCellValue("RESULT");
        //
        if (lap > 0) {
            cell = row1.createCell(1);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellStyle(stringStyle);
            cell.setCellValue("LAP=" + lap);
        } else {
            cell = row1.createCell(1);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellStyle(stringStyle);
            cell.setCellValue("<ONE SHOT>");
        }
        //
        cell = row1.createCell(2);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        if ((data.getTestFail() + data.getTestError()) > 0) {
            cell.setCellStyle(stringStyle_fail);
            cell.setCellValue("<TEST FAIL>");
        } else {
            cell.setCellStyle(stringStyle);
            cell.setCellValue("<TEST PASS>");
        }

        // chapter-4
        row1 = worksheet.createRow(rowcount++);
        row1.setRowStyle(stringStyle);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell = row1.createCell(0);
        cell.setCellStyle(stringStyle);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("TestSet Information");
        cell = row1.createCell(3);
        cell.setCellStyle(stringStyle);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("UUT Information");

        // header
        colcount = 0;
        row1 = worksheet.createRow(rowcount++);

        cell = row1.createCell(colcount++);
        cell.setCellStyle(stringStyle);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("To Probe#");

        cell = row1.createCell(colcount++);
        cell.setCellStyle(stringStyle);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("ProbeCard");

        cell = row1.createCell(colcount++);
        cell.setCellStyle(stringStyle);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("ProbeCard Port#");

        cell = row1.createCell(colcount++);
        cell.setCellStyle(stringStyle);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("UUT Card");

        cell = row1.createCell(colcount++);
        cell.setCellStyle(stringStyle);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("UUT Signal Name");

        System.out.println("lap = " + lap);
        /**
         * each test label*
         */
        if (lap == 0) {
            cell = row1.createCell(colcount++);
            cell.setCellStyle(VstringStyle);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue("THIS<FAIL>");

            cell = row1.createCell(colcount++);
            cell.setCellStyle(VstringStyle);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue("THIS<ERROR>");
        } else {
            cell = row1.createCell(colcount++);
            cell.setCellStyle(VstringStyle);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue("LAP" + String.format("%04d", lap) + "<FAIL>");

            cell = row1.createCell(colcount++);
            cell.setCellStyle(VstringStyle);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue("LAP" + String.format("%04d", lap) + "<ERROR>");
        }

        /**
         * * lap counting label*
         */

        //
        lastCol = colcount;   // last column

        int wcount = 0;
        worksheet.setColumnWidth(wcount++, 2800);// 
        worksheet.setColumnWidth(wcount++, 4000);// 
        worksheet.setColumnWidth(wcount++, 4200);// 
        worksheet.setColumnWidth(wcount++, 5000);// 
        worksheet.setColumnWidth(wcount++, 5000);// 
        for (int z = 0; z < colcount; z++) {
            worksheet.setColumnWidth(wcount++, 1800);// 
        }

        /**
         * set row 3/4 height to
         */
        worksheet.getRow(3).setHeight((short) 1600);
        worksheet.getRow(4).setHeight((short) 1600);

        for (int r = 0; r < probeTotal; r++) {
            HSSFRow rowN = null;
            HSSFCell cellN = null;
            rowN = worksheet.createRow(rowcount++);
            if (labelData == null) {
                for (int z = 0; z < (nsize + 5); z++) {
                    if (z == 0) {
                        cellN = rowN.createCell(z, HSSFCell.CELL_TYPE_STRING);
                        cellN.setCellStyle(stringStyle);
                        cellN.setCellValue("Probe#" + (r + 1));
                    }
                    if (z == 1) {
                        cellN = rowN.createCell(z, HSSFCell.CELL_TYPE_STRING);
                        cellN.setCellStyle(stringStyle);
                        cellN.setCellValue("ProbeCard#" + ((r / (nsize * 2)) + 1));
                    }
                    if (z == 2) {
                        cellN = rowN.createCell(z, HSSFCell.CELL_TYPE_STRING);
                        cellN.setCellStyle(stringStyle);
                        cellN.setCellValue("Port#" + ((r % (nsize * 2)) + 1));
                    }
                    if (z == 3) {
                        cellN = rowN.createCell(z, HSSFCell.CELL_TYPE_STRING);
                        cellN.setCellStyle(stringStyle);
                        cellN.setCellValue(cardName[((r / nsize) + 1)]);
                    }
                    if (z == 4) {
                        cellN = rowN.createCell(z, HSSFCell.CELL_TYPE_STRING);
                        cellN.setCellStyle(stringStyle);
                        cellN.setCellValue("SigName" + (r + 1));
                    }
                    if (z == 5) {
                        cellN = rowN.createCell(z, HSSFCell.CELL_TYPE_STRING);
                        int nfail = data.getTestProbeTotalFail(r);
                        if (nfail > 0) {
                            cellN.setCellStyle(stringStyle_fail);
                        } else {
                            cellN.setCellStyle(stringStyle);
                        }
                        cellN.setCellValue(nfail);
                    }
                    if (z == 6) {
                        cellN = rowN.createCell(z, HSSFCell.CELL_TYPE_STRING);
                        int nerr = data.getTestProbeTotalError(r);
                        if (nerr > 0) {
                            cellN.setCellStyle(stringStyle_fail);
                        } else {
                            cellN.setCellStyle(stringStyle);
                        }
                        cellN.setCellValue(nerr);
                    }
                }
            } else {
                for (int z = 0; z < lastCol; z++) {
                    if (z == 0) {
                        cellN = rowN.createCell(z, HSSFCell.CELL_TYPE_STRING);
                        cellN.setCellStyle(stringStyle);
                        //cellN.setCellValue(labelData[r].getProbeNumber());
                        cellN.setCellValue("Probe#" + (r + 1));
                    }
                    if (z == 1) {
                        cellN = rowN.createCell(z, HSSFCell.CELL_TYPE_STRING);
                        cellN.setCellStyle(stringStyle);
                        cellN.setCellValue("ProbeCard#" + ((r / (nsize * 2)) + 1));
                        //cellN.setCellValue(labelData[r].getProbeCard());
                    }
                    if (z == 2) {
                        cellN = rowN.createCell(z, HSSFCell.CELL_TYPE_STRING);
                        cellN.setCellStyle(stringStyle);
                        cellN.setCellValue("Port#" + ((r % (nsize * 2)) + 1));
                        // cellN.setCellValue(labelData[r].getProbePortNumber());
                    }
                    if (z == 3) {
                        cellN = rowN.createCell(z, HSSFCell.CELL_TYPE_STRING);
                        cellN.setCellStyle(stringStyle);
                        cellN.setCellValue(cardName[((r / nsize) + 1)]);
                        cellN.setCellValue(labelData[r].getUutCard());
                    }
                    if (z == 4) {
                        cellN = rowN.createCell(z, HSSFCell.CELL_TYPE_STRING);
                        cellN.setCellStyle(stringStyle);
                        cellN.setCellValue("SigName" + (r + 1));
                        cellN.setCellValue(labelData[r].getUutSignalName());
                    }
                    // fail count
                    if (z == 5) {
                        cellN = rowN.createCell(z, HSSFCell.CELL_TYPE_STRING);
                        int nfail = data.getTestProbeTotalFail(r);
                        if (nfail > 0) {
                            cellN.setCellStyle(stringStyle_fail);
                        } else {
                            cellN.setCellStyle(stringStyle);
                        }
                        cellN.setCellValue(nfail);
                    }
                    // error count
                    if (z == 6) {
                        cellN = rowN.createCell(z, HSSFCell.CELL_TYPE_STRING);
                        int nerr = data.getTestProbeTotalError(r);
                        if (nerr > 0) {
                            cellN.setCellStyle(stringStyle_fail);
                        } else {
                            cellN.setCellStyle(stringStyle);
                        }
                        cellN.setCellValue(nerr);
                    }
                    // Lap fail count
                    if (z == 7) {
                        cellN = rowN.createCell(z, HSSFCell.CELL_TYPE_STRING);
                        int nfail = data.getTestProbeTotalFail(r);
                        if (nfail > 0) {
                            cellN.setCellStyle(stringStyle_fail);
                        } else {
                            cellN.setCellStyle(stringStyle);
                        }
                        cellN.setCellValue(nfail);
                    }
                    // Lap error count
                    if (z == 8) {
                        cellN = rowN.createCell(z, HSSFCell.CELL_TYPE_STRING);
                        int nerr = data.getTestProbeTotalError(r);
                        if (nerr > 0) {
                            cellN.setCellStyle(stringStyle_fail);
                        } else {
                            cellN.setCellStyle(stringStyle);
                        }
                        cellN.setCellValue(nerr);
                    }


                }
            }
        }
        formatWholeSheet(worksheet, stringStyle);
    }

    // create card worksheet
    public void CreateWorksheetCard(HSSFSheet worksheet, int rcount, ScanAndTestDataProcessor data) throws ScanAndTestDataException {
        HSSFRow row1 = null;
        HSSFCell cell = null;
        int rowcount = rcount;
        int colcount = 0;
        worksheet.setColumnHidden(1, true);
        worksheet.setColumnHidden(2, true);

        // chapter-1
        row1 = worksheet.createRow(rowcount++);
        cell = row1.createCell(0);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellStyle(stringStyle);
        cell.setCellValue("Test Card");
        cell = row1.createCell(2);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellStyle(stringStyle);
        cell.setCellValue("");

        // chapter-2
        row1 = worksheet.createRow(rowcount++);
        cell = row1.createCell(0);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellStyle(stringStyle);
        cell.setCellValue("Created on date-time");
        cell = row1.createCell(4);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellStyle(stringStyle);
        cell.setCellValue(getCurrentDateTime());
        cell.setCellStyle(cellStyle5);
        // chapter-3
        row1 = worksheet.createRow(rowcount++);
        cell = row1.createCell(0);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellStyle(stringStyle);
        cell.setCellValue("");
        cell = row1.createCell(2);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellStyle(stringStyle);
        cell.setCellValue("");
        cell = row1.createCell(5);
        cell.setCellStyle(stringStyle);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("Test Pattern, From Driver# (Result Entry: <Result>;<Expect>.  Key: 'P'=Pass, 'F'=Fail, 'E'=Error, 'N'=Not Connected, ';C'= Connected, ';L'=Loopback");

        // chapter-4
        row1 = worksheet.createRow(rowcount++);
        row1.setRowStyle(stringStyle);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell = row1.createCell(0);
        cell.setCellStyle(stringStyle);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("TestSet Information");
        cell = row1.createCell(3);
        cell.setCellStyle(stringStyle);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("UUT Information");
        colcount = 5;
        for (int z = 1; z <= nsize; z++) {
            cell = row1.createCell(colcount++);
            cell.setCellStyle(VstringStyle);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue("Driver #" + (z + rp));
        }
        // header
        colcount = 0;
        row1 = worksheet.createRow(rowcount++);

        cell = row1.createCell(colcount++);
        cell.setCellStyle(stringStyle);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("Probe");

        cell = row1.createCell(colcount++);
        cell.setCellStyle(stringStyle);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("Probe Card");

        cell = row1.createCell(colcount++);
        cell.setCellStyle(stringStyle);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("ProbeCard Port#");

        cell = row1.createCell(colcount++);
        cell.setCellStyle(stringStyle);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("UUT Card");

        cell = row1.createCell(colcount++);
        cell.setCellStyle(stringStyle);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("UUT Signal Name");

        for (int z = 1; z <= nsize; z++) {
            cell = row1.createCell(colcount++);
            cell.setCellStyle(VstringStyle);
            cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
            cell.setCellFormula("E" + (z + 5 + rp));
        }

        worksheet.getRow(3).setHeight((short) 1500);
        worksheet.getRow(4).setHeight((short) 1500);

        for (int r = 0; r < probeTotal; r++) {
            HSSFRow rowN = null;
            HSSFCell cellN = null;
            rowN = worksheet.createRow(rowcount++);
            for (int z = 0; z < (nsize + 5); z++) {
                if (z == 0) {
                    cellN = rowN.createCell(z, HSSFCell.CELL_TYPE_STRING);
                    cellN.setCellStyle(stringStyle);
                    cellN.setCellValue("Probe#" + (r + 1));
                }
                if (z == 1) {
                    cellN = rowN.createCell(z, HSSFCell.CELL_TYPE_FORMULA);
                    cellN.setCellStyle(stringStyle);
                    cellN.setCellFormula(cardName[0] + "!B" + (r + 6));
                }
                if (z == 2) {
                    cellN = rowN.createCell(z, HSSFCell.CELL_TYPE_FORMULA);
                    cellN.setCellStyle(stringStyle);
                    cellN.setCellFormula(cardName[0] + "!C" + (r + 6));
                }
                if (z == 3) {
                    cellN = rowN.createCell(z, HSSFCell.CELL_TYPE_FORMULA);
                    cellN.setCellStyle(stringStyle);
                    cellN.setCellFormula(cardName[0] + "!D" + (r + 6));
                }
                if (z == 4) {
                    cellN = rowN.createCell(z, HSSFCell.CELL_TYPE_FORMULA);
                    cellN.setCellStyle(stringStyle);
                    cellN.setCellFormula(cardName[0] + "!E" + (r + 6));
                }
                if (z > 4) {
                    cellN = rowN.createCell(z, HSSFCell.CELL_TYPE_STRING);
                    //byte dt =data[z - 5 + rp][r] ;
                    int probe = z - 5 + rp;
                    byte dtest = data.getTestData(probe, r);
                    byte dscan = data.getPatternData(probe, r);
                    //cellN.setCellValue("N" + ( z - 5 + rp ) + "," + r);
                    // getNodeColorcode(probe, r);
                    if (data.isDebug()) {
                        cellN.setCellStyle(stringStyle);
                        cellN.setCellValue(String.format("%02x", dscan & 0xff).toUpperCase() + ";" + String.format("%02x", dtest & 0xff).toUpperCase());
                    } else {
                        String rdata = data.getResultPassFailCode(probe, r);
                        if (rdata.contains("F") || rdata.contains("E") || rdata.contains("U")) {
                            String scanLabel = data.getScanSaveToExcelCode(probe, r);
                            String resultLabel = data.getTestResultErrorCode(probe, r);
                            cellN.setCellStyle(stringStyle_fail);
                            cellN.setCellValue(rdata + ";" + scanLabel /*+ ";" + resultLabel*/);   // output is Test Pass-Fail, scan Label, test error result label
                        } else {
                            cellN.setCellStyle(stringStyle);
                            cellN.setCellValue(rdata);
                        }
                    }
                    // cellN.setCellComment(comment);
                }
            }
        }
        //format whole work sheet
        formatWholeSheet(worksheet, stringStyle);

    }

    /**
     *
     * @param curdate
     */
    public String getCurrentDateTime() {
        Calendar cal = Calendar.getInstance();
        Date curdate = cal.getTime();
        SimpleDateFormat sf = new SimpleDateFormat(fileDateTimeFormatting);
        String cur = sf.format(curdate);
        return cur;
    }

    /*
     * set string date time , if not set use interna getCurrentDateTime () method
     */
    public void setCurrnetDateTimeString(String sdate) {
        stringdatetime = sdate;
    }

    /**
     * @return the tag1
     */
    public String getTag1() {
        return tag1;
    }

    /**
     * @return the tag2
     */
    public String getTag2() {
        return tag2;
    }

    /**
     * @return the tag3
     */
    public String getTag3() {
        return tag3;
    }

    /**
     * @param tag3 the tag3 to set
     */
    public void setTag3(String tag3) {
        this.tag3 = tag3;
    }

    /**
     * @return the patternData
     */
    public ReadLabelFormData[] getPatternData() {
        return labelData;
    }

    /**
     * @param patternData the patternData to set
     */
    public void setPatternData(ReadLabelFormData[] labelData) {
        this.labelData = labelData;
    }

    /*
     * set this test lap count
     */
    public void setLap(int l) {
        lap = l;
    }
}
