package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

public class MockSessionManager extends SessionManager {

	ISession session = null;
	
	public MockSessionManager(IApplication app) {
		super(app);
	}

	public void setSession(ISession aSession) {
		session = aSession;
	}
	
	public ISession getSession(IIdentifier sessionID) {
		return session;
	}
	

	
}
