package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Modifications copyright (C) 2001 Johan Compagner
 * jcompagner@j-com.nl
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
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectSimpleNameInfoComparator;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.objectstree.BaseNode;
import net.sourceforge.squirrel_sql.client.session.objectstree.ObjectsTreeModel;
//import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

class ObjectsTree extends JTree {
    private ISession _session;
    private ObjectsTreeModel _model;

	private static DatabaseObjectSimpleNameInfoComparator s_comparator = new DatabaseObjectSimpleNameInfoComparator();

    ObjectsTree(ISession session) {
        super();
        _session = session;
        _model = new ObjectsTreeModel(session);
        setModel(_model);
        setLayout(new BorderLayout());
        setShowsRootHandles(true);
        setEditable(false);
        addTreeExpansionListener(new MyExpansionListener());

        // Register so that we can display different tooltips depending
        // which entry in tree mouse is over.
        ToolTipManager.sharedInstance().registerComponent(this);

        clearSelection();
        addSelectionRow(0);
    }

    void refresh() throws BaseSQLException {
        SavedExpansionState es = new SavedExpansionState(this);
        _model.refresh();
        es.restore();
        _model.reload();
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
            tip = getToolTipText();
        }
        return tip;
    }

	/**
	 * Return an array of <TT>IDatabaseObjectInfo</TT> objects representing all
	 * the objects selected in the objects tree. This array is sorted by the
	 * simple name of the database object.
	 * 
	 * @return	array of <TT>IDatabaseObjectInfo</TT> objects.
	 */
	IDatabaseObjectInfo[] getSelectedDatabaseObjects() {
		TreePath[] paths = this.getSelectionPaths();
		List list = new ArrayList();
		for (int i = 0; i < paths.length; i++) {
			Object o = paths[i].getLastPathComponent();
			if (o instanceof IDatabaseObjectInfo) {
				list.add(o);
			}
		}
		IDatabaseObjectInfo[] objInfo = (IDatabaseObjectInfo[])list.toArray(new IDatabaseObjectInfo[list.size()]);
		Arrays.sort(objInfo, s_comparator);
		return objInfo;
	}

    private final class MyExpansionListener implements TreeExpansionListener {
        MyExpansionListener() {
            super();
        }

        public void treeExpanded(TreeExpansionEvent evt) {
            DefaultMutableTreeNode node =
                (DefaultMutableTreeNode)evt.getPath().getLastPathComponent();
            if (node instanceof BaseNode) {
                CursorChanger cursorChg = new CursorChanger(ObjectsTree.this);
                cursorChg.show();
                try {
                    ((BaseNode)node).expand();
                } catch (BaseSQLException ex) {
                    ObjectsTree.this._session.getMessageHandler().showMessage(ex);
                } finally {
                    cursorChg.restore();
                }
            }
        }

        public void treeCollapsed(TreeExpansionEvent evt) {
        }
    }

    /**
     * This object is used to save and restore the state of a JTree.
     *
     * Make this a public class in the fw packages. ??
     */
    private final static class SavedExpansionState {
        /** <TT>JTree</TT> that expansion state is being save for. */
        private JTree _tree;

        /**
         * Contains information about each expanded node.
         */
        private Map _expanded = new HashMap();

        /** Current selection. */
        private TreePath _selectionPath;

        /**
         * Ctor. Save the current expansion state of the tree.
         *
         * @param   tree    <TT>JTree</TT> to save expansion state of.
         *
         * @throws  IllegalArgumentException
         *              Thrown if a <TT>null</TT> </TT>JTree</TT> passed.
         */
        SavedExpansionState(JTree tree) throws IllegalArgumentException {
            super();
            if (tree == null) {
                throw new IllegalArgumentException("Null JTree passed");
            }
            _tree = tree;
            saveState();
        }

        void restore() {
            final TreeModel model = _tree.getModel();
            TreeNode root = (TreeNode)model.getRoot();
            restoreState(model, root, _expanded.entrySet().iterator());
            if (_selectionPath != null) {
                _tree.setSelectionPath(_selectionPath);
            }
        }

        /**
         * Save the state of the tree.
         */
        private void saveState() {
            // Path of the current selection.
            _selectionPath = _tree.getSelectionPath();

            // Loop through all expanded nodes off the root node.
            final TreeModel model = _tree.getModel();
            TreeNode rootNode = (TreeNode)model.getRoot();
            TreePath rootPath = new TreePath(rootNode);
            Enumeration en = _tree.getExpandedDescendants(rootPath);
            if (en != null) {
                while (en.hasMoreElements()) {
                    // Get the list of all the parent nodes that make up
                    // this node and save their names into the _expanded
                    // collection.
                    TreePath tp = (TreePath)en.nextElement();
                    Object[] objs = tp.getPath();
                    Map searchMap = _expanded;

                    // Loop (ignoring the current node idx 0).
                    for (int i = 1; i < objs.length; ++i) {
                        String obj = objs[i].toString();
                        Map children = (Map)searchMap.get(obj);
                        if (children == null) {
                            children = new HashMap();
                            searchMap.put(obj, children);
                        }
                        searchMap = children;
                    }
                }
            }
        }

        private void restoreState(TreeModel model, TreeNode node, Iterator it) {
            _tree.expandPath(new TreePath(node));
            Map nodes = new HashMap();
            for (Enumeration en = node.children(); en.hasMoreElements();) {
                TreeNode childNode = (TreeNode)en.nextElement();
                nodes.put(childNode.toString(), childNode);
            }
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry)it.next();
                String obj = (String)entry.getKey();
                Map children = (Map)entry.getValue();
                restoreState(model, (TreeNode)nodes.get(obj), children.entrySet().iterator());
            }
        }
    }
}
