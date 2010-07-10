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

import net.sourceforge.squirrel_sql.client.plugin.IPluginDatabaseObjectType;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class TableTypesGroupNode extends BaseNode {
    private interface i18n {
        String NO_CATALOG = "No Catalog";   // i18n or Replace with md.getCatalogueTerm.
    }

    private String _catalogName;
    private String _schemaName;
    private String _catalogIdentifier;
    private String _schemaIdentifier;

    TableTypesGroupNode(ISession session, ObjectsTreeModel treeModel,
                        String catalogName, String catalogIdentifier,
                        String schemaName, String schemaIdentifier) {
        super(session, treeModel, generateName(catalogName, schemaName));
        _catalogIdentifier = catalogIdentifier;
        _schemaIdentifier = schemaIdentifier;
        _catalogName = catalogName;
        _schemaName = schemaName;
        String[] tableTypes = treeModel.getTableTypes();
        for (int i = 0; i < tableTypes.length; ++i) {
            String tableType = tableTypes[i];
            add(new TableObjectTypeNode(session, treeModel, this, tableType, tableType));
        }

        add(new UDTObjectTypeNode(session, treeModel, this));

        try {
            if (session.getSQLConnection().supportsStoredProcedures()) {
                add(new ProcedureObjectTypeNode(getSession(), getTreeModel(), this));
            }
        } catch (BaseSQLException ignore) {
            // Any probs just assume that db doesn't supports procs.
        }

        // Load object types from plugins.
        PluginManager mgr = getSession().getApplication().getPluginManager();
        IPluginDatabaseObjectType[] types = mgr.getDatabaseObjectTypes(session);
        for (int i = 0; i < types.length; ++i) {
            add(new PluginGroupNode(session, treeModel, this, types[i]));
        }
    }

    public void expand() {
    }

    String getCatalogName() {
        return _catalogName;
    }

    String getCatalogIdentifier() {
        return _catalogIdentifier;
    }

    String getSchemaName() {
        return _schemaName;
    }

    String getSchemaIdentifier() {
        return _schemaIdentifier;
    }

    private static String generateName(String catalogName, String schemaName) {
        StringBuffer buf = new StringBuffer();
        if (catalogName != null) {
            buf.append(catalogName);
            if (schemaName != null) {
                buf.append(".");
            }
        }
        if (schemaName != null) {
            buf.append(schemaName);
        }
        if (buf.length() == 0) {
            buf.append(i18n.NO_CATALOG);
        }
        return buf.toString();
    }

    public boolean isLeaf() {
        return false;
    }

}
