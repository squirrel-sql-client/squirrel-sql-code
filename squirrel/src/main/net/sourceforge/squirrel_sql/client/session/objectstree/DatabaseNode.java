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

import net.sourceforge.squirrel_sql.client.session.ISession;

public class DatabaseNode extends BaseNode {
    private interface ISessionKeys {
        String DETAIL_PANEL_KEY = DatabaseNode.class.getName() + "_DETAIL_PANEL_KEY";
    }

    /**
     * This interface defines locale specific strings. This should be
     * replaced with a property file.
     */
    private interface i18n {
        String DATABASE = "Database";
    }

    private DatabasePanel _dbPnl;

    DatabaseNode(ISession session, ObjectsTreeModel treeModel) {
        super(session, treeModel, i18n.DATABASE);
        _dbPnl = new DatabasePanel(session);
    }

    public void expand() {
    }

    public JComponent getDetailsPanel() {
        return _dbPnl;
    }

    public boolean isLeaf() {
        return false;
    }
}
