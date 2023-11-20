package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;
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

import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.SessionColoringUtil;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.*;
import net.sourceforge.squirrel_sql.client.session.action.dataimport.action.ImportTableDataAction;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.DBDiffObjectTreeMenuFactory;
import net.sourceforge.squirrel_sql.client.session.action.objecttreecopyrestoreselection.CopyRestoreSelectionMenuFactory;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.SQLScriptMenuFactory;
import net.sourceforge.squirrel_sql.client.session.menuattic.AtticHandler;
import net.sourceforge.squirrel_sql.client.session.menuattic.MenuOrigin;
import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;
/**
 * This is the tree showing the structure of objects in the database.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ObjectTree extends JTree
{
    /** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(ObjectTree.class);

	/** Current session. */
	private final ISession _session;

	/**
	 * Collection of popup menus (<TT>JPopupMenu</TT> instances) for the
	 * object tree. Keyed by node type.
	 */
	private final Map<DatabaseObjectType, List<ObjectTreeMenuEntry>> _dbObjType_menuEntries = new HashMap<>();

	/**
	 * Global popup menu. This contains items that are to be displayed
	 * in the popup menu no matter what items are selected in the tree.
	 */
	private final List<ObjectTreeMenuEntry> _globalPopupMenuEntries = new ArrayList<>();

	/**
	 * String representation of the <TT>TreePath</TT> objects that have been
	 * expanded. The key is <TT>Treepath.toString()</TT> and the value
	 * is <TT>null</TT>.
	 */
	private Set<String> _expandedPathNames = new HashSet<>();

	/**
	 * Collection of listeners to this object tree.
	 */
	private EventListenerList _listenerList = new EventListenerList();

   /**
    * ctor specifying session.
    *
    * @param	session	Current session.
    *
    * @throws	IllegalArgumentException
    * 			Thrown if <TT>null</TT> <TT>ISession</TT> passed.
    */
   ObjectTree(final ISession session)
   {
		super(new ObjectTreeModel(session));

		try
		{

			setRowHeight(getFontMetrics(getFont()).getHeight());
			_session = session;
			//_model = (ObjectTreeModel)getModel();
			//setModel(_model);

			addTreeExpansionListener(new NodeExpansionListener());

			addTreeSelectionListener(e -> onTreeSelectionChanged(e));

			setShowsRootHandles(true);

			// Add actions to the popup menu.
			final ActionCollection actions = session.getApplication().getActionCollection();

			// Options for global popup menu.
			addToPopup(actions.get(RefreshSchemaInfoAction.class));
			addToPopup(actions.get(RefreshObjectTreeItemAction.class));

			addToPopup(actions.get(FindColumnsInObjectTreeNodesAction.class));

			addToPopup(DatabaseObjectType.TABLE, actions.get(EditWhereColsAction.class));

			addToPopup(DatabaseObjectType.TABLE, actions.get(SQLFilterAction.class));
			addToPopup(DatabaseObjectType.VIEW, actions.get(SQLFilterAction.class));

			addToPopup(DatabaseObjectType.TABLE, actions.get(DeleteSelectedTablesAction.class));
			addToPopup(DatabaseObjectType.TABLE, actions.get(ShowTableReferencesAction.class));

			addToPopup(DatabaseObjectType.SESSION, actions.get(FilterObjectsAction.class));

			if (_session.getSQLConnection().getSQLMetaData().supportsCatalogs())
			{
				addToPopup(DatabaseObjectType.CATALOG,actions.get(SetDefaultCatalogAction.class));
			}

			addToPopup(actions.get(CopySimpleObjectNameAction.class));
			addToPopup(actions.get(CopyQualifiedObjectNameAction.class));

			addToPopup(CopyRestoreSelectionMenuFactory.getObjectTreeMenu());

			addToPopup(DatabaseObjectType.TABLE, SQLScriptMenuFactory.getObjectTreeMenu(DatabaseObjectType.TABLE));
			addToPopup(DatabaseObjectType.VIEW, SQLScriptMenuFactory.getObjectTreeMenu(DatabaseObjectType.VIEW));

			// Put export next to import
			addToPopup(DatabaseObjectType.TABLE, SQLScriptMenuFactory.getExportAction());
			addToPopup(DatabaseObjectType.VIEW, SQLScriptMenuFactory.getExportAction());
			addToPopup(DatabaseObjectType.TABLE, actions.get(ImportTableDataAction.class));
			addToPopup(DatabaseObjectType.TABLE_TYPE_DBO, actions.get(ImportTableDataAction.class));
			addToPopup(DatabaseObjectType.SESSION, actions.get(ImportTableDataAction.class));
			addToPopup(DatabaseObjectType.SCHEMA, actions.get(ImportTableDataAction.class));
			addToPopup(DatabaseObjectType.CATALOG, actions.get(ImportTableDataAction.class));

			addToPopup(DatabaseObjectType.TABLE, DBDiffObjectTreeMenuFactory.createMenu());

			addMouseListener(new ObjectTreeMouseListener());
			setCellRenderer(new ObjectTreeCellRenderer(getObjectTreeModel(), _session));

			SwingUtilities.invokeLater(() ->
			{
				ObjectTree.this.refresh(false);
				ObjectTree.this.setSelectionPath(ObjectTree.this.getPathForRow(0));
				SessionColoringUtil.colorTree(session, this);
			});
		}
		catch (SQLException e)
		{
			throw Utilities.wrapRuntime(e);
		}
	}

	private void onTreeSelectionChanged(TreeSelectionEvent e)
	{
		if(null != e.getNewLeadSelectionPath())
		{
			scrollPathToVisible(e.getNewLeadSelectionPath());
		}
	}

	public ObjectTreeModel getObjectTreeModel()
	{
		return (ObjectTreeModel) getModel();
	}

   // Mouse listener used to display popup menu.
   private class ObjectTreeMouseListener extends MouseAdapter {
      public void mousePressed(MouseEvent evt)
      {
         checkSelectAndPopUp(evt);
      }

		private void checkSelectAndPopUp(MouseEvent evt)
		{
			if (evt.isPopupTrigger())
			{
				// If the user wants to select for Right mouse clicks then change the selection before popup
				// appears
				if (_session.getApplication().getSquirrelPreferences().getSelectOnRightMouseClick())
				{
					TreePath path = ObjectTree.this.getPathForLocation(evt.getX(), evt.getY());
					boolean alreadySelected = false;
					TreePath[] selectedPaths = ObjectTree.this.getSelectionPaths();
					if (selectedPaths != null)
					{
						for (TreePath selectedPath : selectedPaths)
						{
							if (path != null && path.equals(selectedPath))
							{
								alreadySelected = true;
								break;
							}
						}
					}
					if (!alreadySelected)
					{
						ObjectTree.this.setSelectionPath(path);
					}
				}
				showPopup(evt.getX(), evt.getY());
			}
		}

      public void mouseReleased(MouseEvent evt)
      {
         checkSelectAndPopUp(evt);
      }
   }
   
	/**
	 * Component has been added to its parent.
	 */
	public void addNotify()
	{
		super.addNotify();
		// Register so that we can display different tooltips depending
		// which entry in list mouse is over.
		ToolTipManager.sharedInstance().registerComponent(this);
	}

	/**
	 * Component has been removed from its parent.
	 */
	public void removeNotify()
	{
		super.removeNotify();

		// Don't need tooltips any more.
		ToolTipManager.sharedInstance().unregisterComponent(this);
	}

	/**
	 * Return the name of the object that the mouse is currently
	 * over as the tooltip text.
	 *
	 * @param	event	Used to determine the current mouse position.
	 */
	public String getToolTipText(MouseEvent evt)
	{
		String tip = null;
		final TreePath path = getPathForLocation(evt.getX(), evt.getY());
		if (path != null)
		{
			tip = path.getLastPathComponent().toString();
		}
		else
		{
			tip = getToolTipText();
		}
		return tip;
	}

	/**
	 * Refresh tree.
    * @param reloadSchemaInfo
    */
	public void refresh(final boolean reloadSchemaInfo)
	{
      Runnable task = new Runnable()
      {
         public void run()
         {
            if (reloadSchemaInfo)
            {
               _session.getSchemaInfo().reloadAll();
            }


            GUIUtils.processOnSwingEventThread(() -> refreshTree());
         }
      };

      if(reloadSchemaInfo)
      {
         _session.getApplication().getThreadPool().addTask(task);
      }
      else
      {
         // No need to this in background when SchemaInfo  is not reloaded.
         task.run();
      }
   }

   private void refreshTree()
   {
      final TreePath[] previouslySelectedTreePaths = getSelectionPaths();

      ObjectTreeNode root = getObjectTreeModel().getRootObjectTreeNode();
      root.removeAllChildren();
      fireObjectTreeCleared();
      expandTree(root, previouslySelectedTreePaths, false);
      fireObjectTreeRefreshed();
   }

   /**
    * Refresh the nodes currently selected in the object tree.
    */
   public void refreshSelectedNodes()
   {

      final TreePath[] selectedPaths = getSelectionPaths();
      ObjectTreeNode[] nodesToRefresh = getSelectedNodes();
      clearSelection();


      DefaultMutableTreeNode parent = null;

		if (0 < nodesToRefresh.length)
		{
			parent = (DefaultMutableTreeNode) nodesToRefresh[0].getParent();
		}

		if (parent != null)
      {
         parent.removeAllChildren();
         expandTree((ObjectTreeNode) parent, selectedPaths, true);
      }
      else if (0 < nodesToRefresh.length)
      {
         nodesToRefresh[0].removeAllChildren();
         expandTree(nodesToRefresh[0], selectedPaths, true);
      }
   }

   /**
	 * Adds a listener for changes in this cache entry.
	 *
	 * @param	lis	a IObjectCacheChangeListener that will be notified when
	 *				objects are added and removed from this cache entry.
	 */
	public void addObjectTreeListener(IObjectTreeListener lis)
	{
		_listenerList.add(IObjectTreeListener.class, lis);
	}

	/**
	 * Removes a listener for changes in this cache entry.
	 *
	 * @param	lis a IObjectCacheChangeListener that will be notified when
	 *			objects are added and removed from this cache entry.
	 */
	void removeObjectTreeListener(IObjectTreeListener lis)
	{
		_listenerList.remove(IObjectTreeListener.class, lis);
	}

   private void expandTree(ObjectTreeNode startNode, TreePath[] previouslySelectedTreePaths, boolean refreshSchemaInfo)
	{
      CursorChanger cursorChg = new CursorChanger(this);
      cursorChg.show();
      try
      {
         if (refreshSchemaInfo)
         {
            _session.getSchemaInfo().reload(startNode.getDatabaseObjectInfo());
         }

         expandNode(startNode);
         if (previouslySelectedTreePaths != null)
         {
            List<TreePath> newlySelectedTreepaths = ExpansionStateRestorer.restoreExpansionState(this, startNode, previouslySelectedTreePaths, _expandedPathNames);
            setSelectionPaths(newlySelectedTreepaths.toArray(new TreePath[0]));
         }
      }
      finally
      {
         cursorChg.restore();
      }
   }

	public void expandNode(ObjectTreeNode node)
	{
		if (node == null)
		{
			throw new IllegalArgumentException("ObjectTreeNode == null");
		}
		// If node hasn't already been expanded.
		if (node.getChildCount() == 0 && node.hasNoChildrenFoundWithExpander() == false)
		{
			// Add together the standard expanders for this node type and any
			// individual expanders that there are for the node and process them.
			final DatabaseObjectType dboType = node.getDatabaseObjectType();
			INodeExpander[] stdExpanders = getObjectTreeModel().getExpanders(dboType);
			INodeExpander[] extraExpanders = node.getExpanders();
			if (stdExpanders.length > 0 || extraExpanders.length > 0)
			{
				INodeExpander[] expanders = null;
				if (stdExpanders.length > 0 && extraExpanders.length == 0)
				{
					expanders = stdExpanders;
				}
				else if (stdExpanders.length == 0 && extraExpanders.length > 0)
				{
					expanders = extraExpanders;
				}
				else
				{
					expanders = new INodeExpander[stdExpanders.length + extraExpanders.length];
					System.arraycopy(stdExpanders, 0, expanders, 0, stdExpanders.length);
					System.arraycopy(extraExpanders, 0, expanders, stdExpanders.length, extraExpanders.length);
				}
				new TreeLoader(_session, this, getObjectTreeModel(), node, expanders).execute();
			}
		}
	}

	/**
	 * Add an item to the popup menu for the specified node type in the object
	 * tree.
	 *
	 * @param	dboType		Database Object Type.
	 * @param	action		Action to add to menu.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>Action</TT> or
	 *			<TT>DatabaseObjectType</TT>thrown.
	 */
	void addToPopup(DatabaseObjectType dboType, Action action)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("Null DatabaseObjectType passed");
		}
		if (action == null)
		{
			throw new IllegalArgumentException("Null Action passed");
		}

		List<ObjectTreeMenuEntry> objectTreeMenuEntries = _dbObjType_menuEntries.computeIfAbsent(dboType, k -> new ArrayList<>());
		objectTreeMenuEntries.add(new ObjectTreeMenuEntry(action));
	}

	/**
	 * Add an item to the popup menu for the all nodes.
	 *
	 * @param	action		Action to add to menu.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>Action</TT> thrown.
	 */
	void addToPopup(Action action)
	{
		if (action == null)
		{
			throw new IllegalArgumentException("Null Action passed");
		}

		_globalPopupMenuEntries.add(new ObjectTreeMenuEntry(action));
	}

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
	public void addToPopup(DatabaseObjectType dboType, JMenu menu)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("DatabaseObjectType == null");
		}
		if (menu == null)
		{
			throw new IllegalArgumentException("JMenu == null");
		}

      List<ObjectTreeMenuEntry> objectTreeMenuEntries = _dbObjType_menuEntries.computeIfAbsent(dboType, k -> new ArrayList<>());
      objectTreeMenuEntries.add(new ObjectTreeMenuEntry(menu));
	}

	/**
	 * Add an hierarchical menu to the popup menu for all node types.
	 *
	 * @param	menu	<TT>JMenu</TT> to add to menu.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>JMenu</TT> thrown.
	 */
	public void addToPopup(JMenu menu)
	{
		if (menu == null)
		{
			throw new IllegalArgumentException("JMenu == null");
		}
		_globalPopupMenuEntries.add(new ObjectTreeMenuEntry(menu));
	}

	/**
	 * Get the popup menu for the passed database object type. If one
	 * doesn't exist then storeByDboType one if requested to do so.

	 * @param	dboType		Database Object Type.
	 * @param	storeByDboType		If <TT>true</TT> popup will eb created if it
	 *						doesn't exist.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>Action</TT> or
	 *			<TT>DatabaseObjectType</TT>thrown.
	 */
	private JPopupMenu getPopup(DatabaseObjectType dboType)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("Null DatabaseObjectType passed");
		}

		List<ObjectTreeMenuEntry> dboTypeMenuEntries = _dbObjType_menuEntries.get(dboType);
		if (dboTypeMenuEntries != null)
		{
			return toPopupMenu(_globalPopupMenuEntries, dboTypeMenuEntries);
		}
		else
		{
			return toPopupMenu(_globalPopupMenuEntries);
		}

	}

	private JPopupMenu toPopupMenu(List<ObjectTreeMenuEntry> ... objectTreeMenuEntryLists)
	{
		JPopupMenu ret = new JPopupMenu();

		for (List<ObjectTreeMenuEntry> objectTreeMenuEntryList : objectTreeMenuEntryLists)
		{
			for (ObjectTreeMenuEntry globalPopupMenuEntry : objectTreeMenuEntryList)
			{
				globalPopupMenuEntry.addToPopup(ret);
			}
		}
		return ret;
	}

	/**
	 * Return an array of the currently selected nodes. This array is sorted
	 * by the simple name of the database object.
	 *
	 * @return	array of <TT>ObjectTreeNode</TT> objects.
	 */
	ObjectTreeNode[] getSelectedNodes()
	{
		TreePath[] paths = getSelectionPaths();
		List<ObjectTreeNode> list = new ArrayList<ObjectTreeNode>();
		if (paths != null)
		{
			for (int i = 0; i < paths.length; ++i)
			{
				Object obj = paths[i].getLastPathComponent();
				if (obj instanceof ObjectTreeNode)
				{
					list.add((ObjectTreeNode)obj);
				}
			}
		}
		ObjectTreeNode[] ar = list.toArray(new ObjectTreeNode[list.size()]);
		Arrays.sort(ar, new NodeComparator());
		return ar;
	}
    
	/**
	 * Return an array of the currently selected database
	 * objects.
	 *
	 * @return	array of <TT>ObjectTreeNode</TT> objects.
	 */
	IDatabaseObjectInfo[] getSelectedDatabaseObjects()
	{
		ObjectTreeNode[] nodes = getSelectedNodes();
		IDatabaseObjectInfo[] dbObjects = new IDatabaseObjectInfo[nodes.length];
		for (int i = 0; i < nodes.length; ++i)
		{
			dbObjects[i] = nodes[i].getDatabaseObjectInfo();
		}
		return dbObjects;
	}

    /**
     * Return a type-safe list of the currently selected database tables
     *
     * @return  list of <TT>ITableInfo</TT> objects.
     */
    List<ITableInfo> getSelectedTables()
    {
        ObjectTreeNode[] nodes = getSelectedNodes();
        ArrayList<ITableInfo> result = new ArrayList<>();
        for (int i = 0; i < nodes.length; ++i)
        {
			  if (nodes[i].getDatabaseObjectType() == DatabaseObjectType.TABLE)
			  {
				  result.add((ITableInfo) nodes[i].getDatabaseObjectInfo());
			  }
        }
        return result;
    }
    
    
	/**
	 * Get the appropriate popup menu for the currently selected nodes
	 * in the object tree and display it.
	 *
	 * @param	x	X pos to display popup at.
	 * @param	y	Y pos to display popup at.
	 */
	private void showPopup(int x, int y)
	{
		ObjectTreeNode[] selObj = getSelectedNodes();
		if (selObj.length > 0)
		{
			// See if all selected nodes are of the same type.
			boolean sameType = true;
			final DatabaseObjectType dboType = selObj[0].getDatabaseObjectType();
			for (int i = 1; i < selObj.length; ++i)
			{
				if (selObj[i].getDatabaseObjectType() != dboType)
				{
					sameType = false;
					break;
				}
			}

			JPopupMenu ret;
			if (sameType)
			{
				ret = getPopup(dboType);
			}
			else
			{
				ret = toPopupMenu(_globalPopupMenuEntries);
			}

			AtticHandler.initAtticForMenu(ret, MenuOrigin.OBJECT_TREE);

			ret.show(this, x, y);
		}
	}

	/**
	 * Fire a "tree cleared" event to all listeners.
	 */
	private void fireObjectTreeCleared()
	{
		// Guaranteed to be non-null.
		Object[] listeners = _listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event.
		ObjectTreeListenerEvent evt = null;
		for (int i = listeners.length - 2; i >= 0; i-=2 )
		{
			if (listeners[i] == IObjectTreeListener.class)
			{
				// Lazily create the event.
				if (evt == null)
				{
					evt = new ObjectTreeListenerEvent(ObjectTree.this);
				}
				((IObjectTreeListener)listeners[i + 1]).objectTreeCleared(evt);
			}
		}
	}

	/**
	 * Fire a "tree refreshed" event to all listeners.
	 */
	private void fireObjectTreeRefreshed()
	{
		// Guaranteed to be non-null.
		Object[] listeners = _listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event.
		ObjectTreeListenerEvent evt = null;
		for (int i = listeners.length - 2; i >= 0; i-=2 )
		{
			if (listeners[i] == IObjectTreeListener.class)
			{
				// Lazily create the event.
				if (evt == null)
				{
					evt = new ObjectTreeListenerEvent(ObjectTree.this);
				}
				((IObjectTreeListener)listeners[i + 1]).objectTreeRefreshed(evt);
			}
		}
	}

	public ISession getSession()
	{
		return _session;
	}

	public void dispose()
	{
		_dbObjType_menuEntries.clear();
		_globalPopupMenuEntries.clear();
	}

	private final class NodeExpansionListener implements TreeExpansionListener
	{
		public void treeExpanded(TreeExpansionEvent evt)
		{
			// Get the node to be expanded.
			final TreePath path = evt.getPath();
			final Object parentObj = path.getLastPathComponent();
			if (parentObj instanceof ObjectTreeNode)
			{
				expandTree((ObjectTreeNode)parentObj, null, false);
				_expandedPathNames.add(path.toString());
			}
		}

		public void treeCollapsed(TreeExpansionEvent evt)
		{
			_expandedPathNames.remove(evt.getPath().toString());
		}
	}

	/**
	 * This class is used to sort the nodes by their title.
	 */
	private static class NodeComparator implements Comparator<ObjectTreeNode>, Serializable
	{
      public int compare(ObjectTreeNode obj1, ObjectTreeNode obj2)
		{
			return obj1.toString().compareToIgnoreCase(obj2.toString());
		}
	}

}
