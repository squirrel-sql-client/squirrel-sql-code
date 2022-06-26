package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.fw.sql.tablenamefind.TableNameFindService;


public class EditableSqlCheck
{
   private String _tableNameFromSQL = null;

   public EditableSqlCheck(SQLExecutionInfo exInfo, ISession session)
   {
      if(null == exInfo || null == exInfo.getSQL())
      {
         return;
      }

      _tableNameFromSQL = exInfo.getTableToBeEdited();

      if (null == _tableNameFromSQL)
      {
         _tableNameFromSQL = TableNameFindService.findTableNameInSQL(exInfo.getSQL(), session);
      }
   }


   public boolean allowsEditing()
   {
      return null != _tableNameFromSQL;
   }

   public String getTableNameFromSQL()
   {
      return _tableNameFromSQL;
   }
}
