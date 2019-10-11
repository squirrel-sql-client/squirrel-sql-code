/*
 * Copyright (C) 2002-2004 Colin Bell
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
 * License along with this library; if not, write toS the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.TreePath;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This class actually loads the tree.
 * Note: This class was extracted from {@link ObjectTree} to make it more testable.
 */
public class TreeLoader
{
	private static final ILogger s_log = LoggerController.createLogger(TreeLoader.class);
		
	private ObjectTree _objectTree;
	private ObjectTreeNode _parentNode;
	private INodeExpander[] _expanders;
	private ObjectTreeModel _model;
	private ISession _session;

	TreeLoader(ISession session, ObjectTree objectTree, ObjectTreeModel model, ObjectTreeNode parentNode, INodeExpander[] expanders)
	{
		this._session = session;
		this._objectTree = objectTree;
		this._model = model;
		_parentNode = parentNode;
		_expanders = expanders;
	}

	void execute()
	{
		try
		{
			try
			{
				loadChildren();
			}
			finally
			{
//				if (_selectParentNode)
//				{
//					this._objectTree.clearSelection();
//					this._objectTree.setSelectionPath(new TreePath(_parentNode.getPath()));
//				}
				_model.nodeStructureChanged(_parentNode);
			}
		}
		catch (Throwable ex)
		{
			final String msg = "Error: " + _parentNode.toString();
			s_log.error(msg, ex);
			this._session.showErrorMessage(msg + ": " + ex.toString());
		}
	}

	/**
	 * This expands the parent node and shows all its children.
	 */
	private void loadChildren() throws SQLException
	{
		boolean noChildrenFound = true;
		for (int i = 0; i < _expanders.length; ++i)
		{
			boolean nodeTypeAllowsChildren = false;
			DatabaseObjectType lastDboType = null;
			List<ObjectTreeNode> list = _expanders[i].createChildren(this._session, _parentNode);
			
			if(list.isEmpty() == false){
				noChildrenFound = false;
			}
			
			Iterator<ObjectTreeNode> it = list.iterator();
			while (it.hasNext())
			{
				Object nextObj = it.next();
				if (nextObj instanceof ObjectTreeNode)
				{
					ObjectTreeNode childNode = (ObjectTreeNode)nextObj;
					if (childNode.getExpanders().length >0)
					{
						childNode.setAllowsChildren(true);
					}
					else
					{
						DatabaseObjectType childNodeDboType = childNode.getDatabaseObjectType();
						if (childNodeDboType != lastDboType)
						{
							this._objectTree.getTypedModel().addKnownDatabaseObjectType(childNodeDboType);
							lastDboType = childNodeDboType;
							if (this._model.getExpanders(childNodeDboType).length > 0)
							{
								nodeTypeAllowsChildren = true;
							}
							else
							{
								nodeTypeAllowsChildren = false;
							}
						}
						childNode.setAllowsChildren(nodeTypeAllowsChildren);
					}
					_parentNode.add(childNode);
				}
			}
		}

		// We can't use getChildCount, because the node has a temporary child called "Loading..."
		_parentNode.setNoChildrenFoundWithExpander(noChildrenFound);
	}

}