package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.client.session.ISession;

public interface ICompletionCallback
{
   void connected(SQLConnection conn);
   void sessionCreated(ISession session);
   void errorOccured(Throwable th);
}
