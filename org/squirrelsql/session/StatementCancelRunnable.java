package org.squirrelsql.session;

import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;

public class StatementCancelRunnable implements Runnable
{
   private Statement _statement;
   private boolean _reachedTimeout;

   public StatementCancelRunnable(Statement statement)
   {
      _statement = statement;
   }

   @Override
   public void run()
   {
      MessageHandler mh = new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_PANEL);
      try
      {
         long begin = System.currentTimeMillis();
         _statement.cancel();
         long end = System.currentTimeMillis();

         if(_reachedTimeout)
         {
            mh.info("Canceling statement succeeded after timeout. Canceling took " + (end - begin) + " millis");
         }

      }
      catch (Throwable t)
      {
         if(_reachedTimeout)
         {
            mh.warning("Canceling statement failed after timeout", t);
         }
         else
         {
            mh.warning("Canceling statement failed", t);
         }
      }

   }

   public void reachedTimeout()
   {
      _reachedTimeout = true;
   }
}
