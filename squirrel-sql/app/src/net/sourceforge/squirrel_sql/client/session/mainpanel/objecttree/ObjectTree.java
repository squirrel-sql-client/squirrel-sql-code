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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import java.awt.event.MouseEvent;

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

		_model = new ObjectTreeModel(session);
//		((BaseNode)_model.getRoot()).addBaseNodeExpandListener(this);
//		_model.addTreeLoadedListener(this);
//		_model.fillTree();
		setShowsRootHandles(true);
		setModel(_model);
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
}

