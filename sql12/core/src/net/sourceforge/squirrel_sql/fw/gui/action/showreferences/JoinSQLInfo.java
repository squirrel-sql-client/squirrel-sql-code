package net.sourceforge.squirrel_sql.fw.gui.action.showreferences;

public class JoinSQLInfo
{
   private final String _sql;
   private final String _tableToBeEdited;

   public JoinSQLInfo(String sql, String tableToBeEdited)
   {
      _sql = sql;
      _tableToBeEdited = tableToBeEdited;
   }

   public String getSql()
   {
      return _sql;
   }

   public String getTableToBeEdited()
   {
      return _tableToBeEdited;
   }
}
