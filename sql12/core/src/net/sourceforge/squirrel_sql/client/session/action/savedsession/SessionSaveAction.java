package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.session.IToolsPopupDescription;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.event.ActionEvent;


public class SessionSaveAction extends SquirrelAction implements ISessionAction, IToolsPopupDescription
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SessionSaveAction.class);

	private ISession _session;

	public SessionSaveAction(IApplication app)
	{
		super(app);
		setEnabled(false);
	}

	public void actionPerformed(ActionEvent e)
	{
		SessionPersister.saveSession(_session);
	}

	@Override
	public void setSession(ISession session)
	{
		_session = session;
		setEnabled(null != _session);
	}

	@Override
	public String getToolsPopupDescription()
	{
		return s_stringMgr.getString("SessionSaveAction.tools.popup.description");
	}
}
