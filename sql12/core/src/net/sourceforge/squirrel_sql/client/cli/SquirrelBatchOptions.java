package net.sourceforge.squirrel_sql.client.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public enum SquirrelBatchOptions
{
   ALIAS(new Option("alias", true, "SQuirreL Alias to connect to")),
   PASSWORD(new Option("password", true, "Connection password")),
   USER(new Option("user", true, "Connection password")),

   URL(new Option("url", true, "JDBC URL")),
   DRIVER(new Option("driver", true, "JDBC driver class name")),
   DRIVERCP(new Option("drivercp", true, "JDBC driver class path (a Java classpath)")),
   SQL(new Option("sql", true, "SQL statement or SQL script file")),
   HELP(new Option("help", false, "Print help"));

   private Option _option;

   SquirrelBatchOptions(Option option)
   {
      _option = option;
   }

   public static Options getOptions()
   {
      Options options = new Options();

      for (SquirrelBatchOptions squirrelBatchOption : values())
      {
         options.addOption(squirrelBatchOption._option);
      }

      return options;
   }

   public static String isValid(CommandLine commandLine)
   {
      if(false == commandLine.hasOption(SQL._option.getOpt()))
      {
         return "Missing parameter " + SQL._option.getOpt();
      }


      if(commandLine.hasOption(ALIAS._option.getOpt()) && commandLine.hasOption(URL._option.getOpt()))
      {
         return "Only one of the parameters " + ALIAS._option.getOpt() + ", " + URL._option.getOpt() + " is allowed";
      }

      if(commandLine.hasOption(ALIAS._option.getOpt()))
      {
         return null;
      }
      else if(commandLine.hasOption(URL._option.getOpt()))
      {
         if(false == commandLine.hasOption(DRIVER._option.getOpt()))
         {
            return "Missing parameter " + DRIVER._option.getOpt();
         }
         else if(false == commandLine.hasOption(DRIVERCP._option.getOpt()))
         {
            return "Missing parameter " + DRIVERCP._option.getOpt();
         }
         else if(false == commandLine.hasOption(USER._option.getOpt()))
         {
            return "Missing parameter " + USER._option.getOpt();
         }
//         else if(false == commandLine.hasOption(PASSWORD._option.getOpt()))
//         {
//            return "Missing parameter " + PASSWORD._option.getOpt();
//         }

         return null;

      }
      else
      {
         return "One of the parameters " + ALIAS._option.getOpt() + ", " + URL._option.getOpt() + " must be passed";
      }


   }

   public String getValue(CommandLine commandLine)
   {
      return commandLine.getOptionValue(_option.getOpt());
   }

   public boolean hasParam(CommandLine commandLine)
   {
      return commandLine.hasOption(_option.getOpt());
   }
}
