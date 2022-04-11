package net.sourceforge.squirrel_sql.client.cli;

import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

public class SquirrelBatch
{
   public static void main(String[] args) throws ParseException
   {
      try
      {
         String squirrelHomeDir = System.getProperty("squirrel.home");

//      System.out.println("###############################squirrelHome = " + squirrelHomeDir);
//
//      for (int i = 0; i < args.length; i++)
//      {
//         String arg = args[i];
//         System.out.println("*********SQUIRRELBATCH " + (i+1) + ":  " + arg);
//      }


         CommandLineParser parser = new DefaultParser();

         CommandLine commandLine = parser.parse(SquirrelBatchOptions.getOptions(), args);


         if(SquirrelBatchOptions.HELP.hasParam(commandLine))
         {
            System.out.println();

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("squirrelcli.sh or squirrelcli.bat", SquirrelBatchOptions.getOptions());

            System.out.println("\nNote: No parameter (or -userdir only) enters Java JShell based CLI.\n");

            System.out.println();
            String squirrelUserDir = ApplicationFiles.getStandardUserDir();

            if(false == StringUtilities.isEmpty(getUserDir(commandLine), true))
            {
               squirrelUserDir = getUserDir(commandLine);
            }

            System.out.println("SQuirreL CLI needs a proper installation of the SQuirreL UI application. Here's some information about the installation:");
            System.out.println("  Installation home directory: -home=" + squirrelHomeDir);
            System.out.println("  User data directory: -userdir=" + squirrelUserDir);

            return;
         }


         String validationResult = SquirrelBatchOptions.isValid(commandLine);

         if(null != validationResult)
         {
            System.out.println();
            System.err.println(validationResult+ "\n");

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("squirrelcli.sh or squirrelcli.bat", SquirrelBatchOptions.getOptions());
            return;
         }


         CliInitializer.initializeSquirrelInCliMode(ShellMode.BATCH, squirrelHomeDir, getUserDir(commandLine));


         if(SquirrelBatchOptions.ALIAS.hasParam(commandLine))
         {
            String alias = SquirrelBatchOptions.ALIAS.getValue(commandLine);
            if (SquirrelBatchOptions.PASSWORD.hasParam(commandLine))
            {
               String password = SquirrelBatchOptions.PASSWORD.getValue(commandLine);
               SquirrelCli.connect(alias, password);
            }
            else
            {
               SquirrelCli.connect(alias);
            }
         }
         else
         {
            String url = SquirrelBatchOptions.URL.getValue(commandLine);
            String user = SquirrelBatchOptions.USER.getValue(commandLine);

            String password = null;
            if( SquirrelBatchOptions.USER.hasParam(commandLine) )
            {
               password = SquirrelBatchOptions.PASSWORD.getValue(commandLine);
            }

            String driver = SquirrelBatchOptions.DRIVER.getValue(commandLine);
            String drivercp = SquirrelBatchOptions.DRIVERCP.getValue(commandLine);

            SquirrelCli.connect(url, user, password, driver, drivercp);
         }

         if (SquirrelBatchOptions.MAX_ROWS.hasParam(commandLine))
         {
            SquirrelCli.setMaxRows(Integer.parseInt(SquirrelBatchOptions.MAX_ROWS.getValue(commandLine)));
         }


         String sql = SquirrelBatchOptions.SQL.getValue(commandLine);

         sql = CLISqlFileHandler.handleOptionalSqlFile(sql, false);

         if (SquirrelBatchOptions.TO_FILE_FORMATTED.hasParam(commandLine))
         {
            SquirrelCli._execIntern(sql, SquirrelBatchOptions.TO_FILE_FORMATTED.getValue(commandLine), true);
         }
         else
         {
            SquirrelCli._execIntern(sql, null, false);
         }

         SquirrelCli.close();
      }
      catch(Exception e)
      {
         e.printStackTrace();
      }
      finally
      {
         System.exit(0);
      }
   }

   private static String getUserDir(CommandLine commandLine)
   {
      if(SquirrelBatchOptions.USERDIR.hasParam(commandLine))
      {
         return SquirrelBatchOptions.USERDIR.getValue(commandLine);
      }

      return null;
   }
}
