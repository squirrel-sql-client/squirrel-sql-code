package org.squirrelsql.session.sql;

public interface StateChannelListener
{
   void stateChanged(StatementExecutionState statementExecutionState);
}
