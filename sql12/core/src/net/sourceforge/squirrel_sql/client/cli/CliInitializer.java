package net.sourceforge.squirrel_sql.client.cli;

import net.sourceforge.squirrel_sql.client.Application;
import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import org.apache.log4j.PropertyConfigurator;

import java.util.Properties;

public class CliInitializer
{

   private static ShellMode _shellMode;

   public static void initializeSquirrelInCliMode(ShellMode shellMode)
   {
      String squirrelHome =  System.getProperty("squirrel.home");
      String squirrelUserDir =  System.getProperty("squirrel.userdir");

      initializeSquirrelInCliMode(shellMode, squirrelHome, squirrelUserDir);
   }

   public static void initializeSquirrelInCliMode(ShellMode shellMode, String squirrelHomeDir, String squirrelUserDir)
   {
      _shellMode = shellMode;

      if(StringUtilities.isEmpty(squirrelHomeDir, true))
      {
         throw new IllegalArgumentException("-Dsquirrel.home must be non null");
      }

      if (shellMode == ShellMode.CLI)
      {
         System.out.println();
         System.out.println("SQuirreL CLI environment information:");
         System.out.println("  squirrelHome = " + squirrelHomeDir);
         System.out.println("  squirrelUser = " + squirrelUserDir);
         System.out.println();
      }


      if (StringUtilities.isEmpty(squirrelUserDir, true))
      {
         ApplicationArguments.initialize(new String[]{"-nosplash", "-no-plugins", "-home", squirrelHomeDir});
      }
      else
      {
         ApplicationArguments.initialize(new String[]{"-no-splash", "-no-plugins", "-home", squirrelHomeDir, "-userdir", squirrelUserDir});
      }

      initLogging();


      Application application = new Application();
      Main.setApplication(application);
      application.initResourcesAndPrefs();
      application.initAppFiles();
      application.initDriverManager();
      application.initDataCache();

      if (shellMode == ShellMode.CLI && application.getSquirrelPreferences().getSessionProperties().getSQLLimitRows())
      {
         System.out.println("NOTE: SQL results are limited to " + application.getSquirrelPreferences().getSessionProperties().getSQLNbrRowsToShow() + " rows.");
         System.out.println();
      }
   }

   private static void initLogging()
   {
      Properties props = new Properties();
      props.setProperty("log4j.rootLogger", "off, SquirrelAppender");
      props.setProperty("log4j.appender.SquirrelAppender", "net.sourceforge.squirrel_sql.client.SquirrelFileSizeRollingAppender");
      props.setProperty("log4j.appender.SquirrelAppender.layout", "org.apache.log4j.PatternLayout");
      props.setProperty("log4j.appender.SquirrelAppender.layout.ConversionPattern", "%d{ISO8601} [%t] %-5p %c %x - %m%n");

      PropertyConfigurator.configure(props);
   }

   public static ShellMode getShellMode()
   {
      return _shellMode;
   }
}
