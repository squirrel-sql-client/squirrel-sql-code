package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2003 Colin Bell
 * colbell@users.sourceforge.net
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
import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IClientSession;
/**
 * This <TT>Action</TT> will display the previous results tab for the
 * current session.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class GotoPreviousResultsTabAction extends SquirrelAction
									implements IClientSessionAction
{
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(GotoPreviousResultsTabAction.class);

	/** Current session. */
	private IClientSession _session;

	/** Command that will be executed by this action. */
	private ICommand _cmd;

	/**
	 * Ctor specifying Application API.
	 * 
	 * @param	app	Application API.
	 */
	public GotoPreviousResultsTabAction(IApplication app)
	{
		super(app);
	}

	/**
	 * A new session has been activated.
	 * 
	 * @param	session	The new session.
	 */
	public void setClientSession(IClientSession session)
	{
		_session = session;
		_cmd = null;
	}

	/**
	 * Display the previous results tab.
	 * 
	 * @param	evt		Event being executed.
	 */
	public synchronized void actionPerformed(ActionEvent evt)
	{
		if (_session != null)
		{
			if (_cmd == null)
			{
				_cmd = new GotoPreviousResultsTabCommand(_session);
			}
			try
			{
				_cmd.execute();
			}
			catch (Throwable ex)
			{
				final String msg = "Error occured seting current results tab";
				_session.getMessageHandler().showErrorMessage(msg + ": " + ex);
				s_log.error(msg, ex);
			}
		}
	}
}
