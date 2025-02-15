package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class ExcelHeaderRowBuilder
{
   private Row _headerRow;
   private final ExcelGlobalCellStyleHandler _globalCellStyleHandler;

   public ExcelHeaderRowBuilder(ExcelGlobalCellStyleHandler globalCellStyleHandler, Sheet sheet)
   {
      _globalCellStyleHandler = globalCellStyleHandler;

      _headerRow = sheet.getRow(0);
      if (_headerRow == null)
      {
         _headerRow = sheet.createRow(0);
      }
   }

   public Cell createHeaderCell(int colIdx)
   {
      Cell ret = _headerRow.createCell(colIdx);

      if(null != _globalCellStyleHandler.getHeaderCellStyle())
      {
         ret.setCellStyle(_globalCellStyleHandler.getHeaderCellStyle());
      }

      return ret;
   }
}
