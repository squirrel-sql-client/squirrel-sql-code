package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2002 Colin Bell and Johan Compagner
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
import javax.swing.Action;
import javax.swing.event.TreeModelListener;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNodeType;
import net.sourceforge.squirrel_sql.client.session.objectstree.objectpanel.IObjectPanelTab;
/**
 * This class is the API through which plugins can work with the object tree.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class ObjectTreeAPI implements IObjectTreeAPI
{
	/** Session containing the object tree. */
	private IClientSession _session;

	/**
	 * Database object type for a "Table Type" node. Some examples
	 * are "TABLE", "SSYTEM TABLE", "VIEW" etc.
	 */
	private final DatabaseObjectType _tableGroupType;

	/**
	 * Database object type for a "Procedure Type" node. There is only one
	 * node of this type in the object tree and it says "PROCEDURE".
	 */
	private final DatabaseObjectType _procGroupType;

	/**
	 * Database object type for a "UDT" node. There is only one
	 * node of this type in the object tree and it says "UDT".
	 */
	private final DatabaseObjectType _udtGroupType;

	//	private int _lastUsedNodeType =
	//		ObjectTreeNode.IObjectTreeNodeType.LAST_USED_NODE_TYPE;

	/**
	 * Ctor specifying the session.
	 * 
	 * @param	session	<TT>ISession</TT> containing the object tree.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <T>null</TT> <TT>ISession</TT> passed.
	 */
	ObjectTreeAPI(IClientSession session)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		_session = session;

		// Create database object type objects to represent the "group" nodes
		// that we will have in the tree. E.G. one of the "group" nodes would be
		// the table type nodes such as "TABLE", "SYSTEM TABLE" etc.
		_tableGroupType = DatabaseObjectType.createNewDatabaseObjectType();
		_procGroupType = DatabaseObjectType.createNewDatabaseObjectType();
		_udtGroupType = DatabaseObjectType.createNewDatabaseObjectType();
	}

	/**
	 * Register an expander for the specified object tree node type.
	 * 
	 * @param	nodeType	Object Tree node type.
	 * @param	expander	Expander called to add children to a parent node.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>ObjectTreeNodeType</TT> or
	 * 			<TT>INodeExpander</TT> thrown.
	 */
	public synchronized void registerExpander(ObjectTreeNodeType nodeType,
													INodeExpander expander)
	{
		if (nodeType == null)
		{
			throw new IllegalArgumentException("ObjectTreeNodeType == null");
		}
		if (expander == null)
		{
			throw new IllegalArgumentException("INodeExpander == null");
		}
		_session.getSessionSheet().getObjectTreePanel().registerExpander(
											nodeType, expander);
	}

	/**
	 * Register a tab to be displayed in the detail panel for the passed
	 * object tree node type.
	 * 
	 * @param	nodeType	Node type.
	 * @param	tab			Tab to be displayed.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown when a <TT>null</TT> <TT>ObjectTreeNodeType</TT> or
	 * 			<TT>IObjectPanelTab</TT> passed.
	 */
	public void registerDetailTab(ObjectTreeNodeType nodeType,
									IObjectPanelTab tab)
	{
		if (nodeType == null)
		{
			throw new IllegalArgumentException("ObjectTreeNodeType == null");
		}
		if (tab == null)
		{
			throw new IllegalArgumentException("IObjectPanelTab == null");
		}
		_session.getSessionSheet().getObjectTreePanel().registerDetailTab(
			nodeType,
			tab);
	}

	/**
	 * Add a listener to the object tree for structure changes. I.E nodes
	 * added/removed.
	 * 
	 * @param	lis		The <TT>TreeModelListener</TT> you want added.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>TreeModelListener</TT> passed.
	 */
	public synchronized void addTreeModelListener(TreeModelListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("TreeModelListener == null");
		}
		_session.getSessionSheet().getObjectTreePanel().addTreeModelListener(
			lis);
	}

	/**
	 * Remove a structure changes listener from the object tree.
	 * 
	 * @param	lis		The <TT>TreeModelListener</TT> you want removed.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>TreeModelListener</TT> passed.
	 */
	public synchronized void removeTreeModelListener(TreeModelListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("TreeModelListener == null");
		}
		_session.getSessionSheet().getObjectTreePanel().removeTreeModelListener(lis);
	}

	/**
	 * Add an item to the popup menu for the specified node type.
	 * 
	 * @param	nodeType	Object Tree node type.
	 * @param	action		Action to add to menu.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>ObjectTreeNodeType</TT>
	 * 			<TT>Action</TT> thrown.
	 */
	public synchronized void addToPopup(ObjectTreeNodeType nodeType, Action action)
	{
		if (nodeType == null)
		{
			throw new IllegalArgumentException("ObjectTreeNodeType == null");
		}
		if (action == null)
		{
			throw new IllegalArgumentException("Action == null");
		}
		_session.getSessionSheet().getObjectTreePanel().addToObjectTreePopup(
			nodeType,
			action);
	}

	/**
	 * Add an item to the popup menu for all node types.
	 * 
	 * @param	action		Action to add to menu.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>Action</TT> thrown.
	 */
	public synchronized void addToPopup(Action action)
	{
		if (action == null)
		{
			throw new IllegalArgumentException("Action == null");
		}
		_session.getSessionSheet().getObjectTreePanel().addToObjectTreePopup(
			action);
	}

	/**
	 * Return the next unused "ObjectTree node type". This can be used by
	 * pluigns to identify groups of nodes. I.E you may use this to identify
	 * all Oracle Consumer group nodes.
	 * 
	 * @return	Return the next unused "ObjectTree node type".
	 */
	//	public synchronized int getNextAvailableNodeype()
	//	{
	//		return ++_lastUsedNodeType;
	//	}

	/**
	 * Return an array of the selected nodes in the tree. This is guaranteed
	 * to be non-null.
	 * 
	 * @return	Array of nodes in the tree.
	 */
	public synchronized ObjectTreeNode[] getSelectedNodes()
	{
		return _session.getSessionSheet().getObjectTreePanel().getSelectedNodes();
	}

	/**
	 * Return an array of the currently selected database
	 * objects. This is guaranteed to be non-null.
	 *
	 * @return	array of <TT>ObjectTreeNode</TT> objects.
	 */
	public synchronized IDatabaseObjectInfo[] getSelectedDatabaseObjects()
	{
		return _session.getSessionSheet().getObjectTreePanel().getSelectedDatabaseObjects();
	}

	/**
	 * Refresh the object tree.
	 */
	public synchronized void refreshTree()
	{
		_session.getSessionSheet().getObjectTreePanel().refreshTree();
	}

	/**
	 * Remove one or more nodes from the tree.
	 * 
	 * @param	nodes	Array of nodes to be removed.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>ObjectTreeNode[]</TT> thrown.
	 */
	public synchronized void removeNodes(ObjectTreeNode[] nodes)
	{
		if (nodes == null)
		{
			throw new IllegalArgumentException("ObjectTreeNode[] == null");
		}
		_session.getSessionSheet().getObjectTreePanel().removeNodes(nodes);
	}

	/**
	 * Return the database object type for a "Table Type" node. Some examples
	 * are "TABLE", "SYSTEM TABLE", "VIEW" etc.
	 * 
	 * @return	The database object type.
	 */
	public DatabaseObjectType getTableGroupDatabaseObjectType()
	{
		return _tableGroupType;
	}

	/**
	 * Return the database object type for a "Procedure Type" node. There is
	 * only one node of this type in the object tree and it says "PROCEDURE".
	 * 
	 * @return	The database object type.
	 */
	public DatabaseObjectType getProcedureGroupType()
	{
		return _procGroupType;
	}

	/**
	 * Return the database object type for a "UDT" node. There is only one
	 * node of this type in the object tree and it says "UDT".
	 * 
	 * @return	The database object type.
	 */
	public DatabaseObjectType getUDTGroupType()
	{
		return _udtGroupType;
	}
}
