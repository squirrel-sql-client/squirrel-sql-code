package org.squirrelsql.session;

import javafx.application.Platform;
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
               mh.info("Canceled statement");
            }
            catch (TimeoutException e)
            {
               cancelRunnable.reachedTimeout();
               mh.warning("Cancel did not succeed within one second. If cancel succeeds later you will get further messages");
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
