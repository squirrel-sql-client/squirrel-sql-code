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

package net.sourceforge.squirrel_sql.plugins.dbdiff.commands;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbdiff.DBDiffPlugin;

public class SelectCommand extends AbstractDiffCommand implements ICommand
{
	/**
	 * Current session.
	 */
	private final ISession _session;

	/**
	 * Current plugin.
	 */
	private final DBDiffPlugin _plugin;

	/** Logger for this class. */
	private final static ILogger log = LoggerController.createLogger(SelectCommand.class);

	/**
	 * Ctor specifying the current session.
	 */
	public SelectCommand(ISession session, DBDiffPlugin plugin)
	{
		super();
		_session = session;
		_plugin = plugin;
	}

	/**
	 * Execute this command. Save the session and selected objects in the plugin for use in paste command.
	 */
	public void execute()
	{
		final IObjectTreeAPI api = _session.getObjectTreeAPIOfActiveSessionWindow();
		if (api != null)
		{
			final IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();
			try
			{
				_plugin.setSourceSession(_session);
				_plugin.setSelectedDatabaseObjects(dbObjs);
				_plugin.setCompareMenuEnabled(true);
			}
			catch (final Exception e)
			{
				log.error("Unexected exception: ", e);
			}
		}
	}

}