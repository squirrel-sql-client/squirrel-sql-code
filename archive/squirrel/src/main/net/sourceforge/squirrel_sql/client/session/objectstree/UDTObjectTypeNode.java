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
import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.sql.IUDTInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;

import net.sourceforge.squirrel_sql.client.session.ISession;

public final class UDTObjectTypeNode extends ObjectTypeNode {
    /**
     * This interface defines locale specific strings. This should be
     * replaced with a property file.
     */
    private interface i18n {
        String UDT = "UDT";
    }

    public UDTObjectTypeNode(ISession session, ObjectsTreeModel treeModel,
                                TableTypesGroupNode parentNode) {
        super(session, treeModel, parentNode, i18n.UDT);
    }

    public void expand() {
        if (getChildCount() == 0) {
            final ObjectsTreeModel model = getTreeModel();
            final ISession session = getSession();
            final SQLConnection conn = session.getSQLConnection();
            final String catalogId = getParentNode().getCatalogIdentifier();
            final String schemaId = getParentNode().getSchemaIdentifier();
            IUDTInfo[] udts = null;
            try {
                udts = conn.getUDTs(catalogId, schemaId, "%", null);
            } catch (BaseSQLException ignore) {
                // Assume DBMS doesn't support UDTs.
            }
            if (udts != null) {
                for (int i = 0; i < udts.length; ++i) {
                    UDTNode node = new UDTNode(session, model, udts[i]);
                    model.insertNodeInto(node, this, getChildCount());
                }
            }
        }
    }
}
