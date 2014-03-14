package org.squirrelsql.session;

import javafx.application.Platform;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StatementChannel
{
   private boolean _isCanceled;
   private StateChannelListener _stateChannelListener;
   private Statement _cancelCandidate;

   public void cancelStatement()
   {
      _isCanceled = true;
      Platform.runLater(() -> _stateChannelListener.stateChanged(StatementExecutionState.CANCELED));
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
