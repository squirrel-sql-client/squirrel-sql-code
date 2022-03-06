package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;

import java.awt.event.ActionEvent;


public class SessionOpenAction extends SquirrelAction implements ISessionAction
{
	private ISession _session;

	public SessionOpenAction(IApplication app)
	{
		super(app);
	}

	public void actionPerformed(ActionEvent e)
	{
		SavedSessionOpenCtrl savedSessionOpenCtrl = new SavedSessionOpenCtrl(_session);

		SavedSessionJsonBean savedSessionJsonBean = savedSessionOpenCtrl.getSelectedSavedSession();
	}

	@Override
	public void setSession(ISession session)
	{
		_session = session;

		setEnabled(null != _session);
	}
}
