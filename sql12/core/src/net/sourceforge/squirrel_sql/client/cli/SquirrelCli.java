package net.sourceforge.squirrel_sql.client.cli;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.session.ISQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.id.UidIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriver;
import net.sourceforge.squirrel_sql.fw.util.NullMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.regex.Pattern;


public class SquirrelCli
{
   private static ISQLAlias _aliasToConnectTo;

   public static void connect(String aliasName)
   {
      connect(aliasName, null);
   }

   public static void connect(String aliasName, String password)
   {
      Iterator<ISQLAlias> aliasIterator = Main.getApplication().getDataCache().aliases();

      while(aliasIterator.hasNext())
      {
         ISQLAlias alias = aliasIterator.next();

         if(aliasName.equals(alias.getName()))
         {
            _aliasToConnectTo = alias;

            if(null != password)
            {
               try
               {
                  _aliasToConnectTo.setPassword(password);
               }
               catch (ValidationException e)
               {
                  throw new RuntimeException(e);
               }
            }


            if (CliInitializer.getShellMode() == ShellMode.CLI)
            {
               System.out.println("Connected to Alias \"" + aliasName + "\"");
            }

            return;
         }
      }

      throw new IllegalArgumentException("Alias name \"" + aliasName + "\" not found.");
   }

   public static void connect(String url, String user, String password, String driver, String drivercp)
   {
      try
      {
         SQLDriver sqlDriver = new SQLDriver(new UidIdentifier());
         sqlDriver.setDriverClassName(driver);

         String classPathSeparator = System.getProperty("path.separator");
         sqlDriver.setJarFileNames(drivercp.split(Pattern.quote(classPathSeparator)));

         sqlDriver.setName("temporaryDriver_" + url + "_" + sqlDriver.getIdentifier().toString());

         Main.getApplication().getDataCache().addDriver(sqlDriver, NullMessageHandler.getInstance());


         SQLAlias alias = new SQLAlias(new UidIdentifier());

         alias.setName("temporaryAlias_" + url + "_" + sqlDriver.getIdentifier().toString());

         alias.setUrl(url);
         alias.setUserName(user);

         if (null != password)
         {
            alias.setPassword(password);
         }

         alias.setDriverIdentifier(sqlDriver.getIdentifier());

         _aliasToConnectTo = alias;

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }

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
