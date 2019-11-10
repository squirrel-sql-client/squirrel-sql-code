package net.sourceforge.squirrel_sql.plugins.refactoring.commands;

import net.sourceforge.squirrel_sql.client.session.ISession;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * An SQLExecutionHandler that disables auto commit (if enabled) on the current SQLConnection and handles
 * commits and rollbacks of the transaction by itself.
 */
class NoAutoCommitCommandExecHandler extends CommandExecHandler
{
   protected boolean _origAutoCommit;
   private ISession _session;

   public NoAutoCommitCommandExecHandler(ISession session) throws SQLException
   {
      super(session);
      _session = session;

      _origAutoCommit = _session.getSQLConnection().getAutoCommit();
      _session.getSQLConnection().setAutoCommit(false);
   }

   public void sqlCloseExecutionHandler(ArrayList<String> errMsgs, String lastExecutedStatement)
   {
      super.sqlCloseExecutionHandler(errMsgs, lastExecutedStatement);
      if (_origAutoCommit)
      {
         if (exceptionEncountered())
         {
            _session.rollback();
         }
         else
         {
            _session.commit();
         }
         try
         {
            _session.getSQLConnection().setAutoCommit(true);
         }
         catch (SQLException e)
         {
            _session.showErrorMessage(e);
         }
      }
   }
}
