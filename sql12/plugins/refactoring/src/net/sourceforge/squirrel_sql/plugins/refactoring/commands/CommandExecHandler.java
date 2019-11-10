package net.sourceforge.squirrel_sql.plugins.refactoring.commands;

import net.sourceforge.squirrel_sql.client.session.DefaultSQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.ISession;

class CommandExecHandler extends DefaultSQLExecuterHandler
{
   protected boolean exceptionEncountered = false;

   public CommandExecHandler(ISession session)
   {
      super(session);
   }

   public String sqlExecutionException(Throwable th, String postErrorString)
   {
      super.sqlExecutionException(th, postErrorString);
      exceptionEncountered = true;

      return postErrorString;
   }

   public boolean exceptionEncountered()
   {
      return exceptionEncountered;
   }
}
