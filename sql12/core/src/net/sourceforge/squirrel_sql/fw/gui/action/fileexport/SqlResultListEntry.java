package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class SqlResultListEntry
{
   private SqlResultTabHandle _handle;
   private String _sql;
   private int _index;

   private String _userEnteredSqlResultNameFileNormalized;

   public SqlResultListEntry(SqlResultTabHandle handle, int index)
   {
      _handle = handle;
      _index = index;
   }

   public SqlResultListEntry(String sql, int index)
   {
      _sql = sql;
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

   public String getSql()
   {
      return _sql;
   }

   public void setUserEnteredSqlResultNameFileNormalized(String userEnteredSqlResultNameFileNormalized)
   {
      this._userEnteredSqlResultNameFileNormalized = userEnteredSqlResultNameFileNormalized;
   }

   public String getExportNameFileNormalized()
   {
      if(StringUtilities.isEmpty(_userEnteredSqlResultNameFileNormalized, true))
      {
         return ExportUtil.createDefaultExportName(_index);
      }
      else
      {
         return _userEnteredSqlResultNameFileNormalized;
      }
   }

}
