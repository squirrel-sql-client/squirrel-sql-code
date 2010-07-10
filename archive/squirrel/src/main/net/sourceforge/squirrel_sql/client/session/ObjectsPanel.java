package net.sourceforge.squirrel_sql.client.session;
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
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.objectstree.BaseNode;
//import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

class ObjectsPanel extends JPanel {

    private ObjectsTree _tree;
    private JSplitPane _splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    private JComponent _emptyPnl = new JPanel();

    ObjectsPanel(ISession session) {
        super();
        createUserInterface(session);
    }

    // Required under JDK1.2. Without it the divider location is initialised to
    // zero.
    void fixDividerLocation() {
        _splitPane.setDividerLocation(200);
    }

    void refreshTree() throws BaseSQLException {
        _tree.refresh();
    }

	/**
	 * Return an array of <TT>IDatabaseObjectInfo</TT> objects representing all
	 * the objects selected in the objects tree.
	 * 
	 * @return	array of <TT>IDatabaseObjectInfo</TT> objects.
	 */
	public IDatabaseObjectInfo[] getSelectedDatabaseObjects() {
		return _tree.getSelectedDatabaseObjects();
	}

    private void createUserInterface(ISession session) {
        setLayout(new BorderLayout());

        _tree = new ObjectsTree(session);

        _splitPane.setOneTouchExpandable(true);
        _splitPane.setContinuousLayout(true);
        JScrollPane sp = new JScrollPane();
        sp.setViewportView(_tree);
        sp.setPreferredSize(new Dimension(200, 200));
        _splitPane.add(sp, JSplitPane.LEFT);
        add(_splitPane, BorderLayout.CENTER);

        setSelectedObjectPanel(_emptyPnl);

        _tree.addTreeSelectionListener(new MySelectionListener());

        _splitPane.setDividerLocation(200);

        final TreePath[] path = _tree.getSelectionPaths();
        if (path != null && path.length > 0) {
            setSelectedObjectPanel(path[0]);
        } else {
            setSelectedObjectPanel(_emptyPnl);
        }
    }

    private void setSelectedObjectPanel(TreePath path) {
        JComponent comp = _emptyPnl;
        if (path != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
            if (node instanceof BaseNode) {
                comp = ((BaseNode)node).getDetailsPanel();
            }
        }
        setSelectedObjectPanel(comp);
    }

    private void setSelectedObjectPanel(Component comp) {
        int divLoc = _splitPane.getDividerLocation();
        Component existing = _splitPane.getRightComponent();
        if (existing != null) {
            _splitPane.remove(existing);
        }
        _splitPane.add(comp, JSplitPane.RIGHT);
        _splitPane.setDividerLocation(divLoc);
    }

    private final class MySelectionListener implements TreeSelectionListener {
        public void valueChanged(TreeSelectionEvent evt) {
            setSelectedObjectPanel(evt.getNewLeadSelectionPath());
        }
    }
}
