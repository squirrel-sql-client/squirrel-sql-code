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
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectTypes;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
/**
 * This is the tree showing the structure of objects in the database.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ObjectTree extends JTree {
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(ObjectTree.class);

	/** Model for this tree. */
	private ObjectTreeModel _model;

	/** Current session. */
	private ISession _session;

	/**
	 * ctor specifying session.
	 * 
	 * @param	session	Current session.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ISession</TT> passed.
	 */
	ObjectTree(ISession session) {
		super();
		if (session == null) {
			throw new IllegalArgumentException("ISession == null");
		}
		_session = session;
		_model = new ObjectTreeModel(session);
//		((BaseNode)_model.getRoot()).addBaseNodeExpandListener(this);
//		_model.addTreeLoadedListener(this);
//		_model.fillTree();

		addTreeExpansionListener(new NodeExpansionListener());

		setShowsRootHandles(true);
		setModel(_model);
		expandNode((ObjectTreeNode)_model.getRoot());
		setSelectionRow(0);
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
		// Don't need tooltips any more.
		ToolTipManager.sharedInstance().unregisterComponent(this);
		super.removeNotify();
	}

	/**
	 * Return the name of the object that the mouse is currently
	 * over as the tooltip text.
	 *
	 * @param   event   Used to determine the current mouse position.
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

	public void expandNode(ObjectTreeNode parentNode)
	{
		if (parentNode == null)
		{
			throw new IllegalArgumentException("ObjectTreeNode == null");
		}

		//parentNode.addBaseNodeExpandListener(ObjectsTree.this);
		//INodeExpander[] expanders = parentNode.getExpanders();

		// If node hasn't already neen expanded.
		if (parentNode.getChildCount() == 0)
		{
			// Add together the standard expanders for this node type and any
			// individual expanders that there are for the node and process them.
			int objType = parentNode.getDatabaseObjectInfo().getDatabaseObjectType();
			INodeExpander[] stdExpanders = _model.getExpanders(objType);
			INodeExpander[] extraExpanders = parentNode.getExpanders();
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
				TreeLoader loader = new TreeLoader(parentNode, expanders);
				_session.getApplication().getThreadPool().addTask(loader);
			}
		}
	}
	
	private final class NodeExpansionListener implements TreeExpansionListener
	{
		public void treeExpanded(TreeExpansionEvent evt)
		{
			// Get the node to be expanded.
			Object parentObj = evt.getPath().getLastPathComponent();
			if (parentObj instanceof ObjectTreeNode)
			{
				ObjectTree.this.expandNode((ObjectTreeNode)parentObj);
			}
		}


		public void treeCollapsed(TreeExpansionEvent evt)
		{
		}
	}

	private final class TreeLoader implements Runnable
	{
		private ObjectTreeNode _parentNode;
		private INodeExpander[] _expanders;

		TreeLoader(ObjectTreeNode parentNode, INodeExpander[] expanders)
		{
			super();
			_parentNode = parentNode;
			_expanders = expanders;
		}

		public void run()
		{
			try
			{
				synchronized (ObjectTree.this)
				{
					CursorChanger cursorChg = new CursorChanger(ObjectTree.this);
					cursorChg.show();
					try
					{
//ObjectsTreeModel model = getTreeModel();
//DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode("Loading...");
//model.insertNodeInto(dmtn, this, this.getChildCount());
						IDatabaseObjectInfo doi = new DatabaseObjectInfo(null,null,
								"Loading...", IDatabaseObjectTypes.GENERIC_LEAF,
								_session.getSQLConnection());
						ObjectTreeNode loadingNode = new ObjectTreeNode(_session, doi);
						_parentNode.add(loadingNode);
						try
						{
							// Show the loading node.
							ObjectTree.this._model.nodeStructureChanged(_parentNode);
							for (int i = 0; i < _expanders.length; ++i)
							{
								List list = _expanders[i].createChildren(_session, _parentNode);
								Iterator it = list.iterator();
								while (it.hasNext())
								{
									Object nextObj = it.next();
									if (nextObj instanceof ObjectTreeNode)
									{
										ObjectTreeNode nextNode = (ObjectTreeNode)nextObj;
										_parentNode.add(nextNode);
									}
								}
							}
						}
						finally
						{
							_parentNode.remove(loadingNode);
						}
					}
					finally
					{
						ObjectTree.this._model.nodeStructureChanged(_parentNode);
						cursorChg.restore();
					}
				}
			}
			catch (SQLException ex)
			{
				final String msg = "Error expanding: " + _parentNode.toString();
				s_log.error(msg, ex);
				_session.getMessageHandler().showMessage(msg + ": " + ex.toString());
				
			}
			catch (BaseSQLException ex)
			{
				final String msg = "Error expanding: " + _parentNode.toString();
				s_log.error(msg, ex);
				_session.getMessageHandler().showMessage(msg + ": " + ex.toString());
			}
		}
	}
}

