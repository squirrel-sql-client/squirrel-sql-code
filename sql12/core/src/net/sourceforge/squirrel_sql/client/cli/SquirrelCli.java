package net.sourceforge.squirrel_sql.client.cli;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

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

      CliSession cliSession = new CliSession(_aliasToConnectTo);

      ISQLExecuterHandler sqlExecuterHandlerProxy = new CliSQLExecuterHandler(cliSession);

      SQLExecuterTask sqlExecuterTask = new SQLExecuterTask(cliSession, sql, sqlExecuterHandlerProxy);
      sqlExecuterTask.setExecuteEditableCheck(false);

      sqlExecuterTask.run();
   }

   public static void evaluateScriptParams(String connectParam, String execParam)
   {

      if(StringUtilities.isEmpty(connectParam, true) || StringUtilities.isEmpty(execParam, true))
      {
         String msg = "Missing batch mode parameters:\n" +
               "   First parameter must be \\\"connect(<Alias name>)\\\"\"\n" +
               "   Second parameter must be one or more SQL statements. More than one statements must be separated by SQuirreLs statement separator.";
         throw new IllegalArgumentException(msg);
      }



      if(false == connectParam.startsWith("connect(") || false == connectParam.endsWith(")"))
      {
         throw new IllegalArgumentException("First parameter must be \"connect(<Alias name>)\"");
      }

      String aliasName = connectParam.substring("connect(".length());

      aliasName = aliasName.substring(0, aliasName.length() - 1);

      connect(aliasName);

      System.out.println("############### " + execParam);
      exec(execParam);
   }
}
