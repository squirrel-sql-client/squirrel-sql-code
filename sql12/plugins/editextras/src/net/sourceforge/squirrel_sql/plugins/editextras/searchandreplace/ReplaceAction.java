package net.sourceforge.squirrel_sql.plugins.editextras.searchandreplace;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;

import net.sourceforge.squirrel_sql.plugins.editextras.EditExtrasPlugin;

public class ReplaceAction extends SquirrelAction implements ISessionAction
{
	private SearchAndReplaceController _ctrl;
	private EditExtrasPlugin _plugin;

	public ReplaceAction(IApplication app, Resources rsrc, EditExtrasPlugin plugin)
			throws IllegalArgumentException
	{
		super(app, rsrc);
		_plugin = plugin;
		setEnabled(false);
	}

	public void actionPerformed(ActionEvent evt)
	{
		if(null != _ctrl)
		{
      	_ctrl.show();
		}
	}

	public void setSession(ISession session)
	{
		if(null != session)
		{
			_ctrl = new SearchAndReplaceController(session, _plugin, SearchAndReplaceDlg.MODUS_REPLACE);
			setEnabled(true);
		}
		else
		{
			setEnabled(false);
			_ctrl = null;
		}
	}
}
