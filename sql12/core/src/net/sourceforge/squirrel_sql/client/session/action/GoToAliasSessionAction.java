package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.findaliases.AliasesUtil;
import net.sourceforge.squirrel_sql.client.session.ISession;

import java.awt.event.ActionEvent;

public class GoToAliasSessionAction extends SquirrelAction implements ISessionAction
{
	private ISession _session;

	public GoToAliasSessionAction()
	{
		super(Main.getApplication());
	}

	public void setSession(ISession session)
	{
		_session = session;
	}
	
	public void actionPerformed(ActionEvent evt)
	{
		if(null == _session)
		{
			return;
		}

		AliasesUtil.viewInAliasesDockWidget(_session.getAlias());
	}
	
}
