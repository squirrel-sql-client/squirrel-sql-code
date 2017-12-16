package net.sourceforge.squirrel_sql.client.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SquirrelBatch
{
   public static void main(String[] args) throws ParseException
   {
//      for (String arg : args)
//      {
//         System.out.println(arg);
//      }


      CommandLineParser parser = new DefaultParser();

      CommandLine commandLine = parser.parse(SquirrelBatchOptions.getOptions(), args);


      if(SquirrelBatchOptions.HELP.hasParam(commandLine))
      {
         System.out.println();

         HelpFormatter formatter = new HelpFormatter();
         formatter.printHelp("squirrelcli.sh or squirrelcli.bat", SquirrelBatchOptions.getOptions());

         System.out.println("\nNote: No parameter enters Java 9 JShell based CLI.\n");

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


      net.sourceforge.squirrel_sql.client.cli.CliInitializer.initializeSquirrelInCliMode(ShellMode.BATCH, args[0], args[1]);

      String alias = SquirrelBatchOptions.ALIAS.getValue(commandLine);

      if(null == alias)
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
      else
      {
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





      String sql = SquirrelBatchOptions.SQL.getValue(commandLine);

      Path path = Paths.get(sql);

      if(Files.isRegularFile(path))
      {
         try
         {
            sql = new String(Files.readAllBytes(path));
         }
         catch (IOException e)
         {
            System.err.println("ERROR: Failed to read file " + path.getFileName() + ": " + e.getMessage());
            e.printStackTrace();
         }
      }

      net.sourceforge.squirrel_sql.client.cli.SquirrelCli.exec(sql);
   }
}
