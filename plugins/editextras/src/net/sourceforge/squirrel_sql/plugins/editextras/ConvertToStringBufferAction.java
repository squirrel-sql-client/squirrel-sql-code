package net.sourceforge.squirrel_sql.plugins.editextras;
/*
 * Copyright (C) 2003 Gerd Wagner
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

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
/**
 * This action will convert the SQL string to a StringBuffer.
 *
 * @author  Gerd Wagner
 */
class ConvertToStringBufferAction extends SquirrelAction
					implements ISessionAction
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(ConvertToStringBufferAction.class);

	/** Current session. */
	private ISession _session;

	private EditExtrasPlugin _plugin;

	ConvertToStringBufferAction(IApplication app, EditExtrasPlugin plugin)
	{
		super(app, plugin.getResources());
		_plugin = plugin;
	}

	public void setSession(ISession session)
	{
		_session = session;
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (_session != null)
		{
			try
			{

				//new ConvertToStringBufferCommand(_session.getSQLPanelAPI(_plugin)).execute();
				new ConvertToStringBufferCommand(FrameWorkAcessor.getSQLPanelAPI(_session, _plugin)).execute();
			}
			catch (Throwable ex)
			{
				final String msg = "Error processing Convert ot StringBuffer command";
				_session.getMessageHandler().showErrorMessage(msg + ": " + ex);
				s_log.error(msg, ex);
			}
		}
	}

}
