package net.sourceforge.squirrel_sql.plugins.sqlscript;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;

public class SaveScriptAsAction extends SquirrelAction implements ISessionAction
{
	private SQLScriptPlugin _plugin;
	private SaveAndLoadScriptActionDelegate _delegate;

	public SaveScriptAsAction(IApplication app, Resources rsrc, SQLScriptPlugin plugin)
			throws IllegalArgumentException
	{
		super(app, rsrc);
		_plugin = plugin;
	}

	public void actionPerformed(ActionEvent evt)
	{
		_delegate.actionPerformed(getParentFrame(evt), evt, true);
	}

	public void setSession(ISession session)
	{
		if(null != session)
		{
			_delegate = _plugin.getLoadAndSaveDelegate(session);
		}
	}
}
