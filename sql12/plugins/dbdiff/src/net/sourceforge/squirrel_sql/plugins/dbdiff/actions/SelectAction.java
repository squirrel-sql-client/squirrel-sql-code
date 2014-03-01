/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package net.sourceforge.squirrel_sql.plugins.dbdiff.actions;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbdiff.DBDiffPlugin;
import net.sourceforge.squirrel_sql.plugins.dbdiff.commands.SelectCommand;

public class SelectAction extends AbstractDiffAction implements ISessionAction
{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(SelectAction.class);

	/** Current session. */
	private ISession _session;

	/** Current plugin. */
	private final DBDiffPlugin _plugin;

	public SelectAction(IApplication app, Resources rsrc, DBDiffPlugin plugin)
	{
		super(app, rsrc);
		_plugin = plugin;
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (_session != null)
		{
			final SelectCommand command = new SelectCommand(_session, _plugin);
			command.setPluginPreferencesManager(pluginPreferencesManager);
			command.execute();
		}
		else
		{
			if (s_log.isInfoEnabled()) {
				s_log.info("actionPerformed: session was null; Skipping select command execution");
			}
		}
	}

	/**
	 * Set the current session.
	 * 
	 * @param session
	 *           The current session. This is allowed to be null in the case where a session is closing and
	 *           references to it should be removed.
	 */
	public void setSession(ISession session)
	{
		_session = session;
	}

}