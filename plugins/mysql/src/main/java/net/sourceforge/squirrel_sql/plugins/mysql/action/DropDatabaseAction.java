package net.sourceforge.squirrel_sql.plugins.mysql.action;
/*
 * Copyright (C) 2003 Arun Kapilan.P
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

import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
/**
 * DropDatabaseAction.java
 *
 * Created on June 9, 2003, 1:55 PM
 *
 * @author Arun Kapilan.P
 */
public class DropDatabaseAction	extends SquirrelAction
								implements ISessionAction
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DropDatabaseAction.class);


	/** Title for confirmation dialog. */
	// i18n[mysql.droppingDBs=Dropping database(s)]
	private static final String TITLE = s_stringMgr.getString("mysql.droppingDBs");

	/** Message for confirmation dialog. */
	// i18n[mysql.sureDropping=Are you sure?]
	private static final String MSG = s_stringMgr.getString("mysql.sureDropping");

	/** Current session. */
	private ISession _session;

	/** Current plugin. */
	private final MysqlPlugin _plugin;

	public DropDatabaseAction(IApplication app, Resources rsrc, MysqlPlugin plugin)
	{
		super(app, rsrc);
		_plugin = plugin;
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (_session != null)
		{
			IPlugin plugin = _session.getApplication().getDummyAppPlugin();
			IObjectTreeAPI treeAPI = _session.getSessionInternalFrame().getObjectTreeAPI();
			IDatabaseObjectInfo[] dbs = treeAPI.getSelectedDatabaseObjects();
			ObjectTreeNode[] nodes = treeAPI.getSelectedNodes();
			if (dbs.length > 0)
			{
				if (Dialogs.showYesNo(_session.getSessionSheet(), MSG, TITLE))
				{
					try
					{
						new DropDatabaseCommand(_session, _plugin, dbs).execute();
						treeAPI.removeNodes(nodes);
					}
					catch (Throwable th)
					{
						_session.showErrorMessage(th);
					}
				}
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
