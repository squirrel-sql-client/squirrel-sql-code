package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
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
   public static void createSqlStatementSheet(ExportDataInfo exportDataInfo, Workbook workbook, TableExportPreferences prefs)
   {
      if(StringUtilities.isEmpty(exportDataInfo.getSqlToWriteToFile(), true))
      {
         return;
      }

      Sheet sheet = getOrCreateSqlStatementSheet(exportDataInfo, workbook, prefs);
      Row row = sheet.createRow(0);

      // Does not work
      //sheet.setColumnWidth(1, 400);
      //row.setHeight((short) 1000);

      Cell cell = row.createCell(0, CellType.STRING);

      cell.setCellValue(exportDataInfo.getSqlToWriteToFile());

      CellStyle cellStyle = workbook.createCellStyle();
      Font font = workbook.createFont();
      font.setFontName("Courier New");
      cellStyle.setFont(font);
      cellStyle.setWrapText(true);
      cell.setCellStyle(cellStyle);
   }

   private static Sheet getOrCreateSqlStatementSheet(ExportDataInfo exportDataInfo, Workbook workbook, TableExportPreferences prefs)
   {
      if(ExcelSheetReplaceUtil.isReplaceExcelSheets(prefs))
      {
         String sqlStatementTabName = getInitialCandidateSqlSheetName(exportDataInfo.getExcelSheetTabName(prefs));
         return ExcelSheetReplaceUtil.getOrCreateSheet(workbook, sqlStatementTabName, prefs);
      }
      else
      {
         String sqlStatementTabName = getUniqueSqlSheetName(workbook, exportDataInfo.getExcelSheetTabName(prefs), 0);
         Sheet sheet = workbook.createSheet(sqlStatementTabName);
         return sheet;
      }
   }

   private static String getUniqueSqlSheetName(Workbook workbook, String excelSheetTabName, int numberToCheck)
   {
      String candidate = getInitialCandidateSqlSheetName(excelSheetTabName);

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

   private static String getInitialCandidateSqlSheetName(String excelSheetTabName)
   {
      return excelSheetTabName + "_SQL";
   }
}
