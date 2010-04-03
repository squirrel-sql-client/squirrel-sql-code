package net.sourceforge.squirrel_sql.client.session;

import java.util.HashMap;

public class MockSQLEntryPanelFactory implements ISQLEntryPanelFactory
{

	MockSQLEntryPanel panel = null;

	public MockSQLEntryPanelFactory()
	{

	}

	/**
	 * @param session
	 * @return
	 */
	public ISQLEntryPanel createSQLEntryPanel(ISession session)
	{
		panel = new MockSQLEntryPanel(session);
		return panel;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory#createSQLEntryPanel(net.sourceforge.squirrel_sql.client.session.ISession, java.util.HashMap)
	 */
	public ISQLEntryPanel createSQLEntryPanel(ISession session, HashMap<String, Object> props)
	{
		return createSQLEntryPanel(session);
	}

}
