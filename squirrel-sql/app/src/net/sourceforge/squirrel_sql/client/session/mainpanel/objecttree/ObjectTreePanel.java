package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;
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
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode.IObjectTreeNodeType;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.databasepanel.DataTypesTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.databasepanel.DateTimeFunctionsTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.databasepanel.KeywordsTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.databasepanel.MetaDataTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.databasepanel.NumericFunctionsTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.databasepanel.StringFunctionsTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.databasepanel.SystemFunctionsTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.databasepanel.TableTypesTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.objectpanel.IObjectPanelTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.procedurepanel.ProcedureColumnsTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.procedurepanel.ProcedureInfoTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel.ColumnPriviligesTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel.ColumnsTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel.ContentsTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel.ExportedKeysTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel.ImportedKeysTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel.IndexesTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel.PrimaryKeyTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel.RowIDTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel.TableInfoTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel.TablePriviligesTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel.VersionColumnsTab;
/**
 * This is the panel for the Object Tree tab.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ObjectTreePanel extends JPanel
{
	/** Current session. */
	private ISession _session;

	/** Tree of objects within the database. */
	private ObjectTree _tree;

	/** Split pane between the object tree and the data panel. */
	private final JSplitPane _splitPane =
		new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

	/**
	 * Empty data panel. Used if the object selected in the object
	 * tree doesn't require a panel.
	 */
	private final ObjectTreeTabbedPane _emptyTabPane;

	/**
	 * Contains instances of <TT>ObjectTreeTabbedPane</TT> objects keyed by
	 * the node type. I.E. the tabbed folders for each node type are kept here.
	 */
	private Map _tabbedPanes = new HashMap();

	/**
	 *  Collection of <TT>IObjectPanelTab</TT> objects to be displayed for all
	 * nodes in the object tree.
	 */
	private List _tabsForAllNodes = new ArrayList();
	
	/**
	 * ctor specifying the current session.
	 * 
	 * @param	session	Current session.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public ObjectTreePanel(ISession session)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		_session = session;

		_emptyTabPane = new ObjectTreeTabbedPane(_session);

		createUserInterface();

		// Register tabs to appear for all nodes in the object tree.
//		registerDetailTab(new DatabaseObjectInfoTab());

		// Register tabs to display in the details panel for database nodes.
		registerDetailTab(IObjectTreeNodeType.DATABASE, new MetaDataTab());
		registerDetailTab(IObjectTreeNodeType.DATABASE, new TableTypesTab());
		registerDetailTab(IObjectTreeNodeType.DATABASE, new DataTypesTab());
		registerDetailTab(IObjectTreeNodeType.DATABASE, new NumericFunctionsTab());
		registerDetailTab(IObjectTreeNodeType.DATABASE, new StringFunctionsTab());
		registerDetailTab(IObjectTreeNodeType.DATABASE, new SystemFunctionsTab());
		registerDetailTab(IObjectTreeNodeType.DATABASE, new DateTimeFunctionsTab());
		registerDetailTab(IObjectTreeNodeType.DATABASE, new KeywordsTab());

		// Register tabs to display in the details panel for table nodes.
		registerDetailTab(IObjectTreeNodeType.TABLE, new TableInfoTab());
		registerDetailTab(IObjectTreeNodeType.TABLE, new ContentsTab());
		registerDetailTab(IObjectTreeNodeType.TABLE, new ColumnsTab());
		registerDetailTab(IObjectTreeNodeType.TABLE, new PrimaryKeyTab());
		registerDetailTab(IObjectTreeNodeType.TABLE, new ExportedKeysTab());
		registerDetailTab(IObjectTreeNodeType.TABLE, new ImportedKeysTab());
		registerDetailTab(IObjectTreeNodeType.TABLE, new IndexesTab());
		registerDetailTab(IObjectTreeNodeType.TABLE, new TablePriviligesTab());
		registerDetailTab(IObjectTreeNodeType.TABLE, new ColumnPriviligesTab());
		registerDetailTab(IObjectTreeNodeType.TABLE, new RowIDTab());
		registerDetailTab(IObjectTreeNodeType.TABLE, new VersionColumnsTab());

		// Register tabs to display in the details panel for procedure nodes.
		registerDetailTab(IObjectTreeNodeType.PROCEDURE, new ProcedureInfoTab());
		registerDetailTab(IObjectTreeNodeType.PROCEDURE, new ProcedureColumnsTab());

		// Register tabs to display in the details panel for UDT nodes.
		// TODO: get rid of this once all nodes have a DatabaseObjectInfoTab tab.
		registerDetailTab(IObjectTreeNodeType.UDT, new DatabaseObjectInfoTab());
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
			throw new IllegalArgumentException("Null INodeExpander passed");
		}
		_tree.getTypedModel().registerExpander(nodeType, expander);
	}

	/**
	 * Register a tab to be displayed in the detail panel for all nodes
	 * in the object tree.
	 * 
	 * @param	tab			Tab to be displayed.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown when a <TT>null</TT> <TT>IObjectPanelTab</TT> passed.
	 */
	public void registerDetailTab(IObjectPanelTab tab)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("IObjectPanelTab == null");
		}

		_tabsForAllNodes.add(tab);
