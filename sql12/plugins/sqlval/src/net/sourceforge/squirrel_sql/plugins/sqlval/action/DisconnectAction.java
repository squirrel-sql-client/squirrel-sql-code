package net.sourceforge.squirrel_sql.plugins.sqlval.action;
/*
 * Copyright (C) 2002 Colin Bell
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;

import net.sourceforge.squirrel_sql.plugins.sqlval.SQLValidatorPlugin;
import net.sourceforge.squirrel_sql.plugins.sqlval.WebServicePreferences;
import net.sourceforge.squirrel_sql.plugins.sqlval.WebServiceSessionProperties;
import net.sourceforge.squirrel_sql.plugins.sqlval.cmd.DisconnectCommand;
/**
 * This action will disconnect from the SQL Validation web service.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DisconnectAction extends SquirrelAction implements ISessionAction
{
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(DisconnectAction.class);

	/** Preferences. */
	private final WebServicePreferences _prefs;

	/** Current plugin. */
	private final SQLValidatorPlugin _plugin;

	/** Current session. */
	private ISession _session;

	/**
	 * Ctor.
	 * 
	 * @param	app		Application API.
	 * @param	rsrc	Resources to build this action from.
	 * @param	prefs	Plugin preferences.
	 * @param	plugin	Plugin
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT>WebServicePreferences</TT> passed.
	 */
	public DisconnectAction(IApplication app, Resources rsrc,
									WebServicePreferences prefs,
									SQLValidatorPlugin plugin)
	{
		super(app, rsrc);
		if (prefs == null)
		{
			throw new IllegalArgumentException("WebServicePreferences == null");
		}
		_prefs = prefs;
		_plugin = plugin;
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (_session != null)
		{
			WebServiceSessionProperties wss = _plugin.getWebServiceSessionProperties(_session);
			try
			{
				new DisconnectCommand(_session, _prefs, wss).execute();
			}
			catch (BaseException ex)
			{
				_session.getApplication().showErrorDialog("Error closing SQL Validation web service", ex);
			}
		}
	}

	public void setSession(ISession session)
	{
		_session = session;
	}

}
