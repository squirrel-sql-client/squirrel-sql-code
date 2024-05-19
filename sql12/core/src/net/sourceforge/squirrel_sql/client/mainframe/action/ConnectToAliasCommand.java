package net.sourceforge.squirrel_sql.client.mainframe.action;
/*
 * Copyright (C) 2001-2004 Colin Bell and Johan Compagner
 * colbell@users.sourceforge.net
 * jcompagner@j-com.nl
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

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.ConnectCompletionCallback;
import net.sourceforge.squirrel_sql.client.gui.db.ConnectToAliasCallBack;
import net.sourceforge.squirrel_sql.client.gui.db.ConnectionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.mainframe.action.openconnection.OpenConnectionCommand;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This command is used to start Sessions and to test connections.
 * It delegates to {@link OpenConnectionCommand}.
 */
public class ConnectToAliasCommand
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ConnectToAliasCommand.class);

	private static final ILogger s_log = LoggerController.createLogger(ConnectToAliasCommand.class);

	/** The <TT>SQLAlias</TT> to connect to. */
	private SQLAlias _sqlAlias;

	/** If <TT>true</TT> a session is to be created as well as connecting to database. */
	private boolean _createSession;

	/** Callback to notify client on the progress of this command. */
	private ConnectCompletionCallback _callback;

	/**
	 * Ctor. This ctor will create a new session as well as opening a connection.
	 *
	 * @param	alias	The <TT>SQLAlias</TT> to connect to.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>IApplication</TT> or
	 *			<TT>SQLAlias</TT> passed.
	 */
	public ConnectToAliasCommand(SQLAlias sqlAlias)
	{
		this(sqlAlias, true, null);
	}

	/**
	 * Ctor.
	 *
	 * @param	alias			The <TT>SQLAlias</TT> to connect to.
	 * @param	createSession	If <TT>true</TT> then create a session as well
	 *							as connecting to the database.
	 * @param	callback		Callback for client code to be informed of the
	 *							progress of this command.
	 *
	 *			Thrown if a <TT>null</TT> <TT>IApplication</TT> or
	 *			<TT>SQLAlias</TT> passed.
	 */
	public ConnectToAliasCommand(SQLAlias sqlAlias, boolean createSession, ConnectCompletionCallback callback)
	{
		if (sqlAlias == null)
		{
			throw new IllegalArgumentException("Null SQLAlias passed");
		}
		_sqlAlias = sqlAlias;
		_createSession = createSession;
		_callback = callback != null ? callback : new ConnectToAliasCallBack(_sqlAlias);
	}

	/**
	 * Display connection internal frame.
    */
	public void executeConnect()
	{
		try
		{
			if (_createSession)
			{
				Main.getApplication().getWindowManager().getRecentAliasesListCtrl().startingCreateSession(_sqlAlias);
			}

			GUIUtils.processOnSwingEventThread(() -> createConnectionInternalFrame());
		}
		catch (Exception ex)
		{
			Main.getApplication().showErrorDialog(ex);
		}
	}

	private void createConnectionInternalFrame()
	{
		ConnectionInternalFrame sheet = new ConnectionInternalFrame(_sqlAlias, _createSession, _callback);
		Main.getApplication().getMainFrame().addWidget(sheet);
		DialogWidget.centerWithinDesktop(sheet);
		sheet.moveToFront();
	}
}
