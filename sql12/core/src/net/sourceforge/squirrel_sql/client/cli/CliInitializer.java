package net.sourceforge.squirrel_sql.client.cli;

import net.sourceforge.squirrel_sql.client.Application;
import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class CliInitializer
{

   public static void initializeSquirrelInCliMode()
   {
      String squirrelHome =  System.getProperty("squirrel.home");
      String squirrelUserDir =  System.getProperty("squirrel.userdir");

      if(StringUtilities.isEmpty(squirrelHome, true))
      {
         throw new IllegalArgumentException("-Dsquirrel.home must be non null");
      }

      System.out.println("###############squirrelHome = " + squirrelHome);
      System.out.println("###############squirrelUser = " + squirrelUserDir);

      //Main.setCliMode();

      if (StringUtilities.isEmpty(squirrelUserDir, true))
      {
         ApplicationArguments.initialize(new String[]{"-nosplash", "-no-plugins", "-home", squirrelHome});
      }
      else
      {
         ApplicationArguments.initialize(new String[]{"-no-splash", "-no-plugins", "-home", squirrelHome, "-userdir", squirrelUserDir});
      }

      Application application = new Application();
      Main.setApplication(application);
      application.initResourcesAndPrefs();
      application.initAppFiles();
      application.initDriverManager();
      application.initDataCache();

      System.out.println("CliInitializer.initializeSquirrelInCliMode DONE");

   }

}
