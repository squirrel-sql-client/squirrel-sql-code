package net.sourceforge.squirrel_sql.client.cli;

import net.sourceforge.squirrel_sql.client.Application;
import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

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
         System.out.println("------------------------------------------------------------------");
         System.out.println("-- Welcome to the SQuirreL CLI (Command Line Interface)");
         System.out.println("-- Enter help() for information.");
         System.out.println("-- Enter /exit to leave.");
         System.out.println("------------------------------------------------------------------");
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

      Application application = new Application();
      Main.setApplication(application);
      application.initResourcesAndPrefs();
      application.initAppFiles();
      application.initDriverManager();
      application.initDataCache();
   }


   public static ShellMode getShellMode()
   {
      return _shellMode;
   }

   public static boolean isInShellMode()
   {
      return null != _shellMode;
   }


}
