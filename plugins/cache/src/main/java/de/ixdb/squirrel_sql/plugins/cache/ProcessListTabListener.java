package de.ixdb.squirrel_sql.plugins.cache;

import net.sourceforge.squirrel_sql.client.session.ISession;

public interface ProcessListTabListener
{
   ProcessData[] refreshRequested(ISession session);

   int terminateRequested(ISession session, ProcessData processData);

   void closeRequested(ISession session);
}
