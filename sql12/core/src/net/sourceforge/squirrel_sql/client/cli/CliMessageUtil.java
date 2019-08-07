package net.sourceforge.squirrel_sql.client.cli;

import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.awt.TrayIcon;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CliMessageUtil
{
   public static RuntimeException wrapRuntime(Throwable th)
   {
      // If th is thrown it will be outputted to the command line so for now we do nothing here.
      return Utilities.wrapRuntime(th);
   }

   public static void showMessage(CliMessageType messageType, Throwable th)
   {
      System.out.println(messageType.name() + ":\n" + Utilities.getStackTrace(Utilities.getDeepestThrowable(th)));
   }

   public static void showMessage(CliMessageType messageType, String msg)
   {
      if (messageType == CliMessageType.ERROR || messageType == CliMessageType.WARNING)
      {
         System.err.println(messageType.name() + ": " + msg);
      }
      else
      {
         System.out.println(messageType.name() + ": " + msg);
      }
   }
}
