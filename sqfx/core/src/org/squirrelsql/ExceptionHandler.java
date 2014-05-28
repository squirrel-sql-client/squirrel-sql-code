package org.squirrelsql;

import javafx.application.Platform;
import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;

public class ExceptionHandler
{

   public static void initHandling()
   {
      if(false == Platform.isFxApplicationThread())
      {
         throw new IllegalStateException("Must be on called on FxApplicationThread");
      }

      Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler()
      {
         @Override
         public void uncaughtException(Thread t, Throwable e)
         {
            onUncaughtException(e);
         }
      });
   }

   private static void onUncaughtException(Throwable e)
   {
      if(Platform.isFxApplicationThread())
      {
         handle(e);
      }

      Platform.runLater(() -> handle(e));
   }

   public static void handle(final Throwable t)
   {
      MessageHandler mh = new MessageHandler(ExceptionHandler.class, MessageHandlerDestination.MESSAGE_LOG);
      mh.error(t);
   }


}
