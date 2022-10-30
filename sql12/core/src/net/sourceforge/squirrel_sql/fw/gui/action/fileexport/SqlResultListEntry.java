package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class SqlResultListEntry
{
   private final SqlResultTabHandle _handle;
   private final int _index;

   private String _userEnteredName;

   public SqlResultListEntry(SqlResultTabHandle handle, int index)
   {
      _handle = handle;
      _index = index;
   }

   @Override
   public String toString()
   {
      if(StringUtilities.isEmpty(_userEnteredName, true))
      {
         return DataExportExcelWriter.EXCEL_EXPORT_SHEET_NAME + " " + _index;
      }
      else
      {
         return _userEnteredName;
      }
   }

   public SqlResultTabHandle getHandle()
   {
      return _handle;
   }

   public void setUserEnteredSqlResultName(String userEnteredName)
   {
      this._userEnteredName = userEnteredName;
   }
}
