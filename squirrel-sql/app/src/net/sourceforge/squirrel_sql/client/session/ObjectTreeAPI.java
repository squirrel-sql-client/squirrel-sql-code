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

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
/**
 * This class is the API through which plugins can work with the object tree.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class ObjectTreeAPI implements IObjectTreeAPI
{
	/** Session containing the object tree. */
	private IClientSession _session;

	private int _lastUsedNodeType =
		ObjectTreeNode.IObjectTreeNodeType.LAST_USED_NODE_TYPE;

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
	}

	/**
	 * Register an expander for the specified object tree node type.
	 * 
	 * @param	nodeType	Object Tree node type.
	 *						@see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode.IObjectTreeNodeType
	 * @param	expander	Expander called to add children to a parent node.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>INodeExpander</TT> thrown.
	 */
	public synchronized void registerExpander(int nodeType, INodeExpander expander)
	{
		if (expander == null)
		{
			throw new IllegalArgumentException("INodeExpander == null");
		}
		_session.getSessionSheet().getObjectTreePanel().registerExpander(nodeType, expander);
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
	 * Add an item to the popup menu for the specified node type.
	 * 
	 * @param	nodeType	Object Tree node type.
	 *						@see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode.IObjectTreeNodeType
	 * @param	action		Action to add to menu.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>Action</TT> thrown.
	 */
	public synchronized void addToPopup(int nodeType, Action action)
	{
		if (action == null)
		{
			throw new IllegalArgumentException("Action == null");
		}
		_session.getSessionSheet().getObjectTreePanel().addToObjectTreePopup(nodeType, action);
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
		_session.getSessionSheet().getObjectTreePanel().addToObjectTreePopup(action);
	}

	/**
	 * Return the next unused "ObjectTree node type". This can be used by
	 * pluigns to identify groups of nodes. I.E you may use this to identify
	 * all Oracle Consumer group nodes.
	 * 
	 * @return	Return the next unused "ObjectTree node type".
	 */
	public synchronized int getNextAvailableNodeype()
	{
		return ++_lastUsedNodeType;
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
}
