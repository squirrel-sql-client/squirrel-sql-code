package net.sourceforge.squirrel_sql.plugins.sqlscript;

import net.sourceforge.squirrel_sql.client.session.ISession;

import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.File;

public class SaveAndLoadScriptActionDelegate
{
	private ISession _session;
	private SQLScriptPlugin _plugin;
	private SaveScriptCommand _saveScriptCommand;

	public SaveAndLoadScriptActionDelegate(SQLScriptPlugin plugin)
			throws IllegalArgumentException
	{
		if (plugin == null)
		{
			throw new IllegalArgumentException("null IPlugin passed");
		}

		_plugin = plugin;
	}

	public void actionPerformed(Frame parentFrame, ActionEvent evt, boolean newFile)
	{
		if (_session != null)
		{
			_saveScriptCommand.execute(parentFrame, newFile);
		}
	}

	public void setSession(ISession session)
	{
		_session = session;
		if(null != _session)
		{
			_saveScriptCommand = new SaveScriptCommand(_session, _plugin);
		}
	}

	public void setLoadedFile(File file)
	{
		_saveScriptCommand.setLoadedFile(file);
	}

}
