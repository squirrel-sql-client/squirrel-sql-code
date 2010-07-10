package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2002 Johan Compagner
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
import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.IClientSession;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;

/**
 * @version 	$Id: DropSelectedTablesAction.java,v 1.5 2002-09-20 09:13:10 colbell Exp $
 * @author		Johan Compagner
 */
public class DropSelectedTablesAction extends SquirrelAction
										implements IClientSessionAction
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(DropSelectedTablesAction.class);

	/** Title for confirmation dialog. */
	private static final String TITLE = "Dropping table(s)";

	/** Message for confirmation dialog. */
	private static final String MSG = "Are you sure?";

	/** Current session. */
	private IClientSession _session;

	/**
	 * @param	app	Application API.
	 */
	public DropSelectedTablesAction(IApplication app)
	{
		super(app);
	}

	public void setClientSession(IClientSession session)
	{
		_session = session;
	}

	/**
	 * Drop selected tables in the object tree.
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (_session != null)
		{
			IPlugin plugin = _session.getApplication().getDummyAppPlugin();
			IObjectTreeAPI treeAPI = _session.getObjectTreeAPI(plugin);
			IDatabaseObjectInfo[] tables = treeAPI.getSelectedDatabaseObjects();
//			ObjectTreeNode[] nodes = treeAPI.getSelectedNodes();
			if (tables.length > 0)
			{
				if (Dialogs.showYesNo(_session.getSessionSheet(), MSG, TITLE))
				{
					new DropTablesCommand(_session, tables).execute();
					treeAPI.removeNodes(treeAPI.getSelectedNodes());
				}
			}
		}
	}
}