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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.IUDTInfo;
import net.sourceforge.squirrel_sql.fw.sql.NoConnectionException;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.Logger;

import net.sourceforge.squirrel_sql.client.session.ISession;
//import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.plugin.IPluginDatabaseObject;
import net.sourceforge.squirrel_sql.client.plugin.IPluginDatabaseObjectType;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;

public class ObjectsTreeModel extends DefaultTreeModel {
    private ISession _session;

    /**
     * This interface defines locale specific strings. This should be
     * replaced with a property file.
     */
    private interface i18n {
        String DATABASE = "Database";
        String NO_CATALOG = "No Catalog";   // i18n or Replace with md.getCatalogueTerm.
        String PROCEDURE = "PROCEDURE";
        String UDT = "UDT";
    }

    public ObjectsTreeModel(ISession session) {
        super(new DefaultMutableTreeNode());
        _session = session;
        setRoot(new DatabaseNode(session, this));
        try {
            SQLConnection conn = session.getSQLConnection();
            loadTree();
        } catch (BaseSQLException ex) {
            Logger logger = _session.getApplication().getLogger();
            logger.showMessage(Logger.ILogTypes.ERROR, "Error occured building the objects tree");
            logger.showMessage(Logger.ILogTypes.ERROR, ex);
            _session.getMessageHandler().showMessage(ex.toString());
        }
    }

    private SQLConnection getConnection() {
        return _session.getSQLConnection();
    }

    public void refresh() throws BaseSQLException {
        final DefaultMutableTreeNode root = (DefaultMutableTreeNode)getRoot();
        root.removeAllChildren();
        loadTree();
    }

    String[] getTableTypes() {
        try {
            return getConnection().getTableTypes();
        } catch (BaseSQLException ignore) {
            return new String[] {};
            // Assume driver doesn't handle getTableTypes().
        }
    }

    private void loadTree() throws BaseSQLException {
        final DefaultMutableTreeNode root = (DefaultMutableTreeNode)getRoot();
        try {
            SQLConnection conn = getConnection();
            if (conn != null) {
                // Load object types from plugins.
//              PluginManager mgr = _session.getApplication().getPluginManager();
//              IPluginDatabaseObjectType[] types = mgr.getDatabaseObjectTypes(_session);
//              for (int i = 0; i < types.length; ++i) {
//                  root.add(new BaseNode(_session, this, types[i]));
//              }

                boolean supportsCatalogs = false;
                try {
                    supportsCatalogs = conn.supportsCatalogsInTableDefinitions();
                } catch (BaseSQLException ex) {
                }

                boolean supportsSchemas = false;
                try {
                    supportsSchemas = conn.supportsSchemasInTableDefinitions();
                } catch (BaseSQLException ex) {
                }
                if (supportsCatalogs) {
                    final String[] catalogs = conn.getCatalogs();
                    for (int i = 0; i < catalogs.length; ++i) {
                        final String catalogName =  catalogs[i];
                        root.add(new TableTypesGroupNode(_session, this, catalogName,
                                                            catalogName, null, null));
                    }
                } else if (supportsSchemas) {
                    final String[] schemas = conn.getSchemas();
                    for (int i = 0; i < schemas.length; ++i) {
                        final String schemaName = schemas[i];
                        root.add(new TableTypesGroupNode(_session, this, null,
                                                null, schemaName, schemaName));
                    }
                } else {
                    root.add(new TableTypesGroupNode(_session, this, null, null, null, null));
                }
            }
        } finally {
            reload();
        }
    }


}
