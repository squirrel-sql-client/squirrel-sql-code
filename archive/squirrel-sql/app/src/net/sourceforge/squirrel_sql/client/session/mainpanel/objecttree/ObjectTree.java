package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;
/*
 * Copyright (C) 2002-2003 Colin Bell
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.TreePath;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.EnumerationIterator;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.CopyQualifiedObjectNameAction;
import net.sourceforge.squirrel_sql.client.session.action.CopySimpleObjectNameAction;
import net.sourceforge.squirrel_sql.client.session.action.DropSelectedTablesAction;
import net.sourceforge.squirrel_sql.client.session.action.RefreshObjectTreeAction;
import net.sourceforge.squirrel_sql.client.session.action.RefreshObjectTreeItemAction;
import net.sourceforge.squirrel_sql.client.session.action.SetDefaultCatalogAction;
/**
 * This is the tree showing the structure of objects in the database.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class ObjectTree extends JTree
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(ObjectTree.class);

	/** Model for this tree. */
	private final ObjectTreeModel _model;

	/** Current session. */
	private final ISession _session;

	/**
	 * Collection of popup menus (<TT>JPopupMenu</TT> instances) for the
	 * object tree. Keyed by node type.
	 */
	private final Map _popups = new HashMap();

	/**
	 * Global popup menu. This contains items that are to be displayed
	 * in the popup menu no matter what items are selected in the tree.
	 */
	private final JPopupMenu _globalPopup = new JPopupMenu();

	private final List _globalActions = new ArrayList();

	/**
	 * Object to synchronize on so that only one node can be expanded at any
	 * one time.
	 */
	private Object _syncObject = new Object();

	/**
	 * String representation of the <TT>TreePath</TT> objects that have been
	 * expanded. The key is <TT>Treepath.toString()</TT> and the value
	 * is <TT>null</TT>.
	 */
	private Map _expandedPathNames = new HashMap();

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
	ObjectTree(ISession session)
	{
		super(new ObjectTreeModel(session));
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		_session = session;
		_model = (ObjectTreeModel)getModel();
		setModel(_model);

		addTreeExpansionListener(new NodeExpansionListener());

		setShowsRootHandles(true);

		// Add actions to the popup menu.
		ActionCollection actions = session.getApplication().getActionCollection();
		addToPopup(DatabaseObjectType.TABLE, actions.get(DropSelectedTablesAction.class));

		// Options for global popup menu.
		addToPopup(actions.get(RefreshObjectTreeAction.class));
		addToPopup(actions.get(RefreshObjectTreeItemAction.class));

		// Option to select default catalog only applies to sessions
		// that support catalogs.
		try
		{
			if (_session.getSQLConnection().getSQLMetaData().supportsCatalogs())
			{
				addToPopup(DatabaseObjectType.CATALOG,
							actions.get(SetDefaultCatalogAction.class));
			}
		}
		catch (Throwable th)
		{
			// Assume DBMS doesn't support catalogs.
			s_log.debug(th);
		}

		addToPopup(actions.get(CopySimpleObjectNameAction.class));
		addToPopup(actions.get(CopyQualifiedObjectNameAction.class));

		// Mouse listener used to display popup menu.
		addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent evt)
			{
				if (evt.isPopupTrigger())
				{
					showPopup(evt.getX(), evt.getY());
				}
			}
			public void mouseReleased(MouseEvent evt)
			{
				if (evt.isPopupTrigger())
				{
					showPopup(evt.getX(), evt.getY());
				}
			}
		});
		setDragEnabled(true);
		setTransferHandler(new ObjectTreeTransferHandler());
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
	 * Return the typed data model for this tree.
	 *
	 * @return	The typed data model for this tree.
	 */
	public ObjectTreeModel getTypedModel()
	{
		return _model;
	}

	/**
	 * Refresh tree.
	 */
	public void refresh()
	{
		// Clear cache in case metadata has changed.
		_session.getSQLConnection().getSQLMetaData().clearCache();

		final TreePath[] selectedPaths = getSelectionPaths();
		final Map selectedPathNames = new HashMap();
		if (selectedPaths != null)
		{
			for (int i = 0; i < selectedPaths.length; ++i)
			{
				selectedPathNames.put(selectedPaths[i].toString(), null);
			}
		}
		ObjectTreeNode root = _model.getRootObjectTreeNode();
		root.removeAllChildren();
		fireObjectTreeCleared();
		startExpandingTree(root, false, selectedPathNames);
		fireObjectTreeRefreshed();
	}

	/**
	 * Refresh the nodes currently selected in the object tree.
	 * TODO: Make this work with multiple nodes. Currently multiple threads
	 * will be created (one for each node selected) and it will be very
	 * very very bad.
	 */
	public void refreshSelectedNodes()
	{
		// Clear cache in case metadata has changed.
		_session.getSQLConnection().getSQLMetaData().clearCache();

		final TreePath[] selectedPaths = getSelectionPaths();
		ObjectTreeNode[] nodes = getSelectedNodes();
		final Map selectedPathNames = new HashMap();
		if (selectedPaths != null)
		{
			for (int i = 0; i < selectedPaths.length; ++i)
			{
				selectedPathNames.put(selectedPaths[i].toString(), null);
			}
		}
		clearSelection();
		nodes[0].removeAllChildren();
		startExpandingTree(nodes[0], false, selectedPathNames);
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

	/**
	 * Restore the expansion state of the tree starting at the passed node.
	 * The passed node is always expanded.
	 *
	 * @param	node	Node to restore expansion state from.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if null ObjectTreeNode passed.
	 */
	private void restoreExpansionState(ObjectTreeNode node,
		Map previouslySelectedTreePathNames, List selectedTreePaths)
	{
		if (node == null)
		{
			throw new IllegalArgumentException("ObjectTreeNode == null");
		}

		final TreePath nodePath = new TreePath(node.getPath());
		if (previouslySelectedTreePathNames.containsKey(nodePath.toString()))
		{
			selectedTreePaths.add(nodePath);
		}
		expandPath(nodePath);

		// Go through each child of the parent and see if it was previously
		// expanded. If it was recursively call this method in order to expand
		// the child.
		Iterator it = new EnumerationIterator(node.children());
		while (it.hasNext())
		{
			final ObjectTreeNode child = (ObjectTreeNode)it.next();
			final TreePath childPath = new TreePath(child.getPath());
			final String childPathName = childPath.toString();

			if (previouslySelectedTreePathNames.containsKey(childPathName))
			{
				selectedTreePaths.add(childPath);
			}

			if (_expandedPathNames.containsKey(childPathName))
			{
				restoreExpansionState(child, previouslySelectedTreePathNames, selectedTreePaths);
			}
		}
	}

	private void startExpandingTree(ObjectTreeNode node, boolean selectNode,
										Map selectedPathNames)
	{
		ExpansionController exp = new ExpansionController(node, selectNode, selectedPathNames);
		if (SwingUtilities.isEventDispatchThread())
		{
			_session.getApplication().getThreadPool().addTask(exp);
		}
		else
		{
			exp.run();
		}
	}

	private void expandNode(ObjectTreeNode node, boolean selectNode)
	{
		if (node == null)
		{
			throw new IllegalArgumentException("ObjectTreeNode == null");
		}
		// If node hasn't already been expanded.
		if (node.getChildCount() == 0)
		{
			// Add together the standard expanders for this node type and any
			// individual expanders that there are for the node and process them.
			final DatabaseObjectType dboType = node.getDatabaseObjectType();
			INodeExpander[] stdExpanders = _model.getExpanders(dboType);
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
					System.arraycopy(extraExpanders, 0, expanders, stdExpanders.length,
										extraExpanders.length);
				}
				new TreeLoader(node, expanders, selectNode).execute();
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

		final JPopupMenu pop = getPopup(dboType, true);
		pop.add(action);
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
		_globalPopup.add(action);
		_globalActions.add(action);

		for (Iterator it = _popups.values().iterator(); it.hasNext();)
		{
			JPopupMenu pop = (JPopupMenu)it.next();
			pop.add(action);
		}
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

		final JPopupMenu pop = getPopup(dboType, true);
		pop.add(menu);
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
		_globalPopup.add(menu);
		_globalActions.add(menu);

		for (Iterator it = _popups.values().iterator(); it.hasNext();)
		{
			JPopupMenu pop = (JPopupMenu)it.next();
			pop.add(menu);
		}
	}

	/**
	 * Get the popup menu for the passed database object type. If one
	 * doesn't exist then create one if requested to do so.

	 * @param	dboType		Database Object Type.
	 * @param	create		If <TT>true</TT> popup will eb created if it
	 *						doesn't exist.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>Action</TT> or
	 *			<TT>DatabaseObjectType</TT>thrown.
	 */
	private JPopupMenu getPopup(DatabaseObjectType dboType, boolean create)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("Null DatabaseObjectType passed");
		}
		IIdentifier key = dboType.getIdentifier();
		JPopupMenu pop = (JPopupMenu)_popups.get(key);
		if (pop == null && create)
		{
			pop = new JPopupMenu();
			_popups.put(key, pop);
			for (Iterator it = _globalActions.iterator(); it.hasNext();)
			{
				pop.add((Action)it.next());
			}
		}
		return pop;
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
		List list = new ArrayList();
		if (paths != null)
		{
			for (int i = 0; i < paths.length; ++i)
			{
				Object obj = paths[i].getLastPathComponent();
				if (obj instanceof ObjectTreeNode)
				{
					list.add(obj);
				}
			}
		}
		ObjectTreeNode[] ar = (ObjectTreeNode[])list.toArray(new ObjectTreeNode[list.size()]);
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

			JPopupMenu pop = null;
			if (sameType)
			{
				pop = getPopup(dboType, false);
			}
			if (pop == null)
			{
				pop = _globalPopup;
			}
			pop.show(this, x, y);
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

	private final class NodeExpansionListener implements TreeExpansionListener
	{
		public void treeExpanded(TreeExpansionEvent evt)
		{
			// Get the node to be expanded.
			final TreePath path = evt.getPath();
			final Object parentObj = path.getLastPathComponent();
			if (parentObj instanceof ObjectTreeNode)
			{
				startExpandingTree((ObjectTreeNode)parentObj, false, null);
				_expandedPathNames.put(path.toString(), null);
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
	private static class NodeComparator implements Comparator
	{
		public int compare(Object obj1, Object obj2)
		{
			return obj1.toString().compareToIgnoreCase(obj2.toString());
		}
	}

	private class ExpansionController implements Runnable
	{
		private final ObjectTreeNode _node;
		private final boolean _selectNode;
		private final Map _selectedPathNames;

		ExpansionController(ObjectTreeNode node, boolean selectNode,
					Map selectedPathNames)
		{
			super();
			_node = node;
			_selectNode = selectNode;
			_selectedPathNames = selectedPathNames;
		}

		public void run()
		{
			synchronized (ObjectTree.this._syncObject)
			{
				CursorChanger cursorChg = new CursorChanger(ObjectTree.this);
				cursorChg.show();
				try
				{
					expandNode(_node, _selectNode);
					if (_selectedPathNames != null)
					{
						List newlySelectedTreepaths = new ArrayList();
						restoreExpansionState(_node, _selectedPathNames, newlySelectedTreepaths);
						setSelectionPaths((TreePath[])newlySelectedTreepaths.toArray(new TreePath[newlySelectedTreepaths.size()]));
					}
				}
				finally
				{
					cursorChg.restore();
				}
			}
		}
	}

	/**
	 * This class actually loads the tree.
	 */
	private final class TreeLoader
	{
		private ObjectTreeNode _parentNode;
		private INodeExpander[] _expanders;
		private boolean _selectParentNode;

		TreeLoader(ObjectTreeNode parentNode, INodeExpander[] expanders,
					boolean selectParentNode)
		{
			super();
			_parentNode = parentNode;
			_expanders = expanders;
			_selectParentNode= selectParentNode;
		}

		void execute()
		{
			try
			{
				try
				{
					ObjectTreeNode loadingNode = showLoadingNode();
					try
					{
						loadChildren();
					}
					finally
					{
						_parentNode.remove(loadingNode);
					}
				}
				finally
				{
					fireStructureChanged(_parentNode);
					if (_selectParentNode)
					{
						clearSelection();
						setSelectionPath(new TreePath(_parentNode.getPath()));
					}
				}
			}
			catch (Throwable ex)
			{
				final String msg = "Error: " + _parentNode.toString();
				s_log.error(msg, ex);
				_session.getMessageHandler().showErrorMessage(msg + ": " + ex.toString());
			}
		}

		/**
		 * This adds a node to the tree that says "Loading..." in order to give
		 * feedback to the user.
		 */
		private ObjectTreeNode showLoadingNode()
		{
			IDatabaseObjectInfo doi = new DatabaseObjectInfo(null, null,
								"Loading...", DatabaseObjectType.OTHER,
								_session.getSQLConnection().getSQLMetaData());
			ObjectTreeNode loadingNode = new ObjectTreeNode(_session, doi);
			_parentNode.add(loadingNode);
			fireStructureChanged(_parentNode);
			return loadingNode;
		}

		/**
		 * This expands the parent node and shows all its children.
		 */
		private void loadChildren() throws SQLException
		{
			for (int i = 0; i < _expanders.length; ++i)
			{
				boolean nodeTypeAllowsChildren = false;
				DatabaseObjectType lastDboType = null;
				List list = _expanders[i].createChildren(_session, _parentNode);
				Iterator it = list.iterator();
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
								lastDboType = childNodeDboType;
								if (_model.getExpanders(childNodeDboType).length > 0)
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
		}

		/**
		 * Let the object tree model know that its structure has changed.
		 */
		private void fireStructureChanged(final ObjectTreeNode node)
		{
			ObjectTree.this._model.nodeStructureChanged(node);
		}
	}
}
