package org.squirrelsql;

import java.io.PrintStream;

public class ExceptionHandler
{
   public static void handle(final Throwable t)
   {
      AppState.get().getStatusBarCtrl().error(t);
   }

   public static void initHandling()
   {
      final PrintStream origErr = System.err;
      System.setErr(new PrintStream(origErr)
      {
         @Override
         public void print(Object obj)
         {
            checkException(obj, origErr, false);
         }

         @Override
         public void println(Object obj)
         {
            checkException(obj, origErr, true);
         }
      }
      );
   }

   private static void checkException(Object obj, PrintStream origErr, boolean line)
   {
      if (obj instanceof Throwable)
      {
         Throwable t = (Throwable) obj;
         t.printStackTrace(origErr);
         handle(t);
      }
      else
      {
         if (line)
         {
            origErr.println(obj);
         }
         else
         {
            origErr.print(obj);
         }
      }
   }
}
