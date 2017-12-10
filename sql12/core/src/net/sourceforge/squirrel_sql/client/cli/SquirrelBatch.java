package net.sourceforge.squirrel_sql.client.cli;

public class SquirrelBatch
{
   public static void main(String[] args)
   {
//      for (String arg : args)
//      {
//         System.out.println("arg = " + arg);
//      }

      net.sourceforge.squirrel_sql.client.cli.CliInitializer.initializeSquirrelInCliMode(ShellMode.BATCH, args[0], args[1]);

      String connectParam = null;
      if(args.length > 2)
      {
         connectParam = args[2];
      }

      String execParam = null;
      if(args.length > 3)
      {
         execParam = args[3];
      }

      net.sourceforge.squirrel_sql.client.cli.SquirrelCli.evaluateScriptParams(connectParam, execParam);
   }
}
