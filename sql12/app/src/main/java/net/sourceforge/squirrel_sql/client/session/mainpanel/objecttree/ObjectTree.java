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
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasColorProperties;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.CopyQualifiedObjectNameAction;
import net.sourceforge.squirrel_sql.client.session.action.CopySimpleObjectNameAction;
import net.sourceforge.squirrel_sql.client.session.action.DeleteSelectedTablesAction;
import net.sourceforge.squirrel_sql.client.session.action.EditWhereColsAction;
import net.sourceforge.squirrel_sql.client.session.action.FilterObjectsAction;
import net.sourceforge.squirrel_sql.client.session.action.RefreshObjectTreeItemAction;
import net.sourceforge.squirrel_sql.client.session.action.RefreshSchemaInfoAction;
import net.sourceforge.squirrel_sql.client.session.action.SQLFilterAction;
import net.sourceforge.squirrel_sql.client.session.action.SetDefaultCatalogAction;
import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.EnumerationIterator;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
/**
 * This is the tree showing the structure of objects in the database.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class ObjectTree extends JTree
{
    private static final long serialVersionUID = 1L;

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
	private final Map<IIdentifier, JPopupMenu> _popups = 
        new HashMap<IIdentifier, JPopupMenu>();

	/**
	 * Global popup menu. This contains items that are to be displayed
	 * in the popup menu no matter what items are selected in the tree.
	 */
	private final JPopupMenu _globalPopup = new JPopupMenu();

	private final List<Action> _globalActions = new ArrayList<Action>();

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
	private Map<String, Object> _expandedPathNames = new HashMap<String, Object>();

	/**
	 * Collection of listeners to this object tree.
	 */
	private EventListenerList _listenerList = new EventListenerList();

   private boolean _startExpandInThread = true;

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
      setRowHeight(getFontMetrics(getFont()).getHeight());
      _session = session;
      _model = (ObjectTreeModel)getModel();
      setModel(_model);

      addTreeExpansionListener(new NodeExpansionListener());

      addTreeSelectionListener(new TreeSelectionListener()
      {
         public void valueChanged(TreeSelectionEvent e)
         {
            if(null != e.getNewLeadSelectionPath())
            {
               scrollPathToVisible(e.getNewLeadSelectionPath());
            }
         }
      });

      setShowsRootHandles(true);

      // Add actions to the popup menu.
      final ActionCollection actions = session.getApplication().getActionCollection();

      // Options for global popup menu.
      addToPopup(actions.get(RefreshSchemaInfoAction.class));
      addToPopup(actions.get(RefreshObjectTreeItemAction.class));

      addToPopup(DatabaseObjectType.TABLE, actions.get(EditWhereColsAction.class));

      addToPopup(DatabaseObjectType.TABLE, actions.get(SQLFilterAction.class));
      addToPopup(DatabaseObjectType.VIEW, actions.get(SQLFilterAction.class));

      addToPopup(DatabaseObjectType.TABLE, actions.get(DeleteSelectedTablesAction.class));

      addToPopup(DatabaseObjectType.SESSION, actions.get(FilterObjectsAction.class));


      session.getApplication().getThreadPool().addTask(new Runnable() {
          public void run() {
            try
            {
                // Option to select default catalog only applies to sessions
                // that support catalogs.
                if (_session.getSQLConnection().getSQLMetaData().supportsCatalogs())
                {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            addToPopup(DatabaseObjectType.CATALOG,
                                       actions.get(SetDefaultCatalogAction.class));
                        }

                    });
                }
            }
            catch (Throwable th)
            {
                // Assume DBMS doesn't support catalogs.
                s_log.debug(th);
            }

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    addToPopup(actions.get(CopySimpleObjectNameAction.class));
                    addToPopup(actions.get(CopyQualifiedObjectNameAction.class));


                  addMouseListener(new ObjectTreeMouseListener());
                  setCellRenderer(new ObjectTreeCellRenderer(_model, _session));
                  ObjectTree.this.refresh(false);
                  ObjectTree.this.setSelectionPath(ObjectTree.this.getPathForRow(0));
                }
            });
          }
      });

      SQLAliasColorProperties colorProps = session.getAlias().getColorProperties();
      if (colorProps.isOverrideObjectTreeBackgroundColor()) {
      	int rgbValue = colorProps.getObjectTreeBackgroundColorRgbValue();
      	setBackground(new Color(rgbValue));
      }
   }

   // Mouse listener used to display popup menu.
   private class ObjectTreeMouseListener extends MouseAdapter {
      public void mousePressed(MouseEvent evt)
      {      	
      	 
          if (evt.isPopupTrigger())
          {
    			// If the user wants to select for Right mouse clicks then change the selection before popup 
         	// appears
         	 if (_session.getApplication().getSquirrelPreferences().getSelectOnRightMouseClick()) {         	 
	         	 TreePath path = ObjectTree.this.getPathForLocation(evt.getX(), evt.getY());
	         	 boolean alreadySelected = false;
	         	 TreePath[] selectedPaths = ObjectTree.this.getSelectionPaths();
	         	 if (selectedPaths != null) {	         	 
		         	 for (TreePath selectedPath : selectedPaths) {
		         		 if (path != null && path.equals(selectedPath)) {
		         			 alreadySelected = true;
		         			 break;
		         		 }
		         	 }
	         	 }
	         	 if (!alreadySelected) {
	         		 ObjectTree.this.setSelectionPath(path);
	         	 }
         	 }
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


            GUIUtils.processOnSwingEventThread(new Runnable()
            {
               public void run()
               {
                  refreshTree();
               }
            });
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
      final TreePath[] selectedPaths = getSelectionPaths();
      final Map<String, Object> selectedPathNames = 
          new HashMap<String, Object>();
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
      startExpandingTree(root, false, selectedPathNames, false);
      fireObjectTreeRefreshed();
   }

   /**
    * Refresh the nodes currently selected in the object tree.
    */
   public void refreshSelectedNodes()
   {

      final TreePath[] selectedPaths = getSelectionPaths();
      ObjectTreeNode[] nodes = getSelectedNodes();
      final Map<String, Object> selectedPathNames = 
          new HashMap<String, Object>();
      if (selectedPaths != null)
      {
         for (int i = 0; i < selectedPaths.length; ++i)
         {
            selectedPathNames.put(selectedPaths[i].toString(), null);
         }
      }
      clearSelection();


      DefaultMutableTreeNode parent = (DefaultMutableTreeNode) nodes[0].getParent();

      if (parent != null)
      {
         parent.removeAllChildren();
         startExpandingTree((ObjectTreeNode) parent, false, selectedPathNames, true);
      }
      else
      {
         nodes[0].removeAllChildren();
         startExpandingTree(nodes[0], false, selectedPathNames, true);
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
	                                   Map<String, Object> previouslySelectedTreePathNames, 
                                       List<TreePath> selectedTreePaths)
	{
		if (node == null)
		{
			throw new IllegalArgumentException("ObjectTreeNode == null");
		}

		final TreePath nodePath = new TreePath(node.getPath());
        if (matchKeyPrefix(previouslySelectedTreePathNames, node, nodePath.toString()))
		{
			selectedTreePaths.add(nodePath);
		}


      try
      {
         _startExpandInThread = false;
         expandPath(nodePath);
      }
      finally
      {
         _startExpandInThread = true;
      }



      // Go through each child of the parent and see if it was previously
		// expanded. If it was recursively call this method in order to expand
		// the child.
      @SuppressWarnings("unchecked")
      Enumeration<ObjectTreeNode> childEnumeration = 
          (Enumeration<ObjectTreeNode>) node.children();
		Iterator<ObjectTreeNode> it = 
            new EnumerationIterator<ObjectTreeNode>(childEnumeration);
		while (it.hasNext())
		{
			final ObjectTreeNode child = it.next();
			final TreePath childPath = new TreePath(child.getPath());
			final String childPathName = childPath.toString();

         if (matchKeyPrefix(previouslySelectedTreePathNames, child, childPathName))
			{
				selectedTreePaths.add(childPath);
			}

			if (_expandedPathNames.containsKey(childPathName))
			{
				restoreExpansionState(child, previouslySelectedTreePathNames, selectedTreePaths);
         }
		}
	}

    /**
     * This is to handle the case where the user has enabled showRowCounts and 
     * the table/view name as it appeared before is different only because the 
     * number of rows has changed. For example, suppose a user deletes records
     * in a table "foo" with 100 rows then refreshes the tree.  The tree node
     * before the delete looks like foo(100) and after looks like foo(0).  We 
     * want to strip off the (...) and test to see if the selected path "foo"
     * is the same before the delete as after.  This way, when the user refreshes
     * "foo(...)", then it is still selected after the refresh.
     * 
     * @param map
     * @param pattern
     * @return
     */
    protected boolean matchKeyPrefix(Map<String, Object> map, ObjectTreeNode node, String path) {
        // We only show row counts for tables and views.  Other objects won't 
        // be affected by changing row counts.
        if (node.getDatabaseObjectType() != DatabaseObjectType.TABLE
                && node.getDatabaseObjectType() != DatabaseObjectType.VIEW) 
        {
            return map.containsKey(path);
        }
        Set<String> s = map.keySet();
        Iterator<String> i = s.iterator();
        String pathPrefix = path;
        if (path.indexOf("(") != -1) {
            pathPrefix = path.substring(0, path.lastIndexOf("("));
        }
        boolean result = false;
        while (i.hasNext()) {
            String key = i.next();
            String keyPrefix = key;
            if (key.indexOf("(") != -1) {
                keyPrefix = key.substring(0, key.lastIndexOf("("));
            }
            if (keyPrefix.equals(pathPrefix)) {
                result = true;
                break;
            }
        }
        return result;
    }
        
	private void startExpandingTree(ObjectTreeNode node,
                                   boolean selectNode,
                                   Map<String, Object> selectedPathNames,
                                   boolean refreshSchemaInfo
   )
	{
		ExpansionController exp = new ExpansionController(node, selectNode, selectedPathNames, refreshSchemaInfo);
      exp.run();
	}

	private void expandNode(ObjectTreeNode node, boolean selectNode)
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
				new TreeLoader(this._session, this, this._model, node, expanders, selectNode).execute();
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

		for (Iterator<JPopupMenu> it = _popups.values().iterator(); it.hasNext();)
		{
			JPopupMenu pop = it.next();
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
		_globalActions.add(menu.getAction());

		for (Iterator<JPopupMenu> it = _popups.values().iterator(); it.hasNext();)
		{
			JPopupMenu pop = it.next();
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
		JPopupMenu pop = _popups.get(key);
		if (pop == null && create)
		{
			pop = new JPopupMenu();
			_popups.put(key, pop);
			for (Iterator<Action> it = _globalActions.iterator(); it.hasNext();)
			{
				pop.add(it.next());
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
        ArrayList<ITableInfo> result = new ArrayList<ITableInfo>(); 
        for (int i = 0; i < nodes.length; ++i)
        {
            if (nodes[i].getDatabaseObjectType() == DatabaseObjectType.TABLE) {
                result.add((ITableInfo)nodes[i].getDatabaseObjectInfo());
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

	public void dispose()
	{
		// Menues that are also shown in the main window Session menu might
		// be in this popup. If we don't remove them, the Session won't be Garbage Collected.
		_globalPopup.removeAll();
		_globalPopup.setInvoker(null);
		_globalActions.clear();
		for(Iterator<JPopupMenu> i=_popups.values().iterator(); i.hasNext();)
		{
			JPopupMenu popup = i.next();
			popup.removeAll();
			popup.setInvoker(null);
		}
		_popups.clear();
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
				startExpandingTree((ObjectTreeNode)parentObj, false, null, false);
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
	private static class NodeComparator implements Comparator<ObjectTreeNode>,
                                                   Serializable
	{
        private static final long serialVersionUID = 1L;

        public int compare(ObjectTreeNode obj1, ObjectTreeNode obj2)
		{
			return obj1.toString().compareToIgnoreCase(obj2.toString());
		}
	}

	private class ExpansionController implements Runnable
	{
		private final ObjectTreeNode _node;
		private final boolean _selectNode;
		private final Map<String, Object> _selectedPathNames;
      private boolean _refreshSchemaInfo;

      ExpansionController(ObjectTreeNode node, 
                          boolean selectNode, 
                          Map<String, Object> selectedPathNames, 
                          boolean refreshSchemaInfo)
      {
         super();
         _node = node;
         _selectNode = selectNode;
         _selectedPathNames = selectedPathNames;
         _refreshSchemaInfo = refreshSchemaInfo;
      }

		public void run()
		{
			synchronized (ObjectTree.this._syncObject)
			{
				CursorChanger cursorChg = new CursorChanger(ObjectTree.this);
				cursorChg.show();
				try
				{
               if(_refreshSchemaInfo)
               {
                  _session.getSchemaInfo().reload(_node.getDatabaseObjectInfo());
               }

               expandNode(_node, _selectNode);
					if (_selectedPathNames != null)
					{
						final List<TreePath> newlySelectedTreepaths = new ArrayList<TreePath>();
						
						GUIUtils.processOnSwingEventThread(new Runnable()
						{
							public void run()
							{
                        restoreExpansionState(_node, _selectedPathNames, newlySelectedTreepaths);
                        setSelectionPaths(newlySelectedTreepaths.toArray(new TreePath[newlySelectedTreepaths.size()]));
                     }
						});
					}
				}
				finally
				{
					cursorChg.restore();
				}
			}
		}
	}
}
