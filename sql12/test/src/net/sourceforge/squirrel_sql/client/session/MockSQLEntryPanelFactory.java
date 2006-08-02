package net.sourceforge.squirrel_sql.client.session;

public class MockSQLEntryPanelFactory implements ISQLEntryPanelFactory {

	MockSQLEntryPanel panel = null;
	
	public MockSQLEntryPanelFactory() {
		panel = new MockSQLEntryPanel();
	}
	
	public ISQLEntryPanel createSQLEntryPanel(ISession session) {
		return panel;
	}

}
