package UIMain;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

/**
 *
 * @author de
 */
public class FormUtility {

    /**
     * check and get number of row and col and check if create and formattied,
     * if not create and format it
     *
     * @param worksheet
     */
    protected void formatWholeSheet(HSSFSheet worksheet, HSSFCellStyle stringStyle) {

        // do some final formatting the sheet
        int lastcols = 0;
        int lastrows = worksheet.getLastRowNum();
        for (int rowIx = 0; rowIx < lastrows; rowIx++) {
            HSSFRow rowI = worksheet.getRow(rowIx);
            short minColIx = rowI.getFirstCellNum();
            short maxColIx = rowI.getLastCellNum();
            for (short colIx = minColIx; colIx < maxColIx; colIx++) {
                HSSFCell cellI = rowI.getCell(colIx);
                if (cellI == null) {
                    continue;
                }
                if (cellI.getCellStyle() == null) {
                    cellI.setCellStyle(stringStyle);
                }
            }
            if (lastcols < maxColIx) {
                lastcols = maxColIx;
            }
        }
        if (lastcols < 24) {
            lastcols = 24 + 5;
        }
        //System.out.println("last col = " + lastcols);
        for (int rowIx = 0; rowIx < lastrows + 1; rowIx++) {
            HSSFRow rowI = worksheet.getRow(rowIx);
            if (rowI == null) {
                rowI = worksheet.createRow(rowIx);
            }
            for (short colIx = 0; colIx < lastcols; colIx++) {
                HSSFCell cellI = rowI.getCell(colIx);
                if (cellI == null) {
                    cellI = rowI.createCell(colIx);
                    cellI.setCellStyle(stringStyle);
                    //System.out.println("create cell  col = " + colIx + ", row = " + rowIx);
                }
                if (cellI.getCellStyle() == null) {
                    cellI.setCellStyle(stringStyle);
                    //System.out.println("create style col = " + colIx + ", row = " + rowIx);
                }
            }
        }
    }
}
