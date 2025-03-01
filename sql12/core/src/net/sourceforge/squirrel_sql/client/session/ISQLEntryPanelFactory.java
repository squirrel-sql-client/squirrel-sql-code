package net.sourceforge.squirrel_sql.client.session;

import java.util.HashMap;

public interface ISQLEntryPanelFactory
{
	ISQLEntryPanel createSQLEntryPanel(ISession session, HashMap<String, Object> props);

   void sessionEnding(ISession session);
}
