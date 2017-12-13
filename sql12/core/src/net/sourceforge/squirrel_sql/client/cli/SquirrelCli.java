package net.sourceforge.squirrel_sql.client.cli;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;


public class SquirrelCli
{
   private static ISQLAlias _aliasToConnectTo;

   public static void connect(String aliasName)
   {
      Iterator<ISQLAlias> aliasIterator = Main.getApplication().getDataCache().aliases();

      while(aliasIterator.hasNext())
      {
         ISQLAlias alias = aliasIterator.next();

         if(aliasName.equals(alias.getName()))
         {
            _aliasToConnectTo = alias;

            if (CliInitializer.getShellMode() == ShellMode.CLI)
            {
               System.out.println("Connected to Alias \"" + aliasName + "\"");
            }

            return;
         }
      }

      throw new IllegalArgumentException("Alias name \"" + aliasName + "\" not found.");
   }

   public static void exec(String sql)
   {
      //System.out.println("sql = " + sql);

      if(null == _aliasToConnectTo)
      {
         System.err.println("ERROR: No database connection has been opened. Call connect(...) to open a connection.");
         return;
      }

      CliSession cliSession = new CliSession(_aliasToConnectTo);

      ISQLExecuterHandler sqlExecuterHandlerProxy = new CliSQLExecuterHandler(cliSession);

      SQLExecuterTask sqlExecuterTask = new SQLExecuterTask(cliSession, sql, sqlExecuterHandlerProxy);
      sqlExecuterTask.setExecuteEditableCheck(false);

      sqlExecuterTask.run();
   }
}
