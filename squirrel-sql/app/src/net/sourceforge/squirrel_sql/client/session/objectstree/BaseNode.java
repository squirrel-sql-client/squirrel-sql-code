package net.sourceforge.squirrel_sql.client.session.objectstree;
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
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;

/**
 * This is the base class for all nodes in the Objects Tree.
 */
public class BaseNode extends DefaultMutableTreeNode {

    /**
     * Empty panel. Used by those nodes that don't want to display anything
     * in the main display area if they are selected.
     */
    private static final JPanel s_emptyPnl = new JPanel();

    /** Current session. */
    private final ISession _session;

    /** Tree model. */
    private final ObjectsTreeModel _treeModel;

    public BaseNode(ISession session, ObjectsTreeModel treeModel,
                        Object userObject) {
        super(userObject);
        if (session == null) {
            throw new IllegalArgumentException("null ISession passed");
        }
        if (treeModel == null) {
            throw new IllegalArgumentException("null ObjectsTreeModel passed");
        }
        _session = session;
        _treeModel = treeModel;
    }

    public void expand() throws BaseSQLException {
    }

    public JComponent getDetailsPanel() {
        return s_emptyPnl;
    }

    protected ISession getSession() {
        return _session;
    }

    protected ObjectsTreeModel getTreeModel() {
        return _treeModel;
    }

    protected String getSafeString(String str) {
        return str != null ? str : "";
    }
}
