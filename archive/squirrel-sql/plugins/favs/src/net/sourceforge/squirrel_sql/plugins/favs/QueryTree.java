package net.sourceforge.squirrel_sql.plugins.favs;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;

import net.sourceforge.squirrel_sql.fw.gui.BasePopupMenu;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

final class QueryTree extends JTree {
	private IApplication _app;

	private QueryTreeModel _model;

	/** Popup menu for this component. */
	private MyPopupMenu _popupMenu = new MyPopupMenu();

	private List _actions = new ArrayList();

	public QueryTree(IApplication app, FoldersCache cache) throws IllegalArgumentException {
		super(new QueryTreeModel(app, cache));
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (cache == null) {
			throw new IllegalArgumentException("Null FolderCache passed");
		}
		_app = app;
		_model = (QueryTreeModel)getModel();
		setRootVisible(false);
		//setModel(_model);
		setLayout(new BorderLayout());
		setShowsRootHandles(true);
		setEditable(true);

		// Add mouse listener for displaying popup menu.
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				if (evt.isPopupTrigger()) {
					displayPopupMenu(evt);
				}
			}
			public void mouseReleased(MouseEvent evt) {
				if (evt.isPopupTrigger()) {
					displayPopupMenu(evt);
				}
			}
		});

//	  addTreeExpansionListener(new MyExpansionListener());

		// Register so that we can display different tooltips depending
		// which entry in tree mouse is over.
		ToolTipManager.sharedInstance().registerComponent(this);

/*
		final TreeSelectionModel selModel = getSelectionModel();
		if (selModel != null) {
			selModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		}
*/
	}

	/**
	 * Return the name of the object that the mouse is currently
	 * over as the tooltip text.
	 *
	 * @param   event   Used to determine the current mouse position.
	 */
	public String getToolTipText(MouseEvent evt) {
		String tip = null;
		final TreePath path = getPathForLocation(evt.getX(), evt.getY());
		if (path != null) {
			tip = path.getLastPathComponent().toString();
		} else {
			tip = super.getToolTipText();
		}
		return tip;
	}
	QueryTreeModel getTypedModel() {
		return (QueryTreeModel)getModel();
	}

	/**
	 * Display the popup menu for the drivers list.
	 */
	private void displayPopupMenu(MouseEvent evt) {
		int x = evt.getX();
		int y = evt.getY();
		TreePath path = getPathForLocation(x, y);
		_popupMenu.show(evt, path);
	}
	/**
	 * Popup menu for this tree.
	 */
	private class MyPopupMenu extends BasePopupMenu {
		/** Set to <CODE>true</CODE> once list is built. */
		private boolean _built = false;

		/**
		 * Show the menu. Build it if it hasn't already been built.
		 */
		public void show(MouseEvent evt, TreePath path) {
			if (!_built) {
				ActionCollection actColl = QueryTree.this._app.getActionCollection();
				add(actColl.get(NewSavedQueriesFolderAction.class));
				addSeparator();
				add(actColl.get(RenameSavedQueriesFolderAction.class));
				addSeparator();
				add(actColl.get(DeleteSavedQueriesFolderAction.class));
				_built = true;
			}
			BaseNode node = null;
			for (Iterator it = QueryTree.this._actions.iterator(); it.hasNext();) {
				((BaseFavouriteAction)it.next()).setTreePath(path);
			}
			super.show(evt);
		}
		public JMenuItem add(Action action) {
			if (action instanceof BaseFavouriteAction) {
				((BaseFavouriteAction)action).setQueryTree(QueryTree.this);
				_actions.add(action);
			}
			return super.add(action);
		}
	}
}
