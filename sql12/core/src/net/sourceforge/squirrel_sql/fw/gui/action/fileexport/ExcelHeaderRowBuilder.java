package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelHeaderRowBuilder
{
   private Row _headerRow;
   private CellStyle _headerCellStyle;

   public ExcelHeaderRowBuilder(TableExportPreferences prefs, Sheet sheet, Workbook workbook)
   {
      _headerRow = sheet.getRow(0);
      if (_headerRow == null)
      {
         _headerRow = sheet.createRow(0);
      }

      _headerCellStyle = null;
      if(prefs.isExcelFirstRowBold())
      {
         _headerCellStyle = workbook.createCellStyle();

         Font boldFont = workbook.createFont();
         //boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
         boldFont.setBold(true);
         _headerCellStyle.setFont(boldFont);
         //_headerRow.setRowStyle(cellStyle); does not work
      }

      if(prefs.isExcelFirstRowCentered())
      {
         if(null == _headerCellStyle)
         {
            _headerCellStyle = workbook.createCellStyle();
         }

         _headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
         //_headerRow.setRowStyle(cellStyle); does not work
      }
   }

   public Cell createHeaderCell(int colIdx)
   {
      Cell ret = _headerRow.createCell(colIdx);

      if(null != _headerCellStyle)
      {
         ret.setCellStyle(_headerCellStyle);
      }

      return ret;
   }
}
