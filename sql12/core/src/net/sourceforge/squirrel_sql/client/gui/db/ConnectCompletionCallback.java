package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;

public interface ConnectCompletionCallback
{
   void connected(ISQLConnection conn);
   void sessionCreated(ISession session);
   void errorOccurred(Throwable th, boolean stopConnection);

   void sessionInternalFrameCreated(SessionInternalFrame sessionInternalFrame);
}
