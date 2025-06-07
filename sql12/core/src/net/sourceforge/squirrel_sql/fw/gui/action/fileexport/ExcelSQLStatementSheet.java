package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelSQLStatementSheet
{
   public static void createSqlStatementSheet(ExportDataInfo exportDataInfo, Workbook workbook)
   {
      String sqlStatementTabName = getUniqueSqlSheetName(workbook, exportDataInfo.getExcelSheetTabName(), 0);

      Sheet sheet = workbook.createSheet(sqlStatementTabName);
      Row row = sheet.createRow(0);

      // Does not work
      //sheet.setColumnWidth(1, 400);
      //row.setHeight((short) 1000);

      Cell cell = row.createCell(0, CellType.STRING);

      cell.setCellValue(exportDataInfo.getResultSetExportData().getSqlToWriteToFile());

      CellStyle cellStyle = workbook.createCellStyle();
      Font font = workbook.createFont();
      font.setFontName("Courier New");
      cellStyle.setFont(font);
      cellStyle.setWrapText(true);
      cell.setCellStyle(cellStyle);
   }

   private static String getUniqueSqlSheetName(Workbook workbook, String excelSheetTabName, int numberToCheck)
   {
      String candidate = excelSheetTabName + "_SQL";

      if(numberToCheck > 0)
      {
         candidate += numberToCheck;
      }

      for(int i = 0; i < workbook.getNumberOfSheets(); i++)
      {
         Sheet sheet = workbook.getSheetAt(i);
         if(StringUtils.equalsIgnoreCase(sheet.getSheetName(), candidate))
         {
            candidate = getUniqueSqlSheetName(workbook, excelSheetTabName, ++numberToCheck);
         }
      }

      return candidate;
   }
}
