package org.squirrelsql.session.sql;

import javafx.application.Platform;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.*;

public class StatementChannel
{
   private boolean _isCanceled;
   private StateChannelListener _stateChannelListener;
   private Statement _cancelCandidate;

   private I18n _i18n = new I18n(getClass());

   public void cancelStatement()
   {
      try
      {
         _isCanceled = true;
         Platform.runLater(() -> _stateChannelListener.stateChanged(StatementExecutionState.CANCELED));

         Statement cancelCandidateBuf = _cancelCandidate;

         if(null != cancelCandidateBuf)
         {
            StatementCancelRunnable cancelRunnable = new StatementCancelRunnable(cancelCandidateBuf);
            Future<?> submit = Executors.newSingleThreadExecutor().submit(cancelRunnable);

            MessageHandler mh = new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_PANEL);

            try
            {
               submit.get(1000, TimeUnit.MILLISECONDS);
               mh.info(_i18n.t("session.tab.sql.executing.cancel.success"));
            }
            catch (TimeoutException e)
            {
               cancelRunnable.reachedTimeout();
               mh.warning(_i18n.t("session.tab.sql.executing.cancel.unsuccess"));
            }
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public void setStateChannelListener(StateChannelListener stateChannelListener)
   {
      _stateChannelListener = stateChannelListener;
   }

   public void setStatementExecutionState(StatementExecutionState executing)
   {
      Platform.runLater(() -> _stateChannelListener.stateChanged(executing));
   }

   public boolean isCanceled()
   {
      return _isCanceled;
   }

   public void setCancelCandidate(Statement cancelCandidate)
   {
      _cancelCandidate = cancelCandidate;
   }
}
