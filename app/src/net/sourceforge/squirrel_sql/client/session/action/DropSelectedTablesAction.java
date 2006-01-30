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
import java.util.ArrayList;
import java.util.HashMap;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
/**
 * @version 	$Id: DropSelectedTablesAction.java,v 1.11 2006-01-30 00:25:13 manningr Exp $
 * @author		Johan Compagner
 */
public class DropSelectedTablesAction extends SquirrelAction
										implements IObjectTreeAction
{
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DropSelectedTablesAction.class);   
    
	/** Title for confirmation dialog. */
	private static final String TITLE = 
        s_stringMgr.getString("DropSelectedTablesAction.title");

	/** Message for confirmation dialog. */
	private static final String MSG = 
        s_stringMgr.getString("DropSelectedTablesAction.message");

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
      setEnabled(null != _tree);
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
                    DropTablesCommand command = 
                        new DropTablesCommand(_tree.getSession(), tables);
                    command.execute();
                    
                    if (command.getExceptionOccurred()) {
                        // build a list of nodes that were successfully dropped
                        // and should be removed from the object tree.
                        ArrayList tmp = new ArrayList();
                        HashMap resultMap = command.getResult();
                        for (int i = 0; i < nodes.length; i++) {
                            ObjectTreeNode node = nodes[i];
                            IDatabaseObjectInfo dbObjInfo = 
                                node.getDatabaseObjectInfo();
                            DropTableResult dtr = (DropTableResult)resultMap.get(dbObjInfo);
                            if (dtr.getResult()) {
                                tmp.add(node);
                            }
                        }
                        ObjectTreeNode[] nodesToRemove = 
                            (ObjectTreeNode[])tmp.toArray(new ObjectTreeNode[tmp.size()]);
                        _tree.removeNodes(nodesToRemove);                        
                    } else {
                        _tree.removeNodes(nodes);
                    }
                    
				}
			}
		}
	}
}
