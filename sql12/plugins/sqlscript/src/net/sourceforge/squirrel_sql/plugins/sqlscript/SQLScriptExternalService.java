package net.sourceforge.squirrel_sql.plugins.sqlscript;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.CreateTableScriptCommand;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;


public class SQLScriptExternalService
{
   private SQLScriptPlugin _sqlScriptPlugin;

   public SQLScriptExternalService(SQLScriptPlugin sqlScriptPlugin)
   {
      _sqlScriptPlugin = sqlScriptPlugin;
   }

   public void scriptTablesToSQLEntryArea(ISession sess, ITableInfo[] tis)
   {
      new CreateTableScriptCommand(sess.getObjectTreeAPIOfActiveSessionWindow(), _sqlScriptPlugin).scriptTablesToSQLEntryArea(tis);
   }

}
