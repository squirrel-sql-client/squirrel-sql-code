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
import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;

import net.sourceforge.squirrel_sql.client.plugin.IPluginDatabaseObject;
import net.sourceforge.squirrel_sql.client.plugin.IPluginDatabaseObjectType;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class PluginGroupNode extends ObjectTypeNode {
    private IPluginDatabaseObjectType _dbObjType;

    public PluginGroupNode(ISession session, ObjectsTreeModel treeModel,
                            TableTypesGroupNode parent,
                            IPluginDatabaseObjectType type) {
        super(session, treeModel, parent, type.getName());
        _dbObjType = type;
    }

    public void expand() throws BaseSQLException {
        final ObjectsTreeModel model = getTreeModel();
        ISession session = getSession();
        SQLConnection conn = session.getSQLConnection();
        Statement stmt = conn.createStatement();
        try {
            IPluginDatabaseObject[] objs = null;
            try {
                objs = _dbObjType.getObjects(session, conn, stmt);
            } catch (SQLException ex) {
                throw new BaseSQLException(ex);
            }
            if (objs != null) {
                for (int i = 0; i < objs.length; ++i) {
                    BaseNode node = new BaseNode(session,getTreeModel(), objs[i]);
                    model.insertNodeInto(node, this, this.getChildCount());
                }
            }
        } finally {
            try {
                stmt.close();
            } catch (SQLException ignore) {
            }
        }
    }
}
