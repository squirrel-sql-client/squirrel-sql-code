package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2002-2004 Colin Bell and Johan Compagner
 * colbell@users.sourceforge.net
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
import java.awt.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionListener;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.*;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.IObjectTab;
import net.sourceforge.squirrel_sql.client.session.schemainfo.FilterMatcher;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
/**
 * This interface defines the API through which plugins can work with the object
 * tree.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface IObjectTreeAPI extends IHasIdentifier
{
   /**
    * Retrieves the session of associated with the tree.
    *
    * @return	Session associated with the tree.
    */
   ISession getSession();

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
	 * Add a listener to the object tree.
	 *
	 * @param	lis		The <TT>ObjectTreeListener</TT> you want added.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ObjectTreeListener</TT> passed.
	 */
	void addObjectTreeListener(IObjectTreeListener lis);

	/**
	 * Remove a listener from the object tree.
	 *
	 * @param	lis		The <TT>ObjectTreeListener</TT> you want removed.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ObjectTreeListener</TT> passed.
	 */
	void removeObjectTreeListener(IObjectTreeListener lis);

	/**
	 * Add an <TT>Action</TT> to the popup menu for the specified database
	 * object type.
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
	 * Add an <TT>Action</TT> to the popup menu for all node types.
	 *
	 * @param	action		Action to add to menu.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>Action</TT> thrown.
	 */
	void addToPopup(Action action);

	/**
	 * Add an hierarchical menu to the popup menu for the specified database
	 * object type.
	 *
	 * @param	dboType		Database object type.
	 * @param	menu		<TT>JMenu</TT> to add to menu.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>DatabaseObjectType</TT> or
	 * 			<TT>JMenu</TT> thrown.
	 */
	void addToPopup(DatabaseObjectType dboType, JMenu menu);

	/**
	 * Add an hierarchical menu to the popup menu for all node types.
	 *
	 * @param	menu	<TT>JMenu</TT> to add to menu.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>JMenu</TT> thrown.
	 */
	void addToPopup(JMenu menu);

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
     * Return a type-safe list of the currently selected database tables
     *
     * @return  list of <TT>ITableInfo</TT> objects.
     */
    List<ITableInfo> getSelectedTables();
    
	/**
	 * Refresh the object tree.
	 */
	void refreshTree();

   /**
    * Refresh the object tree.
    */
   void refreshTree(boolean reloadSchemaInfo);


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
    * Retrieve details about all object types that can be in this
    * tree.
    *
    * @return	DatabaseObjectType[]	Array of object type info objects.
    */
   DatabaseObjectType[] getDatabaseObjectTypes();

	/**
	 * Add a known database object type to the object tree.
	 *
	 * @param	dboType		The new database object type.
	 */
	void addKnownDatabaseObjectType(DatabaseObjectType dboType);

	IObjectTab getTabbedPaneIfSelected(DatabaseObjectType dbObjectType, String title);

   /**
    * Tries to locate the object given by the paramteres in the Object tree.
    * The first matching object found is selected.
    *
    * @param catalog null means any catalog
    * @param schema null means any schema
    * @return true if the Object was found and selected.
    */
   boolean selectInObjectTree(String catalog, String schema, FilterMatcher objectMatcher);
   
   /**
    * Selects the root node of the tree.
    */
   void selectRoot();
   
   /**
    * Expands the specified tree node.
    * 
    * @param node the tree node to expand
    */
   void expandNode(ObjectTreeNode node);
   
   /**
    * Calls refreshComponent on the selected tab in the current 
    * ObjectTreeTabbedPane, if the selected tab happens to be a BaseDataSetTab
    * type. 
    * 
    * @throws DataSetException if there was a problem refreshing the component.
    */
   void refreshSelectedTab() throws DataSetException;


   FindInObjectTreeController getFindController();

   Component getDetailTabComp();
}
