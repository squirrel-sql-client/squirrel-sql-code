package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

public class SqlResultListEntry
{
   private final SqlResultTabHandle _handle;
   private final int _index;

   public SqlResultListEntry(SqlResultTabHandle handle, int index)
   {
      _handle = handle;
      _index = index;
   }

   @Override
   public String toString()
   {
      return DataExportExcelWriter.EXCEL_EXPORT_SHEET_NAME + " " + _index;
   }

   public SqlResultTabHandle getHandle()
   {
      return _handle;
   }
}
