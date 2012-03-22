package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.client.session.ISession;

public interface ICompletionCallback
{
   void connected(ISQLConnection conn);
   void sessionCreated(ISession session);
   void errorOccured(Throwable th, boolean stopConnection);
}
