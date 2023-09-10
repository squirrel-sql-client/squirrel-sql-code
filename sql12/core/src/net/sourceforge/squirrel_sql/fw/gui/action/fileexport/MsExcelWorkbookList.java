package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import java.util.ArrayList;
import java.util.List;

public class MsExcelWorkbookList
{
   private List<MsExcelWorkbook> _workbooks = new ArrayList<>();

   public void addCurrentWorkbook(String workbookFileName)
   {
      _workbooks.add(new MsExcelWorkbook(workbookFileName));
   }

   public void addSheetToCurrentWorkbook(String sheetName, String sheetSql)
   {
      if(_workbooks.isEmpty())
      {
         throw new IllegalStateException("Need a workbook before sheets can be added");
      }

      _workbooks.get(_workbooks.size() - 1).addSheet(sheetName, sheetSql);
   }

   public boolean hasExportReadyWorkbook()
   {
      return _workbooks.size() > 1;
   }

   public MsExcelWorkbook checkoutExportReadyWorkbook()
   {
      if(false == hasExportReadyWorkbook())
      {
         throw new IllegalStateException("Has no export ready workbook");
      }

      // The one previous to the current workbook.
      return _workbooks.get(_workbooks.size() - 2);
   }

   public MsExcelWorkbook checkoutCurrentWorkbook()
   {
      MsExcelWorkbook ret = _workbooks.get(_workbooks.size() - 1);

      if(ret.hasSheets())
      {
         return ret;
      }

      return null;
   }
}
