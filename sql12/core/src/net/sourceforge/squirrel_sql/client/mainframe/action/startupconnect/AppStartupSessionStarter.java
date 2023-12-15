package net.sourceforge.squirrel_sql.client.mainframe.action.startupconnect;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.AliasesAndDriversManager;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasCommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AppStartupSessionStarter
{
   private static final ILogger s_log = LoggerController.createLogger(AppStartupSessionStarter.class);
   private static StringManager s_stringMgr = StringManagerFactory.getStringManager(AppStartupSessionStarter.class);


   public static void openStartupSessions(ApplicationArguments args)
   {

      final List<SQLAlias> startUpAliases = new ArrayList<>();
      final AliasesAndDriversManager cache = Main.getApplication().getAliasesAndDriversManager();

      boolean foundStartupJdbcUrl = false;
      boolean foundStartupAliasName = false;
      for (Iterator<? extends SQLAlias> it = cache.aliases(); it.hasNext();)
      {
         SQLAlias alias = it.next();
         if (alias.isConnectAtStartup())
         {
            startUpAliases.add(alias);
         }
         else if(   false == foundStartupJdbcUrl
                 && false == StringUtilities.isEmpty(args.getStartupJdbcUrl(), true)
                 && args.getStartupJdbcUrl().trim().equals(alias.getUrl()))
         {
            foundStartupJdbcUrl = true;
            startUpAliases.add(alias);
         }
         else if(   false == foundStartupAliasName
                 && false == StringUtilities.isEmpty(args.getStartupAliasName(), true)
                 && args.getStartupAliasName().trim().equals(alias.getName()))
         {
            foundStartupAliasName = true;
            startUpAliases.add(alias);
         }
      }

      for (SQLAlias alias : startUpAliases)
      {
         s_log.info("Connecting during Application start to Alias: \"" + alias.getName() + "\" (JDBC-URL: " +  alias.getUrl() + ")");
         new ConnectToAliasCommand(alias).execute();
      }

      if(false == StringUtilities.isEmpty(args.getStartupJdbcUrl(), true) && false == foundStartupJdbcUrl)
      {
         String msg = s_stringMgr.getString("AppStartupSessionStarter.no.alias.for.jdbcurl", args.getStartupJdbcUrl().trim());
         Main.getApplication().getMessageHandler().showErrorMessage(msg);
         s_log.error(msg);
      }
      if(false == StringUtilities.isEmpty(args.getStartupAliasName(), true) && false == foundStartupAliasName)
      {
         String msg = s_stringMgr.getString("AppStartupSessionStarter.no.alias.for.alias.name", args.getStartupAliasName().trim());
         Main.getApplication().getMessageHandler().showErrorMessage(msg);
         s_log.error(msg);
      }
   }
}
