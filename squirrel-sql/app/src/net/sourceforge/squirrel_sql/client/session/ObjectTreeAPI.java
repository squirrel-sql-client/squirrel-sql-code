package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2002 Colin Bell
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
import javax.swing.Action;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

import net.sourceforge.squirrel_sql.client.session.IClientSession;
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
	public void registerExpander(int nodeType, INodeExpander expander)
	{
		if (expander == null)
		{
			throw new IllegalArgumentException("INodeExpander == null");
		}
		_session.getSessionSheet().getObjectTreePanel().registerExpander(nodeType, expander);
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
	public void addToPopup(int nodeType, Action action)
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
	public void addToPopup(Action action)
	{
		if (action == null)
		{
			throw new IllegalArgumentException("Action == null");
		}
		_session.getSessionSheet().getObjectTreePanel().addToObjectTreePopup(action);
	}

	/**
	 * Return an array of the selected nodes in the tree. This is guaranteed
	 * to be non-null.
	 * 
	 * @return	Array of nodes in the tree.
	 */
	public ObjectTreeNode[] getSelectedNodes()
	{
		return _session.getSessionSheet().getObjectTreePanel().getSelectedNodes();
	}

	/**
	 * Return an array of the currently selected database
	 * objects. This is guaranteed to be non-null.
	 *
	 * @return	array of <TT>ObjectTreeNode</TT> objects.
	 */
	public IDatabaseObjectInfo[] getSelectedDatabaseObjects()
	{
		return _session.getSessionSheet().getObjectTreePanel().getSelectedDatabaseObjects();
	}

	/**
	 * Refresh the object tree.
	 */
	public void refresh()
	{
		//TODO: Write me
	}
}
