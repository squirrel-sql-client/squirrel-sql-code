package net.sourceforge.squirrel_sql.client.cli;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class SquirrelCliHelp
{
   public static void printHelp()
   {
      System.out.println();
      System.out.println("The squirrelcli* script offers two modes:");
      System.out.println("1. The Java 9 JShell based mode (this mode).");
      System.out.println("2. The batch mode which you can learn about by entering squirrelcli* -help on your command line.");
      System.out.println();
      System.out.println("This describes Java 9 JShell based mode (this mode):");
      System.out.println("- For information on JSell see http://cr.openjdk.java.net/~rfield/tutorial/JShellTutorial.html");
      System.out.println("- The following functions are offered by SQuirreL's shell based mode:");
      System.out.println("-- connect(<aliasName>) --> Connects to an Alias defined in SQuirreL's UI.");
      System.out.println("-- connect(<aliasName>, <password>) --> Connects to an Alias defined SQuirreL's UI using the given password.");
      System.out.println("-- connect(<jdbcUrl>, <user>, <password>, <driverClassName>, <driverCP>) --> Connects to a database the usual JDBC way.");
      System.out.println("     <driverCP> is the JDBC driver class path. If multiple class path entries are needed use the platform specific class path separator.");
      System.out.println("     On your system that is \"" + System.getProperty("path.separator")+ "\"");
      System.out.println("-- setMaxRows(<maxRows>) --> Defines the maximum number of rows read from an SQL result. 0 means no limit. The current maximum is " + getCurrentMaxDesc() + ".");
      System.out.println("-- exec(<sqlOrSqlFile>) --> Executes one or more SQLs or the contents of an SQL script file.");
      System.out.println("     Currently \""  + getSqlSeparator() + "\" is defined as the separator for multiple SQLs. The separator can be defined in SQuirreL UI's \"New Session properties\".");
      System.out.println("-- exec(<sqlOrSqlFile>, <outputFile>) --> Allows to write the output to a file instead of STDOUT.");
      System.out.println("-- exec(<sqlOrSqlFile>, <outputFile>, <formatted>) --> Allows to write the output to a file instead of STDOUT");
      System.out.println("     When <formatted> is true File format will be according to SQuirreL's 'Store result of SQL to file' dialog.");
      System.out.println("-- close() --> Closes the current database connection.");
      System.out.println("-- help() --> Prints this help.");

      System.out.println();
      String squirrelHomeDir =  System.getProperty("squirrel.home");
      String squirrelUserDir =  System.getProperty("squirrel.userdir");

      if(StringUtilities.isEmpty(squirrelUserDir, true))
      {
         squirrelUserDir = ApplicationFiles.getStandardUserDir();
      }

      System.out.println("SQuirreL CLI needs a proper installation of the SQuirreL UI application. Here's some information about the installation:");
      System.out.println("  Installation home directory: -home=" + squirrelHomeDir);
      System.out.println("  User data directory: -userdir=" + squirrelUserDir);
   }

   private static int getCurrentMaxDesc()
   {
      if (Main.getApplication().getSquirrelPreferences().getSessionProperties().getSQLLimitRows())
      {
         return Main.getApplication().getSquirrelPreferences().getSessionProperties().getSQLNbrRowsToShow();
      }
      else
      {
         return 0;
      }
   }

   private static String getSqlSeparator()
   {
      return Main.getApplication().getSquirrelPreferences().getSessionProperties().getSQLStatementSeparator();
   }
}
