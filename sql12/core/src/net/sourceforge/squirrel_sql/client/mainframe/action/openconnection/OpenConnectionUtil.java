package net.sourceforge.squirrel_sql.client.mainframe.action.openconnection;

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;

public class OpenConnectionUtil
{
   public static SQLConnection createSQLConnection(ISQLAlias sqlAlias, String userName, String password, SQLDriverPropertyCollection props)
   {
      final OpenConnectionCommand command = new OpenConnectionCommand(sqlAlias, userName, password, props);
      command.executeAndWait();
      return command.getSQLConnection();
   }
}
