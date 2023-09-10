package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

public class MsExcelSheet
{
   private final String _sheetName;
   private final String _sheetSql;

   public MsExcelSheet(String sheetName, String sheetSql)
   {
      _sheetName = sheetName;
      _sheetSql = sheetSql;
   }

   public String getSheetName()
   {
      return _sheetName;
   }

   public String getSheetSql()
   {
      return _sheetSql;
   }
}
