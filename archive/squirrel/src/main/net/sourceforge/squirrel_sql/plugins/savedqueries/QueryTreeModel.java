package net.sourceforge.squirrel_sql.plugins.savedqueries;
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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.squirrel_sql.client.IApplication;

final class QueryTreeModel extends DefaultTreeModel {
    private IApplication _app;
    private FoldersCache _cache;

    private JPanel _emptyPnl = new JPanel();

    private MyModelListener _modelListener = new MyModelListener();

    /**
     * This interface defines locale specific strings. This should be
     * replaced with a property file.
     */
    private interface i18n {
        String DATABASE = "Database";
//      String TITLE = "Catalogs";  // ?? Replace with md.getCatalogueTerm.
        String NO_CATALOG = "No Catalog";   // ?? Replace with md.getCatalogueTerm.
        String MAY_RETURN = "May return a result";
        String DOESNT_RETURN = "Does not return a result";
        String DOES_RETURN = "Returns a result";
        String UNKNOWN = "Unknown";
    }

    QueryTreeModel(IApplication app, FoldersCache cache) throws IllegalArgumentException {
        super(new FolderNode(new Folder(null, "Root")));
        if (app == null) {
            throw new IllegalArgumentException("Null IApplication passed");
        }
        if (cache == null) {
            throw new IllegalArgumentException("Null FoldersCache passed");
        }
        addTreeModelListener(_modelListener);
        _app = app;
        _cache = cache;
        loadTree();
    }

    private void loadTree() {
        _modelListener.stopListening();
        try {
            Folder rootFolder = _cache.getRootFolder();
            if (rootFolder != null) {
                FolderNode rootNode = new FolderNode(rootFolder);
                setRoot(rootNode);
                loadSubFolders(rootNode);
            } else {
                rootFolder = new Folder(null, "Root");
                setRoot(new FolderNode(rootFolder));
                _cache.setRootFolder(rootFolder);
            }
            reload();
        } finally {
            _modelListener.startListening();
        }
    }

    private void loadSubFolders(FolderNode node) {
        Folder[] subFolders = node.getFolder().getSubFolders();
        for (int i = 0; i < subFolders.length; ++i) {
            FolderNode childNode = new FolderNode(subFolders[i]);
            node.add(childNode);
            loadSubFolders(childNode);
        }
    }

    private static class MyModelListener implements TreeModelListener {
        private boolean _listening = true;

        public void treeStructureChanged(TreeModelEvent evt) {
            System.out.println("treeStructureChanged");
        }

        public void treeNodesInserted(TreeModelEvent evt) {
            System.out.println("treeNodesInserted");
        }

        public void treeNodesChanged(TreeModelEvent evt) {
            System.out.println("treeNodesChanged");
            Object objs[] = evt.getChildren();
            if (objs != null) {
                for (int i = 0; i < objs.length; ++i) {
                    FolderNode node = (FolderNode)objs[i];
                    Folder folder = node.getFolder();
                    String newName = node.toString();
                    String oldName = folder.getName();
                    System.out.println(oldName);
                    if (!newName.equals(oldName)) {
                        try {
                            folder.setName(newName);
                        } catch (Exception ex) {
                            System.out.println(ex.toString()); // ??
                        }
                    }
                }
            }
        }

        public void treeNodesRemoved(TreeModelEvent evt) {
        }

        void startListening() {
            _listening = true;
        }

        void stopListening() {
            _listening = false;
        }
    }
}