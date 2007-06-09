package net.sourceforge.squirrel_sql.plugins.mysql.action;
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

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;

import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;
/**
 * This <TT>Action</TT> will allow the user to rename the currently selected
 * table.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class RenameTableAction extends SquirrelAction
								implements ISessionAction
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(RenameTableAction.class);

	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(RenameTableAction.class);

	/** Current session. */
	private ISession _session;

	/** Current plugin. */
	private final MysqlPlugin _plugin;

	public RenameTableAction(IApplication app, Resources rsrc,MysqlPlugin plugin)
	{
		super(app, rsrc);
		_plugin = plugin;
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (_session != null)
		{
			final IObjectTreeAPI treeAPI = _session.getSessionInternalFrame().getObjectTreeAPI();
			final IDatabaseObjectInfo[] tables = treeAPI.getSelectedDatabaseObjects();
			if (tables.length == 1)
			{
				final ITableInfo ti = (ITableInfo)tables[0];
				final String msg = s_stringMgr.getString("RenameTableAction.newnameprompt", ti.getQualifiedName());
				final String title = s_stringMgr.getString("RenameTableAction.rename");
				final String newTableName = JOptionPane.showInputDialog(null, msg, title, JOptionPane.QUESTION_MESSAGE);
				if (newTableName != null && newTableName.length() > 0)
				{
					try
					{
						new RenameTableCommand(_session, _plugin, ti, newTableName).execute();
					}
					catch (Throwable th)
					{
						_session.showErrorMessage(th);
						s_log.error("Error occured renaming table", th);
					}
				}
			}
			else
			{
				// i18n[mysql.selectSingleTable=Must select a single table]
				_session.getApplication().showErrorDialog(s_stringMgr.getString("mysql.selectSingleTable"));
			}
		}
	}

	/**
	 * Set the current session.
	 *
	 * @param	session		The current session.
	 */
	public void setSession(ISession session)
	{
		_session = session;
	}
}