//		Iterator it = _tabbedPanes.values().iterator();
//		while (it.hasNext())
//		{
//			ObjectTreeTabbedPane tabPane = (ObjectTreeTabbedPane)it.next();
//			tabPane.addObjectPaneltab(tab);
//		}
	}

	/**
	 * Register a tab to be displayed in the detail panel for the passed
	 * object tree node type.
	 * 
	 * @param	nodeType	Node type.
	 * @param	tab			Tab to be displayed.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown when a <TT>null</TT> <TT>IObjectPanelTab</TT> passed.
	 */
	public void registerDetailTab(int nodeType, IObjectPanelTab tab)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("IObjectPanelTab == null");
		}
		
		getOrCreateObjectPanelTabbedPane(nodeType).addObjectPaneltab(tab);
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
	public void addTreeModelListener(TreeModelListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("TreeModelListener == null");
		}
		_tree.getModel().addTreeModelListener(lis);
	}

	/**
	 * Remove a structure changes listener from the object tree.
	 * 
	 * @param	lis		The <TT>TreeModelListener</TT> you want removed.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>TreeModelListener</TT> passed.
	 */
	public void removeTreeModelListener(TreeModelListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("TreeModelListener == null");
		}
		_tree.getModel().removeTreeModelListener(lis);
	}

	/**
	 * Add an item to the popup menu for the specified node type in the object
	 * tree.
	 * 
	 * @param	nodeType	Object Tree node type.
	 *						@see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode.IObjectTreeNodeType
	 * @param	action		Action to add to menu.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>Action</TT> thrown.
	 */
	public void addToObjectTreePopup(int nodeType, Action action)
	{
		if (action == null)
		{
			throw new IllegalArgumentException("Null Action passed");
		}
		_tree.addToPopup(nodeType, action);
	}

	/**
	 * Add an item to the popup menu for all node types in the object
	 * tree.
	 * 
	 * @param	action		Action to add to menu.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>Action</TT> thrown.
	 */
	public void addToObjectTreePopup(Action action)
	{
		if (action == null)
		{
			throw new IllegalArgumentException("Null Action passed");
		}
		_tree.addToPopup(action);
	}

	/**
	 * Return an array of the currently selected nodes.
	 *
	 * @return	array of <TT>ObjectTreeNode</TT> objects.
	 */
	public ObjectTreeNode[] getSelectedNodes()
	{
		return _tree.getSelectedNodes();
	}

	/**
	 * Return an array of the currently selected database
	 * objects. This is guaranteed to be non-null.
	 *
	 * @return	array of <TT>ObjectTreeNode</TT> objects.
	 */
	public IDatabaseObjectInfo[] getSelectedDatabaseObjects()
	{
		return _tree.getSelectedDatabaseObjects();
	}

	/**
	 * Refresh object tree.
	 */
	public void refreshTree()
	{
		_tree.refresh();
	}

	/**
	 * Remove one or more nodes from the tree.
	 * 
	 * @param	nodes	Array of nodes to be removed.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>ObjectTreeNode[]</TT> thrown.
	 */
	public void removeNodes(ObjectTreeNode[] nodes)
	{
		if (nodes == null)
		{
			throw new IllegalArgumentException("ObjectTreeNode[] == null");
		}
		ObjectTreeModel model = _tree.getTypedModel();
		for (int i = 0; i < nodes.length; ++i)
		{
			model.removeNodeFromParent(nodes[i]);
		}
	}

	/**
	 * Set the panel to be shown in the data area for the passed
	 * path.
	 * 
	 * @param	path	path of node currently selected.
	 */
	private void setSelectedObjectPanel(TreePath path)
	{
		ObjectTreeTabbedPane tabPane = null;
		if (path != null)
		{
			Object lastComp = path.getLastPathComponent();
			if (lastComp instanceof ObjectTreeNode)
			{
				ObjectTreeNode node = (ObjectTreeNode)lastComp;
				tabPane = getDetailPanel(node);
				tabPane.setDatabaseObjectInfo(node.getDatabaseObjectInfo());
				tabPane.selectCurrentTab();
			}
		}
		setSelectedObjectPanel(tabPane);
	}

	/**
	 * Set the panel in the data area to that passed.
	 * 
	 * @param	comp	Component to be displayed. If <TT>null</TT> use an empty
	 * 					panel.
	 */
	private void setSelectedObjectPanel(Component comp)
	{
		if (comp == null)
		{
			comp = _emptyTabPane;
		}

		int divLoc = _splitPane.getDividerLocation();
		Component existing = _splitPane.getRightComponent();
		if (existing != null)
		{
			_splitPane.remove(existing);
		}
		_splitPane.add(comp, JSplitPane.RIGHT);
		_splitPane.setDividerLocation(divLoc);
	}

	/**
	 * Get the detail panel to be displayed for the passed node.
	 * 
	 * @param	node	Node to get details panel for.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>ObjectTreeNode</TT> passed.
	 */
	private ObjectTreeTabbedPane getDetailPanel(ObjectTreeNode node)
	{
		if (node == null)
		{
			throw new IllegalArgumentException("ObjectTreeNode == null");
		}

		ObjectTreeTabbedPane tabPane = getObjectPanelTabbedPane(node.getNodeType());
		if (tabPane != null)
		{
			return tabPane;
		}

		return _emptyTabPane;
	}

	/**
	 * Return the tabbed pane for the passed object tree node type.
	 * 
	 * @param	nodeType	The object tree node type we are getting a tabbed
	 *						pane for.
	 * 
	 * @return	the <TT>ObjectTreeTabbedPane</TT> for the passed object
	 *			tree node type.
	 */
	private ObjectTreeTabbedPane getObjectPanelTabbedPane(int nodeType)
	{
		return (ObjectTreeTabbedPane)_tabbedPanes.get(String.valueOf(nodeType));
	}

	/**
	 * Return the tabbed pane for the passed object tree node type. If one
	 * doesn't exist then create it.
	 * 
	 * @param	nodeType	The object tree node type we are getting a tabbed
	 *						pane for.
	 * 
	 * @return	the <TT>List</TT> containing all the <TT>IObjectPanelTab</TT>
	 * 			instances for the passed object tree node type.
	 */
	private ObjectTreeTabbedPane getOrCreateObjectPanelTabbedPane(int nodeType)
	{
		final String key = String.valueOf(nodeType);
		ObjectTreeTabbedPane tabPane = (ObjectTreeTabbedPane)_tabbedPanes.get(key);
		if (tabPane == null)
		{
			tabPane = new ObjectTreeTabbedPane(_session);
			_tabbedPanes.put(key, tabPane);

			// Add tabs that are displayed for all nodes to this new tabbed
			// folder.
//			Iterator it = _tabsForAllNodes.iterator();
//			while (it.hasNext())
//			{
//				//?? TODO: Should clone/copy the tab
//				IObjectPanelTab tab = (IObjectPanelTab)it.next();
//				tabPane.addObjectPaneltab(tab);
//			}
		}
		return tabPane;
	}

	/**
	 * Create the user interface.
	 */
	private void createUserInterface()
	{
		setLayout(new BorderLayout());

		_tree = new ObjectTree(_session);

		_splitPane.setOneTouchExpandable(true);
		_splitPane.setContinuousLayout(true);
		JScrollPane sp = new JScrollPane();
		sp.setViewportView(_tree);
		sp.setPreferredSize(new Dimension(200, 200));
		_splitPane.add(sp, JSplitPane.LEFT);
		add(_splitPane, BorderLayout.CENTER);

//		setSelectedObjectPanel(_emptyTabPane);
		_tree.addTreeSelectionListener(new ObjectTreeSelectionListener());

		setSelectedObjectPanel(_emptyTabPane);
		_splitPane.setDividerLocation(200);
	}

	/**
	 * This class listens for changes in the node selected in the tree
	 * and displays the appropriate detail panrl for the node.
	 */
	private final class ObjectTreeSelectionListener
		implements TreeSelectionListener
	{
		public void valueChanged(TreeSelectionEvent evt)
		{
			setSelectedObjectPanel(evt.getNewLeadSelectionPath());
		}
	}

//	private final class EmptyTab implements IObjectPanelTab
//	{
//		public void clear()
//		{
//		}
//
//		public Component getComponent()
//		{
//			return _emptyPnl;
//		}
//
//		public String getHint()
//		{
//			return "";
//		}
//
//		public String getTitle()
//		{
//			return "";
//		}
//
//		public void select()
//		{
//		}
//
//		public void setSession(ISession session)
//		{
//		}
//	}
}
