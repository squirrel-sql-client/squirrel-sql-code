package org.squirrelsql;

import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;

import java.io.PrintStream;

public class ExceptionHandler
{
   public static void handle(final Throwable t)
   {
      MessageHandler mh = new MessageHandler(ExceptionHandler.class, MessageHandlerDestination.MESSAGE_LOG);
      mh.error(t);
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
