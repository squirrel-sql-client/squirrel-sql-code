package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2002-2004 Johan Compagner
 * jcompagner@j-com.nl
 *
 * Modifications Copyright (C) 2003-2004 Jason Height
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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
/**
 * @version 	$Id: DropSelectedTablesAction.java,v 1.8 2005-05-22 11:14:59 gerdwagner Exp $
 * @author		Johan Compagner
 */
public class DropSelectedTablesAction extends SquirrelAction
										implements IObjectTreeAction
{
	/** Title for confirmation dialog. */
	private static final String TITLE = "Dropping table(s)/view(s)";

	/** Message for confirmation dialog. */
	private static final String MSG = "Do you really want to drop the selected tables or views?";

	/** API for the current tree. */
	private IObjectTreeAPI _tree;

	/**
	 * @param	app	Application API.
	 */
	public DropSelectedTablesAction(IApplication app)
	{
		super(app);
	}

	/**
	 * Set the current object tree API.
	 *
	 * @param	tree	Current ObjectTree
	 */
	public void setObjectTree(IObjectTreeAPI tree)
	{
		_tree = tree;
	}

	/**
	 * Drop selected tables in the object tree.
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (_tree != null)
		{
			IDatabaseObjectInfo[] tables = _tree.getSelectedDatabaseObjects();
			ObjectTreeNode[] nodes = _tree.getSelectedNodes();
			if (tables.length > 0)
			{
				// JASON: Ideally this should center on the tree rather than the main panel
				if (Dialogs.showYesNo(getApplication().getMainFrame(), MSG, TITLE))
				{
					new DropTablesCommand(_tree.getSession(), tables).execute();
					_tree.removeNodes(nodes);
				}
			}
		}
	}
}
