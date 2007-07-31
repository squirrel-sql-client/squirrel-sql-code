package net.sourceforge.squirrel_sql.client.session;

import java.util.HashMap;

import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessorFactory;

public class MockSQLEntryPanelFactory implements ISQLEntryPanelFactory {

	MockSQLEntryPanel panel = null;
	
	public MockSQLEntryPanelFactory() {
		
	}
	
	public ISQLEntryPanel createSQLEntryPanel(ISession session) {
        panel = new MockSQLEntryPanel(session);
        return panel;
	}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory#createSQLEntryPanel(net.sourceforge.squirrel_sql.client.session.ISession, java.util.HashMap)
     */
    public ISQLEntryPanel createSQLEntryPanel(ISession session, 
                                              HashMap<String, IParserEventsProcessorFactory> props) {
        return createSQLEntryPanel(session);
    }

}
