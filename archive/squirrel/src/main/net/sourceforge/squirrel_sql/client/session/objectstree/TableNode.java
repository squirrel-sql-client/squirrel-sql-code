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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JComponent;

import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.NoConnectionException;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.ISession;

public final class TableNode extends DatabaseObjectNode implements ITableInfo {
    private interface ISessionKeys {
        String DETAIL_PANEL_KEY = TableNode.class.getName() + "_DETAIL_PANEL_KEY";
    }

    private ITableInfo _tableInfo;

    public TableNode(ISession session, ObjectsTreeModel treeModel,
                    ITableInfo tableInfo, Statement rowCountStmt)

            throws IllegalArgumentException, BaseSQLException,
                    NoConnectionException {
        super(session, treeModel, tableInfo);
        if (tableInfo == null) {
            throw new IllegalArgumentException("Null ITableInfo passed");
        }
        _tableInfo = tableInfo;
//      setUserObject(getDisplayText(rowCountStmt));
    }

    public void expand() {
    }

    public String getCatalogName() {
        return _tableInfo.getCatalogName();
    }

    public String getSchemaName() {
        return _tableInfo.getSchemaName();
    }

    public String getSimpleName() {
        return _tableInfo.getSimpleName();
    }

    public String getQualifiedName() {
        return _tableInfo.getQualifiedName();
    }

    public String getType() {
        return _tableInfo.getType();
    }

    public String getRemarks() {
        return _tableInfo.getRemarks();
    }

    public JComponent getDetailsPanel() {
        final ISession session = getSession();
        final IPlugin plugin = session.getApplication().getDummyAppPlugin();
        TablePanel pnl = (TablePanel)session.getPluginObject(plugin, ISessionKeys.DETAIL_PANEL_KEY);
        if (pnl == null) {
            pnl = new TablePanel(session);
            session.putPluginObject(plugin, ISessionKeys.DETAIL_PANEL_KEY, pnl);
        }
        pnl.setTableInfo(this);
        return pnl;
    }

    public boolean isLeaf() {
        return true;
    }

/*
    private String getDisplayText(Statement rowCountStmt) {
        if (rowCountStmt != null) {
            try {
                ResultSet rs = rowCountStmt.executeQuery("select count(*) from " + _tableInfo.getQualifiedName());
                long nbrRows = 0;
                if (rs.next()) {
                    nbrRows = rs.getLong(1);
                }
                return _tableInfo.getSimpleName() + " (" + nbrRows + ")";
            } catch (SQLException ex) {
                return _tableInfo.getSimpleName();
            }
        } else {
            return _tableInfo.getSimpleName();
        }
    }
*/
}
