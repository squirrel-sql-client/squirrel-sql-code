package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelHeaderRowBuilder
{
   private Row _headerRow;

   public ExcelHeaderRowBuilder(TableExportPreferences prefs, Sheet sheet, Workbook workbook)
   {
      _headerRow = sheet.getRow(0);
      if (_headerRow == null)
      {
         _headerRow = sheet.createRow(0);
      }

      CellStyle cellStyle = null;
      if(prefs.isExcelFirstRowBold())
      {
         cellStyle = workbook.createCellStyle();

         Font boldFont = workbook.createFont();
         boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
         cellStyle.setFont(boldFont);
         _headerRow.setRowStyle(cellStyle);
      }

      if(prefs.isExcelFirstRowCentered())
      {
         if(null == cellStyle)
         {
            cellStyle = workbook.createCellStyle();
         }

         cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
         _headerRow.setRowStyle(cellStyle);
      }
   }

   public Row getHeaderRow()
   {
      return _headerRow;
   }
}
