package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;
/*
 * Copyright (C) 2002-2004 Colin Bell
 * colbell@users.sourceforge.net
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

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseDataSetTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.IObjectTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database.*;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.procedure.ProcedureColumnsTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.*;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.session.schemainfo.FilterMatcher;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetUpdateableTableModelListener;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.activation.DataHandler;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This is the panel for the Object Tree tab.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ObjectTreePanel extends JPanel implements IObjectTreeAPI
{
   /** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(ObjectTreePanel.class);

	/** The <TT>IIdentifier</TT> that uniquely identifies this object. */
	private IIdentifier _id = IdentifierFactory.getInstance().createIdentifier();

	/** Current session. */
	private transient ISession _session;

	/** Tree of objects within the database. */
	private ObjectTree _tree;

	/** Split pane between the object tree and the data panel. */
	private final JSplitPane _splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

	/**
	 * Empty data panel. Used if the object selected in the object
	 * tree doesn't require a panel.
	 */
	private final ObjectTreeTabbedPane _emptyTabPane;

	/**
	 * Contains instances of <TT>ObjectTreeTabbedPane</TT> objects keyed by
	 * the node type. I.E. the tabbed folders for each node type are kept here.
	 */
	private final Map<IIdentifier, ObjectTreeTabbedPane> _tabbedPanes = 
        new HashMap<IIdentifier, ObjectTreeTabbedPane>();

	/** Listens to changes in session properties. */
	private SessionPropertiesListener _propsListener;

	/** Listens to changes in each of the tabbed folders. */
	private TabbedPaneListener _tabPnlListener;

	private transient ObjectTreeSelectionListener _objTreeSelLis = null;

   private ObjectTreeTabbedPane _selectedObjTreeTabbedPane = null;
   
   /** used to save and restore previously selected object tree paths */ 
   private TreePath[] previouslySelectedPaths = null;

   private FindInObjectTreeController _findInObjectTreeController;

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
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		_session = session;

		_emptyTabPane = new ObjectTreeTabbedPane(_session);

      _findInObjectTreeController = new FindInObjectTreeController(_session);

      createGUI();

      init();

      initDnD();

   }

   private void initDnD()
   {
      _tree.setTransferHandler(new TransferHandler("DragedTreeNode")
      {
         @Override
         protected Transferable createTransferable(JComponent c)
         {
            return new DataHandler(new ObjectTreeDndTransfer(getSelectedTables(), _session.getIdentifier()), DataFlavor.javaJVMLocalObjectMimeType);
         }

         public int getSourceActions(JComponent c)
         {
            return COPY;
         }

      });
      _tree.setDragEnabled(true);
   }

   private void init()
   {
      try
      {
         // Register tabs to display in the details panel for database nodes.
         addDetailTab(DatabaseObjectType.SESSION, new MetaDataTab());
         addDetailTab(DatabaseObjectType.SESSION, new ConnectionStatusTab());

         try
         {
            SQLDatabaseMetaData md =
               _session.getSQLConnection().getSQLMetaData();
            if (md.supportsCatalogs())
            {
               _addDetailTab(DatabaseObjectType.SESSION, new CatalogsTab());
            }
         }
         catch (Throwable th)
         {
            s_log.error("Error in supportsCatalogs()", th);
         }

         try
         {
            SQLDatabaseMetaData md =
               _session.getSQLConnection().getSQLMetaData();
            if (md.supportsSchemas())
            {
               _addDetailTab(DatabaseObjectType.SESSION, new SchemasTab());
            }
         }
         catch (Throwable th)
         {
            s_log.error("Error in supportsCatalogs()", th);
         }
         _addDetailTab(DatabaseObjectType.SESSION, new TableTypesTab());
         _addDetailTab(DatabaseObjectType.SESSION, new DataTypesTab());
         _addDetailTab(DatabaseObjectType.SESSION, new NumericFunctionsTab());
         _addDetailTab(DatabaseObjectType.SESSION, new StringFunctionsTab());
         _addDetailTab(DatabaseObjectType.SESSION, new SystemFunctionsTab());
         _addDetailTab(DatabaseObjectType.SESSION, new TimeDateFunctionsTab());
         _addDetailTab(DatabaseObjectType.SESSION, new KeywordsTab());

         // Register tabs to display in the details panel for catalog nodes.
         _addDetailTab(DatabaseObjectType.CATALOG, new DatabaseObjectInfoTab());

         // Register tabs to display in the details panel for schema nodes.
         _addDetailTab(DatabaseObjectType.SCHEMA, new DatabaseObjectInfoTab());

         _addDetailTabForTableLikeObjects(DatabaseObjectType.TABLE);
         _addDetailTabForTableLikeObjects(DatabaseObjectType.VIEW);

         // Register tabs to display in the details panel for procedure nodes.
         _addDetailTab(DatabaseObjectType.PROCEDURE, new DatabaseObjectInfoTab());
         _addDetailTab(DatabaseObjectType.PROCEDURE, new ProcedureColumnsTab());

         // Register tabs to display in the details panel for UDT nodes.
         _addDetailTab(DatabaseObjectType.UDT, new DatabaseObjectInfoTab());

         _session.getSchemaInfo().addSchemaInfoUpdateListener(new net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfoUpdateListener()
         {
            public void schemaInfoUpdated()
            {
               refreshTree(false);
            }
         });

      }
      catch (Throwable th)
      {
         s_log.error("Error doing background initalization of Object tree" , th);
      }
   }

   private void _addDetailTabForTableLikeObjects(final DatabaseObjectType type)
   {
       GUIUtils.processOnSwingEventThread(new Runnable() {
          public void run() {
              addDetailTabForTableLikeObjects(type);
          }
       });
   }
    
   private void addDetailTabForTableLikeObjects(DatabaseObjectType type)
   {
      // Register tabs to display in the details panel for table nodes.
      addDetailTab(type, new DatabaseObjectInfoTab());

      ContentsTab conttentsTab = new ContentsTab(this);
      conttentsTab.addListener(new DataSetUpdateableTableModelListener()
      {
         public void forceEditMode(boolean mode)
         {
            onForceEditMode(mode);
         }
      });
      addDetailTab(type, conttentsTab);

      addDetailTab(type, new RowCountTab());
      addDetailTab(type, new ColumnsTab());
      addDetailTab(type, new PrimaryKeyTab());
      addDetailTab(type, new ExportedKeysTab());
      addDetailTab(type, new ImportedKeysTab());
      addDetailTab(type, new IndexesTab());
      addDetailTab(type, new TablePriviligesTab());
      addDetailTab(type, new ColumnPriviligesTab());
      addDetailTab(type, new RowIDTab());
      addDetailTab(type, new VersionColumnsTab());
   }

   /**
	 * Return the unique identifier for this object.
	 *
	 * @return the unique identifier for this object.
	 */
	public IIdentifier getIdentifier()
	{
		return _id;
	}

	public void addNotify()
	{
		super.addNotify();
		_tabPnlListener = new TabbedPaneListener();
		_propsListener = new SessionPropertiesListener();
		_session.getProperties().addPropertyChangeListener(_propsListener);

		Iterator<ObjectTreeTabbedPane> it = _tabbedPanes.values().iterator();
		while (it.hasNext())
		{
			//setupTabbedPane((ObjectTreeTabbedPane)it.next());
			ObjectTreeTabbedPane ottp = it.next();
			ottp.getTabbedPane().addChangeListener(_tabPnlListener);
		}

		_objTreeSelLis = new ObjectTreeSelectionListener();
		_tree.addTreeSelectionListener(_objTreeSelLis);
	}

	public void removeNotify()
	{
		super.removeNotify();

		if (_propsListener != null)
		{
			_session.getProperties().removePropertyChangeListener(_propsListener);
			_propsListener = null;
		}

		Iterator<ObjectTreeTabbedPane> it = _tabbedPanes.values().iterator();
		while (it.hasNext())
		{
			ObjectTreeTabbedPane pane = it.next();
			pane.getTabbedPane().removeChangeListener(_tabPnlListener);
		}
		_tabPnlListener = null;
		if (_objTreeSelLis != null)
		{
			_tree.removeTreeSelectionListener(_objTreeSelLis);
			_objTreeSelLis = null;
		}
	}

	/**
	 * Add an expander for the specified database object type.
	 *
	 * @param	dboType		Database object type.
	 * @param	expander	Expander called to add children to a parent node.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>DatabaseObjectType</TT>
	 * 			or <TT>INodeExpander</TT> passed.
	 */
	public void addExpander(DatabaseObjectType dboType, INodeExpander expander)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("Null DatabaseObjectType passed");
		}
		if (expander == null)
		{
			throw new IllegalArgumentException("Null INodeExpander passed");
		}
		_tree.getTypedModel().addExpander(dboType, expander);
	}

   /**
    * Expands the specified tree node.
    * 
    * @param node the tree node to expand
    */    
    public void expandNode(ObjectTreeNode node) {
        IDatabaseObjectInfo info = node.getDatabaseObjectInfo();
        TreePath path = getTreePath(info.getCatalogName(), 
                                    info.getSchemaName(), 
                                    new FilterMatcher(info.getSimpleName(), null));    
        _tree.fireTreeExpanded(path);
    }
    
    private void _addDetailTab(final DatabaseObjectType dboType, 
                               final IObjectTab tab) 
    {
        GUIUtils.processOnSwingEventThread(new Runnable() {
            public void run() {
                addDetailTab(dboType, tab);
            }
        });
    }
    
	/**
	 * Add a tab to be displayed in the detail panel for the passed
	 * database object type type.
	 *
	 * @param	dboType		Database Object type.
	 * @param	tab			Tab to be displayed.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown when a <TT>null</TT> <TT>DatabaseObjectType</TT> or
	 * 			<TT>IObjectPanelTab</TT> passed.
	 */
	public void addDetailTab(DatabaseObjectType dboType, IObjectTab tab)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("Null DatabaseObjectType passed");
		}
		if (tab == null)
		{
			throw new IllegalArgumentException("IObjectPanelTab == null");
		}

		getOrCreateObjectPanelTabbedPane(dboType).addObjectPanelTab(tab);
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
		_tree.addTreeSelectionListener(lis);
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
		_tree.removeTreeSelectionListener(lis);
	}

	/**
	 * Add a listener to the object tree.
	 *
	 * @param	lis		The <TT>ObjectTreeListener</TT> you want added.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ObjectTreeListener</TT> passed.
	 */
	public void addObjectTreeListener(IObjectTreeListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("IObjectTreeListener == null");
		}
		_tree.addObjectTreeListener(lis);
	}

	/**
	 * Remove a listener from the object tree.
	 *
	 * @param	lis		The <TT>ObjectTreeListener</TT> you want removed.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ObjectTreeListener</TT> passed.
	 */
	public void removeObjectTreeListener(IObjectTreeListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("IObjectTreeListener == null");
		}
		_tree.removeObjectTreeListener(lis);
	}

	/**
	 * Add an item to the popup menu for the specified database object type
	 * in the object tree.
	 *
	 * @param	dboType		Database Object type.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</passed> <TT>DatabaseObjectType</TT>
	 * 			or <TT>Action</TT> passed.
	 */
	public void addToPopup(DatabaseObjectType dboType, Action action)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("Null DatabaseObjectType passed");
		}
		if (action == null)
		{
			throw new IllegalArgumentException("Null Action passed");
		}
		_tree.addToPopup(dboType, action);
	}

	/**
	 * Add an item to the popup menu for all node types in the object
	 * tree.
	 *
	 * @param	action		Action to add to menu.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>Action</TT> passed.
	 */
	public void addToPopup(Action action)
	{
		if (action == null)
		{
			throw new IllegalArgumentException("Null Action passed");
		}
		_tree.addToPopup(action);
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
		_tree.addToPopup(dboType, menu);
	}

	/**
	 * Add an hierarchical menu to the popup menu for all node types.
	 *
	 * @param	menu	<TT>JMenu</TT> to add to menu.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>JMenu</TT> thrown.
	 */
	public void addToPopup(JMenu menu)	{
		if (menu == null)
		{
			throw new IllegalArgumentException("JMenu == null");
		}
		_tree.addToPopup(menu);
	}

   public ISession getSession()
   {
      return _session;
   }

	/**
	 * Return an array of the currently selected nodes.
	 *
	 * @return array of <TT>ObjectTreeNode</TT> objects.
	 */
	public ObjectTreeNode[] getSelectedNodes()
	{
		return _tree.getSelectedNodes();
	}

   /**
    * Return a type-safe list of the currently selected database tables
    * 
    * @return list of <TT>ITableInfo</TT> objects.
    */
   public List<ITableInfo> getSelectedTables() {
      return _tree.getSelectedTables();
   }
   
   /**
    * Saves the tree paths that are currently selected.  These can then be 
    * restored with restoreSavedSelectedPaths.
    */
   public void saveSelectedPaths() {
      previouslySelectedPaths = _tree.getSelectionPaths();
   }
   
   /**
    * Used to restore selected tree paths that were saved with saveSelectedPaths.
    */
   public void restoreSavedSelectedPaths() {
      _tree.setSelectionPaths(previouslySelectedPaths);
      _tree.requestFocusInWindow();
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
	 * Retrieve details about all object types that can be in this
	 * tree.
	 *
	 * @return	DatabaseObjectType[]	Array of object type info objects.
	 */
	public DatabaseObjectType[] getDatabaseObjectTypes()
	{
		return _tree.getTypedModel().getDatabaseObjectTypes();
	}

	/**
	 * Refresh object tree.
	 */
	public void refreshTree()
	{
      refreshTree(false);
   }

   public void refreshTree(boolean reloadSchemaInfo)
   {
      _tree.refresh(reloadSchemaInfo);
   }

   /**
	 * Refresh the nodes currently selected in the object tree.
	 */
	public void refreshSelectedNodes()
	{
		_tree.refreshSelectedNodes();
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

	public IObjectTab getTabbedPaneIfSelected(DatabaseObjectType dbObjectType, String title)
	{
		return getTabbedPane(dbObjectType).getTabIfSelected(title);
	}

    /**
     * Calls refreshComponent on the selected tab in the current 
     * ObjectTreeTabbedPane, if the selected tab happens to be a BaseDataSetTab
     * type. 
     * 
     * @throws DataSetException if there was a problem refreshing the component.
     */
    public void refreshSelectedTab() throws DataSetException 
    {
        if (_selectedObjTreeTabbedPane != null) {
            IObjectTab tab= _selectedObjTreeTabbedPane.getSelectedTab();
            if (tab != null) {
                if (tab instanceof BaseDataSetTab) {
                    BaseDataSetTab btab = (BaseDataSetTab) tab;
                    btab.refreshComponent();
                }
            }        
        }
    }
    
   /**
    * Tries to locate the object given by the paramteres in the Object tree.
    * The first matching object found is selected.
    *
    * @param catalog null means any catalog
    * @param schema null means any schema
    * @return true if the Object was found and selected.
    */
   public boolean selectInObjectTree(String catalog, String schema, FilterMatcher objectMatcher)
   {
      if ("".equals(objectMatcher.getMetaDataMatchString())) {
          return false;
      }

      TreePath treePath = getTreePath(catalog, schema, objectMatcher);
      if(null != treePath)
      {
         _tree.setSelectionPath(treePath);
         _tree.scrollPathToVisible(treePath);
         return true;
      }
      else
      {
         return false;
      }
   }

   /**
    * Get the TreePath to the node with the specified catalog, schema and 
    * object name.
    * 
    * @param catalog the catalog that the node is located in - can be null
    * @param schema the schema that the node is located in - can be null
    * @param object display name of the node
    *
    * @return the TreePath to the node with the specified criteria, or the root
    *         node if a node with matching characteristics isn't found.
    */
   private TreePath getTreePath(String catalog, String schema, FilterMatcher objectMatcher) {
       ObjectTreeModel otm = (ObjectTreeModel) _tree.getModel();
       TreePath treePath = 
           otm.getPathToDbInfo(catalog, 
                               schema, 
                               objectMatcher,
                               (ObjectTreeNode) otm.getRoot(), 
                               false);
       if(null == treePath)
       {
          treePath = otm.getPathToDbInfo(catalog, 
                                         schema, 
                                         objectMatcher,
                                         (ObjectTreeNode) otm.getRoot(), 
                                         true);
       }
       return treePath;
   }
   
   /**
	 * Add a known database object type to the object tree.
	 *
	 * @param	dboType		The new database object type.
	 */
	public void addKnownDatabaseObjectType(DatabaseObjectType dboType)
	{
		_tree.getTypedModel().addKnownDatabaseObjectType(dboType);
	}

	/**
	 * Set the panel to be shown in the data area for the passed
	 * path.
	 *
	 * @param	path	path of node currently selected.
	 */
	private void setSelectedObjectPanel(final TreePath path)
	{
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
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
		});
	}

	/**
	 * Set the panel in the data area to that passed.
	 *
	 */
	private void setSelectedObjectPanel(ObjectTreeTabbedPane pane)
	{
        _selectedObjTreeTabbedPane = pane;
		JTabbedPane comp = null;
		if (pane != null)
		{
			comp = pane.getTabbedPane();
		}
		if (comp == null)
		{
			comp = _emptyTabPane.getTabbedPane();
		}

		int divLoc = _splitPane.getDividerLocation();
		Component existing = _splitPane.getRightComponent();
		if (existing != null)
		{
			_splitPane.remove(existing);
		}
		_splitPane.add(comp, JSplitPane.RIGHT);
		_splitPane.setDividerLocation(divLoc);

		if (pane != null)
		{
			pane.selectCurrentTab();
		}
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

		ObjectTreeTabbedPane tabPane = getTabbedPane(node.getDatabaseObjectType());
		if (tabPane != null)
		{
			return tabPane;
		}

		return _emptyTabPane;
	}

	/**
	 * Return the tabbed pane for the passed object tree node type.
	 *
	 * @param	dboType		The database object type we are getting a tabbed
	 *						pane for.
	 *
	 * @return		the <TT>ObjectTreeTabbedPane</TT> for the passed database object
	 *				type.
	 */
	private ObjectTreeTabbedPane getTabbedPane(DatabaseObjectType dboType)
	{
		return _tabbedPanes.get(dboType.getIdentifier());
	}

	/**
	 * Return the tabbed pane for the passed database object type. If one
	 * doesn't exist then create it.
	 *
	 * @param	dboType		The database object type we are getting a tabbed
	 *						pane for.
	 *
	 * @return	the <TT>List</TT> containing all the <TT>IObjectPanelTab</TT>
	 * 			instances for the passed object tree node type.
	 */
	private ObjectTreeTabbedPane getOrCreateObjectPanelTabbedPane(DatabaseObjectType dboType)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("Null DatabaseObjectType passed");
		}

		final IIdentifier key = dboType.getIdentifier();
		ObjectTreeTabbedPane tabPane = _tabbedPanes.get(key);
		if (tabPane == null)
		{
			tabPane = new ObjectTreeTabbedPane(_session);
			setupTabbedPane(tabPane);
			_tabbedPanes.put(key, tabPane);
		}
		return tabPane;
	}

	/**
	 * Create the user interface.
	 */
	private void createGUI()
	{
		setLayout(new BorderLayout());

		_tree = new ObjectTree(_session);

		_splitPane.setOneTouchExpandable(true);
		_splitPane.setContinuousLayout(true);

      LeftPanel leftPanel = new LeftPanel();
      leftPanel.setMinimumSize(new Dimension(50,50));
      _splitPane.add(leftPanel, JSplitPane.LEFT);
		add(_splitPane, BorderLayout.CENTER);
		_splitPane.setDividerLocation(200);

		_tree.setSelectionRow(0);
	}

	private synchronized void propertiesHaveChanged(String propName)
	{
		if (propName == null
			|| propName.equals(SessionProperties.IPropertyNames.META_DATA_OUTPUT_CLASS_NAME)
			|| propName.equals(SessionProperties.IPropertyNames.TABLE_CONTENTS_OUTPUT_CLASS_NAME)
			|| propName.equals(SessionProperties.IPropertyNames.SQL_RESULTS_OUTPUT_CLASS_NAME)
			|| propName.equals(SessionProperties.IPropertyNames.OBJECT_TAB_PLACEMENT))
		{
			final SessionProperties props = _session.getProperties();

			Iterator<ObjectTreeTabbedPane> it = _tabbedPanes.values().iterator();
			while (it.hasNext())
			{
				ObjectTreeTabbedPane pane = it.next();

				if (propName == null
					|| propName.equals(SessionProperties.IPropertyNames.META_DATA_OUTPUT_CLASS_NAME)
					|| propName.equals(SessionProperties.IPropertyNames.TABLE_CONTENTS_OUTPUT_CLASS_NAME)
					|| propName.equals(SessionProperties.IPropertyNames.SQL_RESULTS_OUTPUT_CLASS_NAME))
				{
					pane.rebuild();
				}
				if (propName == null
					|| propName.equals(SessionProperties.IPropertyNames.OBJECT_TAB_PLACEMENT))
				{
					pane.getTabbedPane().setTabPlacement(props.getObjectTabPlacement());
				}
			}
		}
	}


   private void onForceEditMode(boolean editable)
   {
      Iterator<ObjectTreeTabbedPane> it = _tabbedPanes.values().iterator();
      while (it.hasNext())
      {
         ObjectTreeTabbedPane pane = it.next();
         pane.rebuild();

      }
   }



	private void setupTabbedPane(ObjectTreeTabbedPane pane)
	{
		final SessionProperties props = _session.getProperties();
		pane.rebuild();
		final JTabbedPane p = pane.getTabbedPane();
		p.setTabPlacement(props.getObjectTabPlacement());
		p.addChangeListener(_tabPnlListener);
	}

	public void sessionWindowClosing()
	{
      _findInObjectTreeController.dispose();
      _tree.dispose();
	}

   public FindInObjectTreeController getFindController()
   {
      return _findInObjectTreeController;   
   }

   @Override
   public Component getDetailTabComp()
   {
      return _splitPane.getRightComponent();
   }

   private final class LeftPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;

		LeftPanel()
		{
			super(new BorderLayout());
         add(_findInObjectTreeController.getFindInObjectTreePanel(), BorderLayout.NORTH);
         JScrollPane sp = new JScrollPane();
			sp.setBorder(BorderFactory.createEmptyBorder());
			sp.setViewportView(_tree);
			sp.setPreferredSize(new Dimension(200, 200));
			add(sp, BorderLayout.CENTER);
		}
	}

	/**
	 * This class listens for changes in the node selected in the tree
	 * and displays the appropriate detail panel for the node.
	 */
	private final class ObjectTreeSelectionListener
		implements TreeSelectionListener
	{
		public void valueChanged(TreeSelectionEvent evt)
		{
			setSelectedObjectPanel(evt.getNewLeadSelectionPath());
		}
	}

	/**
	 * Listen for changes in session properties.
	 */
	private class SessionPropertiesListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			propertiesHaveChanged(evt.getPropertyName());
		}
	}

	/**
	 * When a different tab is selected in one of the tabbed panels then
	 * refresh the newly selected tab.
	 */
	private static class TabbedPaneListener implements ChangeListener
	{
		public void stateChanged(ChangeEvent evt)
		{
			final Object src = evt.getSource();
			if (!(src instanceof JTabbedPane))
			{
				StringBuilder buf = new StringBuilder();
				buf.append("Source object in TabbedPaneListener was not a JTabbedpane")
					.append(" - it was ")
					.append(src == null ? "null" : src.getClass().getName());
				s_log.error(buf.toString());
				return;
			}
			JTabbedPane tabPane = (JTabbedPane)src;

			Object prop = tabPane.getClientProperty(ObjectTreeTabbedPane.IClientPropertiesKeys.TABBED_PANE_OBJ);
			if (!(prop instanceof ObjectTreeTabbedPane))
			{
				StringBuilder buf = new StringBuilder();
				buf.append("Client property in JTabbedPane was not an ObjectTreeTabbedPane")
					.append(" - it was ")
					.append(prop == null ? "null" : prop.getClass().getName());
				s_log.error(buf.toString());
				return;
			}

			((ObjectTreeTabbedPane)prop).selectCurrentTab();
		}
	}

    /**
     * @see net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI#selectRoot()
     */
    public void selectRoot() {
        // TODO: Figure out why the selection of the root node in the object tree
        // is undone if we don't delay. It seems like some other thread racing 
        // to set the selected node to none.  When this happens, the detail pane
        // for the root node disappears when a session is first opened, or when
        // an ObjectTree frame is created.  This is a really crappy and unreliable
        // way to fix this problem, but it hides the problem for now.  A better 
        // understanding of the architecture of session creation is required 
        // before a proper solution can be attempted. RMM 20051217
        _session.getApplication().getThreadPool().addTask(new delaySelectionRunnable());
    }

    private class delaySelectionRunnable implements Runnable {
        public void run() {
      	  Utilities.sleep(50);
      	  SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    TreePath rootPath = _tree.getPathForRow(0);
                    _tree.setSelectionPath(rootPath);
                }
            });            
        }
    }
}
