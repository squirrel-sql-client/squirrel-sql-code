package net.sourceforge.squirrel_sql.plugins.editextras.searchandreplace;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.plugins.editextras.EditExtrasPlugin;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;

public class FindSelectedAction extends SquirrelAction implements ISessionAction
{
	private SearchAndReplaceController _ctrl;
	private EditExtrasPlugin _plugin;
	private ISession _session;

	public FindSelectedAction(IApplication app, Resources rsrc, EditExtrasPlugin plugin)
			throws IllegalArgumentException
	{
		super(app, rsrc);
		_plugin = plugin;
	}

	public void actionPerformed(ActionEvent evt)
	{
		if(null == _session)
		{
			return;
		}
		_plugin.getSearchAndReplaceKernel(_session).findSelected();
	}

	public void setSession(ISession session)
	{
		if(null != session)
		{
			_session = session;
		}
	}
}