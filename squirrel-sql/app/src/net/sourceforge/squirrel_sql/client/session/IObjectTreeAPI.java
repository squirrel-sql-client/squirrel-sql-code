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
 * This interface defines the API through which plugins can work with the object
 * tree.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface IObjectTreeAPI
{
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
	void registerExpander(ObjectTreeNodeType nodeType, INodeExpander expander);

	/**
	 * Register a tab to be displayed in the detail panel for the passed
	 * object tree node type.
	 * 
	 * @param	nodeType	Node type.
	 * @param	tab			Tab to be displayed.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown when a <TT>null</TT> <TT>ObjectTreeNodeType</TT> or
	 *			<TT>IObjectPanelTab</TT> passed.
	 */
	void registerDetailTab(ObjectTreeNodeType nodeType, IObjectPanelTab tab);

	/**
	 * Add a listener to the object tree for structure changes. I.E nodes
	 * added/removed.
	 * 
	 * @param	lis		The <TT>TreeModelListener</TT> you want added.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>TreeModelListener</TT> passed.
	 */
	void addTreeModelListener(TreeModelListener lis);

	/**
	 * Remove a structure changes listener from the object tree.
	 * 
	 * @param	lis		The <TT>TreeModelListener</TT> you want removed.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>TreeModelListener</TT> passed.
	 */
	void removeTreeModelListener(TreeModelListener lis);

	/**
	 * Add an item to the popup menu for the specified node type.
	 * 
	 * @param	nodeType	Object Tree node type.
	 * @param	action		Action to add to menu.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>ObjectTreeNodeType</TT> or
	 * 			<TT>Action</TT> thrown.
	 */
	void addToPopup(ObjectTreeNodeType nodeType, Action action);

	/**
	 * Add an item to the popup menu for all node types.
	 * 
	 * @param	action		Action to add to menu.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>Action</TT> thrown.
	 */
	void addToPopup(Action action);

	/**
	 * Return the next unused "ObjectTree node type". This can be used by
	 * pluigns to identify groups of nodes. I.E you may use this to identify
	 * all Oracle Consumer group nodes.
	 * 
	 * @return	Return the next unused "ObjectTree node type".
	 */
	//	int getNextAvailableNodeype();

	/**
	 * Return an array of the selected nodes in the tree. This is guaranteed
	 * to be non-null.
	 * 
	 * @return	Array of nodes in the tree.
	 */
	ObjectTreeNode[] getSelectedNodes();

	/**
	 * Return an array of the currently selected database
	 * objects. This is guaranteed to be non-null.
	 *
	 * @return	array of <TT>ObjectTreeNode</TT> objects.
	 */
	IDatabaseObjectInfo[] getSelectedDatabaseObjects();

	/**
	 * Refresh the object tree.
	 */
	void refreshTree();

	/**
	 * Remove one or more nodes from the tree.
	 * 
	 * @param	nodes	Array of nodes to be removed.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>ObjectTreeNode[]</TT> thrown.
	 */
	void removeNodes(ObjectTreeNode[] nodes);

	/**
	 * Return the database object type for a "Table Type" node. Some examples
	 * are "TABLE", "SYSTEM TABLE", "VIEW" etc.
	 * 
	 * @return	The database object type.
	 */
	DatabaseObjectType getTableGroupDatabaseObjectType();

	/**
	 * Return the database object type for a "Procedure Type" node. There is
	 * only one node of this type in the object tree and it says "PROCEDURE".
	 * 
	 * @return	The database object type.
	 */
	DatabaseObjectType getProcedureGroupType();

	/**
	 * Return the database object type for a "UDT" node. There is only one
	 * node of this type in the object tree and it says "UDT".
	 * 
	 * @return	The database object type.
	 */
	DatabaseObjectType getUDTGroupType();
}
