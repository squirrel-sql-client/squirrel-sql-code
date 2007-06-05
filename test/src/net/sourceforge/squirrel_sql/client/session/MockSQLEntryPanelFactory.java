package net.sourceforge.squirrel_sql.client.session;

import java.util.HashMap;

public class MockSQLEntryPanelFactory implements ISQLEntryPanelFactory {

	MockSQLEntryPanel panel = null;
	
	public MockSQLEntryPanelFactory() {
		panel = new MockSQLEntryPanel();
	}
	
	public ISQLEntryPanel createSQLEntryPanel(ISession session) {
		return panel;
	}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory#createSQLEntryPanel(net.sourceforge.squirrel_sql.client.session.ISession, java.util.HashMap)
     */
    public ISQLEntryPanel createSQLEntryPanel(ISession session, HashMap props) {
        return panel;
    }

}
