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
 * This interface defines the API through which plugins can work with the object
 * tree.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface IObjectTreeAPI
{
	/**
	 * Database object type for a "Table Type" node in the object tree. Some examples
	 * are "TABLE", "SYSTEM TABLE", "VIEW" etc.
	 */
	DatabaseObjectType TABLE_TYPE_DBO = DatabaseObjectType.createNewDatabaseObjectType();

	/**
	 * Database object type for a "Procedure Type" node in the object tree. There is
	 * only one node of this type in the object tree and it says "PROCEDURE".
	 */
	DatabaseObjectType PROC_TYPE_DBO = DatabaseObjectType.createNewDatabaseObjectType();

	/**
	 * Database object type for a "UDT Type" node in the object tree. There is only one
	 * node of this type in the object tree and it says "UDT".
	 */
	DatabaseObjectType UDT_TYPE_DBO = DatabaseObjectType.createNewDatabaseObjectType();

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
	void addExpander(DatabaseObjectType dboType, INodeExpander expander);

	/**
	 * Add a tab to be displayed in the detail panel for the passed
	 * database object type.
	 * 
	 * @param	dboType		Database object type.
	 * @param	tab			Tab to be displayed.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown when a <TT>null</TT> <TT>DatabaseObjectType</TT> or
	 *			<TT>IObjectTab</TT> passed.
	 */
	void addDetailTab(DatabaseObjectType dboType, IObjectTab tab);

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
	 * Add a listener to the object tree for selection changes.
	 * 
	 * @param	lis		The <TT>TreeSelectionListener</TT> you want added.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>TreeSelectionListener</TT> passed.
	 */
	void addTreeSelectionListener(TreeSelectionListener lis);

	/**
	 * Remove a listener from the object tree for selection changes.
	 * 
	 * @param	lis		The <TT>TreeSelectionListener</TT> you want removed.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>TreeSelectionListener</TT> passed.
	 */
	void removeTreeSelectionListener(TreeSelectionListener lis);

	/**
	 * Add an item to the popup menu for the specified database object type.
	 * 
	 * @param	dboType		Database object type.
	 * @param	action		Action to add to menu.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>DatabaseObjectType</TT> or
	 * 			<TT>Action</TT> thrown.
	 */
	void addToPopup(DatabaseObjectType dboType, Action action);

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
	 * Refresh the nodes currently selected in the object tree.
	 */
	void refreshSelectedNodes();

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
	 * Create a new <TT>DatabaseObjectType</TT>
	 * 
	 * @return	a new <TT>DatabaseObjectType</TT>
	 */
	DatabaseObjectType createNewDatabaseObjectType();	
}
