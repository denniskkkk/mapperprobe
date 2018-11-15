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
public class ScanPatternGen extends FormUtility implements ConstantsInterface {

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
    private HSSFCellStyle stringStyle_error = null;
    private HSSFCellStyle stringStyle1 = null;
    private HSSFCellStyle stringStyle2 = null;
    private HSSFCellStyle stringStyle3 = null;
    private HSSFCellStyle hiddenString = null;
    private int rp;
    private ReadLabelFormData[] labelData = null;
    private String[] cardName = null;
    private String stringdatetime = null;
    //= {"Configure", "card-1", "card-2", "card-3", "card-4A", "card-4B",
    //     "card-5", "card-6", "card-7", "card-8", "card-9",
    //     "card-10", "card-11", "card-12", "card-13"
    // };

    public ScanPatternGen() {

    }

    public void genTemplate(String filename, ScanAndTestDataProcessor data) throws TemplateGenException, ScanAndTestDataException {
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
        worksheet.addMergedRegion(CellRangeAddress.valueOf("F3:Z3"));

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
        for (int z = 0; z < nsize; z++) {
            worksheet.setColumnWidth(wcount++, 1300);// 
        }

        worksheet.setDefaultColumnWidth(14);
        worksheet.setHorizontallyCenter(true);

        HSSFPalette palette = workbook.getCustomPalette();
        HSSFColor hssfColor = null;

        lockCellStyle = workbook.createCellStyle();
        lockCellStyle.setLocked(true);
        lockCellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        lockCellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        lockCellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        lockCellStyle.setTopBorderColor(HSSFColor.BLACK.index);

        cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.RED.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
        cellStyle.setLocked(false);

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
        VstringStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        VstringStyle.setDataFormat(HSSFCellStyle.VERTICAL_CENTER);
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
        VstringStyle.setRotation((short) -90);

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

        stringStyle_error = workbook.createCellStyle();
        stringStyle_error.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        stringStyle_error.setDataFormat(HSSFCellStyle.VERTICAL_CENTER);
        stringStyle_error.setBorderBottom(HSSFCellStyle.BORDER_HAIR);
        stringStyle_error.setBorderTop(HSSFCellStyle.BORDER_HAIR);
        stringStyle_error.setBorderRight(HSSFCellStyle.BORDER_HAIR);
        stringStyle_error.setBorderLeft(HSSFCellStyle.BORDER_HAIR);
        stringStyle_error.setBorderBottom(HSSFCellStyle.BORDER_HAIR);
        stringStyle_error.setBorderTop(HSSFCellStyle.BORDER_HAIR);
        stringStyle_error.setBorderRight(HSSFCellStyle.BORDER_HAIR);
        stringStyle_error.setBorderLeft(HSSFCellStyle.BORDER_HAIR);
        stringStyle_error.setLeftBorderColor(HSSFColor.BLACK.index);
        stringStyle_error.setRightBorderColor(HSSFColor.BLACK.index);
        stringStyle_error.setBottomBorderColor(HSSFColor.BLACK.index);
        stringStyle_error.setTopBorderColor(HSSFColor.BLACK.index);
        stringStyle_error.setFillForegroundColor(HSSFColor.RED.index);
        stringStyle_error.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

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
            throw new TemplateGenException();
        }
    }

    public void CreateWorksheetConfig(HSSFSheet worksheet, int rowcount, ScanAndTestDataProcessor data) throws ScanAndTestDataException {
        HSSFRow row1 = null;
        HSSFCell cell = null;
        int colcount = 0;

        // chapter-1
        row1 = worksheet.createRow(rowcount++);
        cell = row1.createCell(0);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("Test Configuration Card");
        cell = row1.createCell(2);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("");

        // chapter-2
        row1 = worksheet.createRow(rowcount++);
        cell = row1.createCell(0);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("Created on date-time");
        cell = row1.createCell(4);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
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
        cell = row1.createCell(2);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        if (data.getScanAnyErrorPattern() > 0) {
            cell.setCellStyle(stringStyle_error);
            cell.setCellValue("<SCAN ERROR>");
        } else {
            cell.setCellStyle(stringStyle);
            cell.setCellValue("<SCAN OK>");
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

        cell = row1.createCell(colcount++);
        cell.setCellStyle(VstringStyle);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("Error Count ");

        worksheet.getRow(3).setHeight((short) 1500);
        worksheet.getRow(4).setHeight((short) 1500);

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
                        int nfail = data.getScanFailProbe(r);
                        if (nfail > 0) {
                            cellN.setCellStyle(stringStyle_error);
                        } else {
                            cellN.setCellStyle(stringStyle);
                        }
                        cellN.setCellValue(nfail);
                    }
                }
            } else {
                for (int z = 0; z < (nsize + 5); z++) {
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
                    if (z == 5) {
                        cellN = rowN.createCell(z, HSSFCell.CELL_TYPE_STRING);
                        int nfail = data.getScanFailProbe(r);
                        if (nfail > 0) {
                            cellN.setCellStyle(stringStyle_error);
                        } else {
                            cellN.setCellStyle(stringStyle);
                        }
                        cellN.setCellValue(nfail);
                    }
                }
            }
        }
        // format whole worksheet again
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
        /*   row1 = worksheet.createRow(probeTotal + 8);
         cell = row1.createCell(25);
         cell.setCellType(HSSFCell.CELL_TYPE_STRING);
         cell.setCellStyle(hiddenString);
         cell.setCellValue(getTag1());
         row1 = worksheet.createRow(probeTotal + 9);
         cell = row1.createCell(25);
         cell.setCellType(HSSFCell.CELL_TYPE_STRING);
         cell.setCellStyle(hiddenString);
         cell.setCellValue(getTag2());
         row1 = worksheet.createRow(probeTotal + 10);
         cell = row1.createCell(25);
         cell.setCellType(HSSFCell.CELL_TYPE_STRING);
         cell.setCellStyle(hiddenString);
         cell.setCellValue(getTag3()); */

        // chapter-1
        row1 = worksheet.createRow(rowcount++);
        cell = row1.createCell(0);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("Test Card");
        cell = row1.createCell(2);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("");

        // chapter-2
        row1 = worksheet.createRow(rowcount++);
        cell = row1.createCell(0);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("Created on date-time");
        cell = row1.createCell(4);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue(getCurrentDateTime());
        cell.setCellStyle(cellStyle5);
        // chapter-3
        row1 = worksheet.createRow(rowcount++);
        cell = row1.createCell(0);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("");
        cell = row1.createCell(2);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("");
        cell = row1.createCell(5);
        cell.setCellStyle(stringStyle);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("Test Pattern, From Driver# (Entry: 'L'=Loopback, 'C'=Connected, 'N'=Not Connected, 'G'=Gnd, 'E'=Error)");

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
        cell.setCellValue("Sensor#");

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
                    cellN.setCellStyle(stringStyle);
                    //byte dt =data[z - 5 + rp][r] ;
                    int probe = z - 5 + rp;
                    byte dt = data.getPatternData(probe, r);
                    //cellN.setCellValue("N" + ( z - 5 + rp ) + "," + r);
                    // getNodeColorcode(probe, r);
                    if (data.isDebug()) {
                        cellN.setCellValue(String.format("%02x", dt & 0xff).toUpperCase());
                    } else {
                        String sData = data.getScanSaveToExcelCode(probe, r);
                        if (sData.contains("E") || sData.contains("U")) {
                            cellN.setCellStyle(stringStyle_error);
                            cellN.setCellValue(sData);
                        } else {
                            cellN.setCellStyle(stringStyle);
                            cellN.setCellValue(sData);
                        }
                    }
                    // cellN.setCellComment(comment);
                }
            }
        }
        //
        // format whole worksheet again
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

}
