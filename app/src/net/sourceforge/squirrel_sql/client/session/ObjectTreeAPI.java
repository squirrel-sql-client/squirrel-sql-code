package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2002-2003 Colin Bell and Johan Compagner
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
import java.sql.SQLException;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionListener;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.IObjectTreeListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.IObjectTab;
/**
 * This class is the API through which plugins can work with the object tree.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class ObjectTreeAPI implements IObjectTreeAPI
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(ObjectTreeAPI.class);

	/** Session containing the object tree. */
	private ISession _session;

	/** <TT>true</TT> until the object tree has been built the first time. */
	private boolean _firstRefresh = true;

	/**
	 * Ctor specifying the session.
	 *
	 * @param	session	<TT>ISession</TT> containing the object tree.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <T>null</TT> <TT>ISession</TT> passed.
	 */
	ObjectTreeAPI(ISession session)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		_session = session;
	}

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
	public synchronized void addExpander(DatabaseObjectType dboType,
													INodeExpander expander)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("DatabaseObjectType == null");
		}
		if (expander == null)
		{
			throw new IllegalArgumentException("INodeExpander == null");
		}
		_session.getSessionSheet().getObjectTreePanel().addExpander(dboType, expander);
	}

	/**
	 * Add a tab to be displayed in the detail panel for the passed
	 * object tree node type.
	 *
	 * @param	dboType		Database object type.
	 * @param	tab			Tab to be displayed.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown when a <TT>null</TT> <TT>DatabaseObjectType</TT> or
	 * 			<TT>IObjectTab</TT> passed.
	 */
	public void addDetailTab(DatabaseObjectType dboType,
									IObjectTab tab)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("DatabaseObjectType == null");
		}
		if (tab == null)
		{
			throw new IllegalArgumentException("IObjectTab == null");
		}
		_session.getSessionSheet().getObjectTreePanel().addDetailTab(dboType, tab);
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
		_session.getSessionSheet().getObjectTreePanel().addTreeSelectionListener(lis);
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
		_session.getSessionSheet().getObjectTreePanel().removeTreeSelectionListener(lis);
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
		_session.getSessionSheet().getObjectTreePanel().addObjectTreeListener(lis);
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
		_session.getSessionSheet().getObjectTreePanel().removeObjectTreeListener(lis);
	}

	/**
	 * Add an item to the popup menu for the specified node type.
	 *
	 * @param	dboType		Database object type.
	 * @param	action		Action to add to menu.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>DatabaseObjectType</TT>
	 * 			<TT>Action</TT> thrown.
	 */
	public synchronized void addToPopup(DatabaseObjectType dboType, Action action)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("DatabaseObjectType == null");
		}
		if (action == null)
		{
			throw new IllegalArgumentException("Action == null");
		}
		_session.getSessionSheet().getObjectTreePanel().addToObjectTreePopup(dboType, action);
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
		_session.getSessionSheet().getObjectTreePanel().addToObjectTreePopup(
			action);
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
		_session.getSessionSheet().getObjectTreePanel().addToObjectTreePopup(dboType, menu);
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
		_session.getSessionSheet().getObjectTreePanel().addToObjectTreePopup(menu);
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
		if (_firstRefresh)
		{
			_firstRefresh = false;

			addKnownDatabaseObjectType(IObjectTreeAPI.TABLE_TYPE_DBO);
			addKnownDatabaseObjectType(IObjectTreeAPI.PROC_TYPE_DBO);
			addKnownDatabaseObjectType(IObjectTreeAPI.UDT_TYPE_DBO);
			addKnownDatabaseObjectType(DatabaseObjectType.TABLE);
			addKnownDatabaseObjectType(DatabaseObjectType.UDT);

			final SQLDatabaseMetaData md = _session.getSQLConnection().getSQLMetaData();
			try
			{
				if (md.supportsCatalogs())
				{
					addKnownDatabaseObjectType(DatabaseObjectType.CATALOG);
				}
			}
			catch (SQLException ex)
			{
				s_log.debug("Error in SQLDatabaseMetaData.supportsCatalogs() call", ex);
			}
			try
			{
				if (md.supportsSchemas())
				{
					addKnownDatabaseObjectType(DatabaseObjectType.SCHEMA);
				}
			}
			catch (SQLException ex)
			{
				s_log.debug("Error in SQLDatabaseMetaData.supportsSchemas() call", ex);
			}
			try
			{
				if (md.supportsStoredProcedures())
				{
					addKnownDatabaseObjectType(DatabaseObjectType.PROCEDURE);
				}
			}
			catch (SQLException ex)
			{
				s_log.debug("Error in SQLDatabaseMetaData.supportsStoredProcedures() call", ex);
			}
		}
		_session.getSessionSheet().getObjectTreePanel().refreshTree();
	}

	/**
	 * Refresh the nodes currently selected in the object tree.
	 */
	public void refreshSelectedNodes()
	{
		_session.getSessionSheet().getObjectTreePanel().refreshSelectedNodes();
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

	/**
	 * Create a new <TT>DatabaseObjectType</TT>
	 *
	 * @return	a new <TT>DatabaseObjectType</TT>
	 */
	public DatabaseObjectType createNewDatabaseObjectType(String name)
	{
		return DatabaseObjectType.createNewDatabaseObjectType(name);
	}

	/**
	 * Retrieve details about all object types that can be in this
	 * tree.
	 *
	 * @return	DatabaseObjectType[]	Array of object type info objects.
	 */
	public DatabaseObjectType[] getDatabaseObjectTypes()
	{
		return _session.getSessionSheet().getObjectTreePanel().getDatabaseObjectTypes();
	}

	/**
	 * Add a known database object type to the object tree.
	 *
	 * @param	dboType		The new database object type.
	 */
	public void addKnownDatabaseObjectType(DatabaseObjectType dboType)
	{
		_session.getSessionSheet().getObjectTreePanel().addKnownDatabaseObjectType(dboType);
	}
}
