package net.sourceforge.squirrel_sql.plugins.sqlval.cmd;
/*
 * Copyright (C) 2002 Colin Bell
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
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.session.ISession;

import net.sourceforge.squirrel_sql.plugins.sqlval.SQLValidatorPlugin;
import net.sourceforge.squirrel_sql.plugins.sqlval.WebServicePreferences;
import net.sourceforge.squirrel_sql.plugins.sqlval.WebServiceSession;
import net.sourceforge.squirrel_sql.plugins.sqlval.WebServiceSessionProperties;
/**
 * This <CODE>ICommand</CODE> will connect to the SQL Validation web service.
 *
 * @author	<A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ConnectCommand implements ICommand
{
	private final ISession _session;
	private final WebServicePreferences _prefs;
	private final WebServiceSessionProperties _sessionProps;

	public ConnectCommand(ISession session, WebServicePreferences prefs,
							WebServiceSessionProperties sessionProps)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		if (prefs == null)
		{
			throw new IllegalArgumentException("WebServicePreferences == null");
		}
		if (sessionProps == null)
		{
			throw new IllegalArgumentException("WebServiceSessionProperties == null");
		}

		_session = session;
		_prefs = prefs;
		_sessionProps = sessionProps;
	}

	/**
	 * Connect to the web service.
	 */
	public void execute() throws BaseException
	{
		try
		{
			final WebServiceSession wss = _sessionProps.getWebServiceSession();
			if (!wss.isOpen())
			{
				wss.open();
				_session.getMessageHandler().showMessage("Connected to the SQL Validation web service");
			}
		}
		catch (Throwable th)
		{
			throw new BaseException(th);
		}
	}

}
