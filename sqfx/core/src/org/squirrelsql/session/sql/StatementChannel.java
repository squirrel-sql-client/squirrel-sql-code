package org.squirrelsql.session.sql;

import javafx.application.Platform;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;

import java.sql.Statement;
import java.util.concurrent.*;

public class StatementChannel
{
   private boolean _isCanceled;
   private StateChannelListener _stateChannelListener;
   private Statement _cancelCandidate;

   private I18n _i18n = new I18n(getClass());
   private boolean _fireStateChangesToEventQueue = true;
   private StatementExecutionState _statementExecutionState;

   public void cancelStatement()
   {
      try
      {
         if(_isCanceled || _statementExecutionState == StatementExecutionState.FINSHED || _statementExecutionState == StatementExecutionState.ERROR)
         {
            return;
         }


         _isCanceled = true;
         setStatementExecutionState(StatementExecutionState.CANCELED);

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

   private void fireStateChange(StatementExecutionState executionState)
   {
      if (null != _stateChannelListener)
      {
         if (_fireStateChangesToEventQueue)
         {
            Platform.runLater(() -> _stateChannelListener.stateChanged(executionState));
         }
         else
         {
            _stateChannelListener.stateChanged(executionState);
         }
      }
   }

   public void setStateChannelListener(StateChannelListener stateChannelListener)
   {
      _stateChannelListener = stateChannelListener;
   }

   public void setStatementExecutionState(StatementExecutionState executionState)
   {
      _statementExecutionState = executionState;
      fireStateChange(executionState);
   }

   public StatementExecutionState getStatementExecutionState()
   {
      return _statementExecutionState;
   }

   public boolean isCanceled()
   {
      return _isCanceled;
   }

   public void setCancelCandidate(Statement cancelCandidate)
   {
      _cancelCandidate = cancelCandidate;
   }

   public void setFireStateChangesToEventQueue(boolean fireStateChangesToEventQueue)
   {
      _fireStateChangesToEventQueue = fireStateChangesToEventQueue;
   }
}
