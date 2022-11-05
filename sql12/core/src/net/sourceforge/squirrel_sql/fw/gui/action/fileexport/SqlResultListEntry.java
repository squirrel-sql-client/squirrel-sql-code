package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class SqlResultListEntry
{
   private final SqlResultTabHandle _handle;
   private final int _index;

   private String _userEnteredSqlResultNameFileNormalized;

   public SqlResultListEntry(SqlResultTabHandle handle, int index)
   {
      _handle = handle;
      _index = index;
   }

   @Override
   public String toString()
   {
      return getExportNameFileNormalized();
   }

   public SqlResultTabHandle getHandle()
   {
      return _handle;
   }

   public void setUserEnteredSqlResultNameFileNormalized(String userEnteredSqlResultNameFileNormalized)
   {
      this._userEnteredSqlResultNameFileNormalized = userEnteredSqlResultNameFileNormalized;
   }

   public String getExportNameFileNormalized()
   {
      if(StringUtilities.isEmpty(_userEnteredSqlResultNameFileNormalized, true))
      {
         return DataExportExcelWriter.DEFAULT_EXCEL_EXPORT_SHEET_NAME + " " + _index;
      }
      else
      {
         return _userEnteredSqlResultNameFileNormalized;
      }
   }
}
