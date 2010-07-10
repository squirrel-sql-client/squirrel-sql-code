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
import javax.swing.event.TreeSelectionListener;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.IObjectTab;
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
	 * Add an expander for the specified object tree node type.
	 * 
	 * @param	dboType		Database object type.
	 * @param	expander	Expander called to add children to a parent node.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>DatabaseObjectType</TT> or
	 * 			<TT>INodeExpander</TT> thrown.
	 */
	public synchronized void addExpander(DatabaseObjectType dboType,
													INodeExpander expander)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("DatabaseObjectType == null");
		}
		if (expander == null)
		{
			throw new IllegalArgumentException("INodeExpander == null");
		}
		_session.getSessionSheet().getObjectTreePanel().addExpander(dboType, expander);
	}

	/**
	 * Add a tab to be displayed in the detail panel for the passed
	 * object tree node type.
	 * 
	 * @param	dboType		Database object type.
	 * @param	tab			Tab to be displayed.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown when a <TT>null</TT> <TT>DatabaseObjectType</TT> or
	 * 			<TT>IObjectTab</TT> passed.
	 */
	public void addDetailTab(DatabaseObjectType dboType,
									IObjectTab tab)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("DatabaseObjectType == null");
		}
		if (tab == null)
		{
			throw new IllegalArgumentException("IObjectTab == null");
		}
		_session.getSessionSheet().getObjectTreePanel().addDetailTab(dboType, tab);
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
		_session.getSessionSheet().getObjectTreePanel().addTreeModelListener(lis);
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
	 * Add a listener to the object tree for selection changes.
	 * 
	 * @param	lis		The <TT>TreeSelectionListener</TT> you want added.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>TreeSelectionListener</TT> passed.
	 */
	public void addTreeSelectionListener(TreeSelectionListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("TreeSelectionListener == null");
		}
		_session.getSessionSheet().getObjectTreePanel().addTreeSelectionListener(lis);
	}

	/**
	 * Remove a listener from the object tree for selection changes.
	 * 
	 * @param	lis		The <TT>TreeSelectionListener</TT> you want removed.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>TreeSelectionListener</TT> passed.
	 */
	public void removeTreeSelectionListener(TreeSelectionListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("TreeSelectionListener == null");
		}
		_session.getSessionSheet().getObjectTreePanel().removeTreeSelectionListener(lis);
	}

	/**
	 * Add an item to the popup menu for the specified node type.
	 * 
	 * @param	dboType		Database object type.
	 * @param	action		Action to add to menu.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>DatabaseObjectType</TT>
	 * 			<TT>Action</TT> thrown.
	 */
	public synchronized void addToPopup(DatabaseObjectType dboType, Action action)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("DatabaseObjectType == null");
		}
		if (action == null)
		{
			throw new IllegalArgumentException("Action == null");
		}
		_session.getSessionSheet().getObjectTreePanel().addToObjectTreePopup(
			dboType,
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
	 * Refresh the nodes currently selected in the object tree.
	 */
	public void refreshSelectedNodes()
	{
		_session.getSessionSheet().getObjectTreePanel().refreshSelectedNodes();
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
	 * Create a new <TT>DatabaseObjectType</TT>
	 * 
	 * @return	a new <TT>DatabaseObjectType</TT>
	 */
	public DatabaseObjectType createNewDatabaseObjectType()
	{
		return DatabaseObjectType.createNewDatabaseObjectType();
	}
}
