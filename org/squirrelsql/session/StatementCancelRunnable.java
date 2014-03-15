package org.squirrelsql.session;

import org.squirrelsql.services.I18n;
import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;

import java.sql.Statement;

public class StatementCancelRunnable implements Runnable
{
   private Statement _statement;
   private boolean _reachedTimeout;
   private I18n _i18n = new I18n(getClass());


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
            mh.info(_i18n.t("session.tab.sql.executing.cancel.success.after.to", (end - begin)));
         }

      }
      catch (Throwable t)
      {
         if(_reachedTimeout)
         {
            mh.warning(_i18n.t("session.tab.sql.executing.cancel.failed.after.to"), t);
         }
         else
         {
            mh.warning(_i18n.t("session.tab.sql.executing.cancel.failed"), t);
         }
      }

   }

   public void reachedTimeout()
   {
      _reachedTimeout = true;
   }
}
