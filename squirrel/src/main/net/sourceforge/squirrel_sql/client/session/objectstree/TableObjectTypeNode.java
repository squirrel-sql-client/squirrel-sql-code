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
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;

import net.sourceforge.squirrel_sql.fw.util.Logger;

import net.sourceforge.squirrel_sql.client.session.ISession;

public final class TableObjectTypeNode extends ObjectTypeNode {
    private final String _text;
    private final String _tableTypePattern;

    public TableObjectTypeNode(ISession session, ObjectsTreeModel treeModel,
                                TableTypesGroupNode parent, String text,
                                String tableTypePattern) {
        super(session, treeModel, parent, text);
        _text = text;
        _tableTypePattern = tableTypePattern;
    }

    public void expand() throws BaseSQLException {
        if (getChildCount() == 0) {
            final ISession session = getSession();
            final SQLConnection conn = session.getSQLConnection();
            final String catalogId = getParentNode().getCatalogIdentifier();
            final String schemaId = getParentNode().getSchemaIdentifier();
            final ITableInfo[] tables = conn.getTables(catalogId, schemaId, "%", new String[]{_tableTypePattern});
            Statement stmt = null;
            if (session.getProperties().getShowRowCount()) {
                stmt = conn.createStatement();
            }
            try {
                ObjectsTreeModel model = getTreeModel();
                for (int i = 0; i < tables.length; ++i) {
                    TableNode node = new TableNode(session, model, tables[i], stmt);
                    model.insertNodeInto(node, this, this.getChildCount());
                }
            } finally {
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                } catch (SQLException ex) {
                    session.getMessageHandler().showMessage(ex);
                    Logger logger = session.getApplication().getLogger();
                    logger.showMessage(Logger.ILogTypes.ERROR, "Error occured expanding TableObjectTypeNode");
                    logger.showMessage(Logger.ILogTypes.ERROR, ex);
                }
            }
        }
    }

}
