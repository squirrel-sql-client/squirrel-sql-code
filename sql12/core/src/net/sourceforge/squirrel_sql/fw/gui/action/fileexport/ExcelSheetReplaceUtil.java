package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import java.io.FileInputStream;
import java.io.IOException;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelSheetReplaceUtil
{
   private static final ILogger s_log = LoggerController.createLogger(ExcelSheetReplaceUtil.class);


   public static Sheet getOrCreateSheet(Workbook workbook, String excelSheetTabName, TableExportPreferences prefs)
   {
      if(false == isReplaceExcelSheets(prefs))
      {
         return workbook.createSheet(excelSheetTabName);
      }

      Sheet sheet = workbook.getSheet(excelSheetTabName);
      if(null == sheet)
      {
         return workbook.createSheet(excelSheetTabName);
      }

      // AI: Clear existing contents
      for(int rowIndex = sheet.getLastRowNum(); rowIndex >= 0; rowIndex--)
      {
         Row row = sheet.getRow(rowIndex);
         if(row != null)
         {
            sheet.removeRow(row);
         }
      }

      return sheet;

   }


   public static boolean isReplaceExcelSheets(TableExportPreferences prefs)
   {
      return prefs.isExcelReplaceSheets();
   }

   public static Workbook getExistingWorkBookOrNull(FileExportService fileExportService)
   {
      if(false == isReplaceExcelSheets(fileExportService.getPrefs()))
      {
         return null;
      }


      if(!hasExcelExtension(fileExportService.getFile().getAbsolutePath()))
      {
         return null; // Quick check by extension
      }

      try(FileInputStream fis = new FileInputStream(fileExportService.getFile()))
      {
         Workbook workbook = WorkbookFactory.create(fis); // Will throw exception if not valid
         return workbook;
      }
      catch(IOException e)
      {
         s_log.warn("File to replace sheet(s) in is not an MSExcelFile", e);
         return null;
      }
   }

   /**
    * AI: Method to check by extension
    */
   public static boolean hasExcelExtension(String filePath)
   {
      return StringUtils.endsWithIgnoreCase(filePath,".xls") || StringUtils.endsWithIgnoreCase(filePath,".xlsx");
   }
}
