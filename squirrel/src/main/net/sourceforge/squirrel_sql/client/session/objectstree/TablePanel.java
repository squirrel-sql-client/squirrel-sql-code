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
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTextPanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewerDestination;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.TableInfoDataSet;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.util.Logger;

public class TablePanel extends JTabbedPane {
    /**
     * This interface defines locale specific strings. This should be
     * replaced with a property file.
     */
    private interface i18n {
        String TABLE_TAB_TITLE = "Info";
        String TABLE_TAB_DESC = "Basic information";
        String COLPRIV_TAB_TITLE = "Column Priviliges";
        String COLPRIV_TAB_DESC = "Show access rights for columns";
        String COL_TAB_TITLE = "Columns";
        String COL_TAB_DESC = "Show columns for table";
        String CONTENTS_TAB_TITLE = "Content";
        String CONTENTS_TAB_DESC = "Sample contents";
        String EXP_KEY_TAB_TITLE = "Exported Keys";
        String EXP_KEY_TAB_DESC = "Show tables that reference this table";
        String IDX_TAB_TITLE = "Indexes";
        String IDX_TAB_DESC = "Show indexes";
        String IMP_KEY_TAB_TITLE = "Imported Keys";
        String IMP_KEY_TAB_DESC = "Show tables that this table references";
        String PRIMARY_KEY_TITLE = "Primary Key";
        String PRIMARY_KEY_DESC = "Show primary key for table";
        String PRIV_TAB_TITLE = "Priviliges";
        String PRIV_TAB_DESC = "Show access rights for table";
        String ROWID_TAB_TITLE = "Row IDs";
        String ROWID_TAB_DESC = "Show columns that uniquely identify a row";
        String VERS_TAB_TITLE = "Versions";
        String VERS_TAB_DESC = "Show columns that are automatically updated when row updated";
    }

    private HashMap _viewersMap = new HashMap();
    private ArrayList _viewersArray = new ArrayList();
    private ISession _session;

    /** Listens to changes in <CODE>_props</CODE>. */
    private MyPropertiesListener _propsListener;

    public TablePanel(ISession session) {
        super();
        _session = session;
        createUserInterface();
        setSession(session);
    }

    private void setSession(ISession session) {
        for (Iterator it = _viewersMap.values().iterator(); it.hasNext();) {
            ((MyBaseViewer)it.next()).setSession(session);
        }
    }

    public void setTableInfo(ITableInfo value) {
        for (Iterator it = _viewersMap.values().iterator(); it.hasNext();) {
            ((MyBaseViewer)it.next()).setTableInfo(value);
        }

        // Refresh the currentyl selected tab.
        ((MyBaseViewer)_viewersArray.get(getSelectedIndex())).load();
    }

    private void propertiesHaveChanged(PropertyChangeEvent evt) {
        String propName = evt.getPropertyName();
        if (propName.equals(SessionProperties.IPropertyNames.CONTENTS_OUTPUT_CLASS_NAME)) {
            MyBaseViewer viewer = (MyBaseViewer)_viewersMap.get(i18n.CONTENTS_TAB_TITLE);
            addContentsViewerTab(viewer);
            viewer.load();
        } else if (propName.equals(SessionProperties.IPropertyNames.TABLE_OUTPUT_CLASS_NAME)) {
            MyBaseViewer viewer = (MyBaseViewer)_viewersMap.get(i18n.TABLE_TAB_TITLE);
            addTableInfoViewerTab(viewer);
            viewer.load();
        } else if (propName.equals(SessionProperties.IPropertyNames.COLUMNS_OUTPUT_CLASS_NAME)) {
            MyBaseViewer viewer = (MyBaseViewer)_viewersMap.get(i18n.COL_TAB_TITLE);
            addColumnsViewerTab(viewer);
            viewer.load();
        } else if (propName.equals(SessionProperties.IPropertyNames.PRIM_KEY_OUTPUT_CLASS_NAME)) {
            MyBaseViewer viewer = (MyBaseViewer)_viewersMap.get(i18n.PRIMARY_KEY_TITLE);
            addPrimaryKeyViewerTab(viewer);
            viewer.load();
        } else if (propName.equals(SessionProperties.IPropertyNames.EXP_KEYS_OUTPUT_CLASS_NAME)) {
            MyBaseViewer viewer = (MyBaseViewer)_viewersMap.get(i18n.EXP_KEY_TAB_TITLE);
            addExportedKeysViewerTab(viewer);
            viewer.load();
        } else if (propName.equals(SessionProperties.IPropertyNames.IMP_KEYS_OUTPUT_CLASS_NAME)) {
            MyBaseViewer viewer = (MyBaseViewer)_viewersMap.get(i18n.IMP_KEY_TAB_TITLE);
            addImportedKeysViewerTab(viewer);
            viewer.load();
        } else if (propName.equals(SessionProperties.IPropertyNames.INDEXES_OUTPUT_CLASS_NAME)) {
            MyBaseViewer viewer = (MyBaseViewer)_viewersMap.get(i18n.IDX_TAB_TITLE);
            addIndexesViewerTab(viewer);
            viewer.load();
        } else if (propName.equals(SessionProperties.IPropertyNames.PRIVILIGES_OUTPUT_CLASS_NAME)) {
            MyBaseViewer viewer = (MyBaseViewer)_viewersMap.get(i18n.PRIV_TAB_TITLE);
            addTablePriviligesViewerTab(viewer);
            viewer.load();
        } else if (propName.equals(SessionProperties.IPropertyNames.COLUMN_PRIVILIGES_OUTPUT_CLASS_NAME)) {
            MyBaseViewer viewer = (MyBaseViewer)_viewersMap.get(i18n.COLPRIV_TAB_TITLE);
            addColumnPriviligesViewerTab(viewer);
            viewer.load();
        } else if (propName.equals(SessionProperties.IPropertyNames.ROWID_OUTPUT_CLASS_NAME)) {
            MyBaseViewer viewer = (MyBaseViewer)_viewersMap.get(i18n.ROWID_TAB_TITLE);
            addRowIdViewerTab(viewer);
            viewer.load();
        } else if (propName.equals(SessionProperties.IPropertyNames.VERSIONS_OUTPUT_CLASS_NAME)) {
            MyBaseViewer viewer = (MyBaseViewer)_viewersMap.get(i18n.VERS_TAB_TITLE);
            addVersionColumnsViewerTab(viewer);
            viewer.load();
        }
    }

    private void createUserInterface() {
        addContentsViewerTab(new ContentsViewer());
        addTableInfoViewerTab(new TableInfoViewer());
        addColumnsViewerTab(new ColumnsViewer());
        addPrimaryKeyViewerTab(new PrimaryKeyViewer());
        addExportedKeysViewerTab(new ExportedKeysViewer());
        addImportedKeysViewerTab(new ImportedKeysViewer());
        addIndexesViewerTab(new IndexViewer());
        addTablePriviligesViewerTab(new TablePriviligesViewer());
        addColumnPriviligesViewerTab(new ColumnPriviligesViewer());
        addRowIdViewerTab(new RowIdViewer());
        addVersionColumnsViewerTab(new VersionColumnsViewer());
        _propsListener = new MyPropertiesListener(this);
        _session.getProperties().addPropertyChangeListener(_propsListener);
        addChangeListener(new TabbedPaneListener());
    }

    private void addTableInfoViewerTab(MyBaseViewer viewer) {
        addViewer(viewer, i18n.TABLE_TAB_TITLE, i18n.TABLE_TAB_DESC,
                                    _session.getProperties().getTableOutputClassName());
    }

    private void addContentsViewerTab(MyBaseViewer viewer) {
        addViewer(viewer, i18n.CONTENTS_TAB_TITLE, i18n.CONTENTS_TAB_DESC,
                                    _session.getProperties().getContentsOutputClassName());
    }

    private void addColumnsViewerTab(MyBaseViewer viewer) {
        addViewer(viewer, i18n.COL_TAB_TITLE, i18n.COL_TAB_DESC,
                                    _session.getProperties().getColumnsOutputClassName());
    }

    private void addPrimaryKeyViewerTab(MyBaseViewer viewer) {
        addViewer(viewer, i18n.PRIMARY_KEY_TITLE, i18n.PRIMARY_KEY_DESC,
                                    _session.getProperties().getPrimaryKeyOutputClassName());
    }

    private void addExportedKeysViewerTab(MyBaseViewer viewer) {
        addViewer(viewer, i18n.EXP_KEY_TAB_TITLE, i18n.EXP_KEY_TAB_DESC,
                                    _session.getProperties().getExportedKeysOutputClassName());
    }

    private void addImportedKeysViewerTab(MyBaseViewer viewer) {
        addViewer(viewer, i18n.IMP_KEY_TAB_TITLE, i18n.IMP_KEY_TAB_DESC,
                                    _session.getProperties().getImportedKeysOutputClassName());
    }

    private void addIndexesViewerTab(MyBaseViewer viewer) {
        addViewer(viewer, i18n.IDX_TAB_TITLE, i18n.IDX_TAB_DESC,
                                    _session.getProperties().getIndexesOutputClassName());
    }

    private void addTablePriviligesViewerTab(MyBaseViewer viewer) {
        addViewer(viewer, i18n.PRIV_TAB_TITLE, i18n.PRIV_TAB_DESC,
                                    _session.getProperties().getPriviligesOutputClassName());
    }

    private void addColumnPriviligesViewerTab(MyBaseViewer viewer) {
        addViewer(viewer, i18n.COLPRIV_TAB_TITLE, i18n.COLPRIV_TAB_DESC,
                                    _session.getProperties().getColumnPriviligesOutputClassName());
    }

    private void addRowIdViewerTab(MyBaseViewer viewer) {
        addViewer(viewer, i18n.ROWID_TAB_TITLE, i18n.ROWID_TAB_DESC,
                                    _session.getProperties().getRowIdOutputClassName());
    }

    private void addVersionColumnsViewerTab(MyBaseViewer viewer) {
        addViewer(viewer, i18n.VERS_TAB_TITLE, i18n.VERS_TAB_DESC,
                                    _session.getProperties().getVersionsOutputClassName());
    }

    private void addViewer(MyBaseViewer viewer, String title, String desc,
                            String outputClassName) {
        _viewersMap.put(title, viewer);
        _viewersArray.add(viewer);
        addResultSetViewerTab(title, desc, viewer, outputClassName);
        viewer.setHasBeenBuilt(false);
    }

    private void addResultSetViewerTab(String title, String description,
                                                MyBaseViewer viewer,
                                                String destClassName) {
        IDataSetViewerDestination dest = createDestination(destClassName);
        viewer.setDestination(dest);
        int idx = indexOfTab(title);
        if (idx != -1) {
            removeTabAt(idx);
            insertTab(title, null, new JScrollPane((Component)dest), description, idx);
        } else {
            addTab(title, null, new JScrollPane((Component)dest), description);
        }
    }

    private IDataSetViewerDestination createDestination(String destClassName) {
        IDataSetViewerDestination dest = null;
        try {
            Class destClass = Class.forName(destClassName);
            if (IDataSetViewerDestination.class.isAssignableFrom(destClass) &&
                    Component.class.isAssignableFrom(destClass)) {
                dest = (IDataSetViewerDestination)destClass.newInstance();
            }

        } catch (Exception ignore) {
            _session.getApplication().getLogger().showMessage(Logger.ILogTypes.ERROR, ignore.getMessage());
        }
        if (dest == null) {
            dest = new DataSetViewerTextPanel();
        }
        return dest;
    }

    private abstract static class MyBaseViewer extends DataSetViewer {
        private ISession _session;
        private ITableInfo _ti;
        private boolean _hasBeenBuilt = false;

        final void setSession(ISession session) {
            _session = session;
        }

        final ISession getSession() {
            return _session;
        }

        final void setTableInfo(ITableInfo value) {
            _ti = value;
            _hasBeenBuilt = false;
        }

        final void load() {
            if (!_hasBeenBuilt) {
                try {
                    clearDestination();
                    load(_session.getSQLConnection(), _ti);
                } catch (Exception ex) {
                    IMessageHandler msgHandler = _session.getMessageHandler();
                    msgHandler.showMessage("Error in: " + getClass().getName());
                    msgHandler.showMessage(ex);
                }
                _hasBeenBuilt = true;
            }
        }

        public final void setHasBeenBuilt(boolean value) {
            _hasBeenBuilt = value;
        }

        protected abstract void load(SQLConnection conn, ITableInfo ti) throws BaseSQLException, DataSetException, SQLException;
    }

    private static class ContentsViewer extends MyBaseViewer {

        protected void load(SQLConnection conn, ITableInfo ti)
                throws BaseSQLException, DataSetException, SQLException {
            if (conn != null && ti != null) {
                Statement stmt = conn.createStatement();
                try {
                    SessionProperties props = getSession().getProperties();
                    if (props.getContentsLimitRows()) {
                        stmt.setMaxRows(props.getContentsNbrRowsToShow());
                    }
                    ResultSet rs = stmt.executeQuery("select * from " + ti.getQualifiedName());
                    try {
                        show(new ResultSetDataSet(rs), getSession().getMessageHandler());
                    } finally {
                        rs.close();
                    }
                } finally {
                    stmt.close();
                }
            }
        }
    }

    private static class TableInfoViewer extends MyBaseViewer {
        protected void load(SQLConnection conn, ITableInfo ti) throws BaseSQLException, DataSetException, SQLException {
            if (ti != null) {
                show(new TableInfoDataSet(ti), getSession().getMessageHandler());
            }
        }
    }

    private static class ColumnsViewer extends MyBaseViewer {
        protected void load(SQLConnection conn, ITableInfo ti) throws BaseSQLException, DataSetException, SQLException {
            if (conn != null && ti != null) {
                ResultSet rs = conn.getColumns(ti);
                if (rs != null) {
                    show(new ResultSetDataSet(rs, new int[] {4,5,6,7,9,10,11,12,13,14,15,16,17,18}), getSession().getMessageHandler());
                } else {
                    IMessageHandler msgHandler = getSession().getMessageHandler();
                    msgHandler.showMessage("Null ResultSet returned from java.sql.MetaData.getColumns(). This could be an error in the JDBC driver.");
                }
            }
        }
    }

    private static class ExportedKeysViewer extends MyBaseViewer {
        protected void load(SQLConnection conn, ITableInfo ti) throws BaseSQLException, DataSetException, SQLException {
            if (conn != null && ti != null) {
                ResultSet rs = conn.getExportedKeys(ti);
                if (rs != null) {
                    show(new ResultSetDataSet(rs, new int[] {4,7,8,9,10,11,12,13,14}), getSession().getMessageHandler());
                } else {
                    IMessageHandler msgHandler = getSession().getMessageHandler();
                    msgHandler.showMessage("Null ResultSet returned from java.sql.MetaData.getExportedKeys(). This could be an error in the JDBC driver.");

                }
            }
        }
    }

    private static class ImportedKeysViewer extends MyBaseViewer {
        protected void load(SQLConnection conn, ITableInfo ti) throws BaseSQLException, DataSetException, SQLException {
            if (conn != null && ti != null) {
                ResultSet rs = conn.getImportedKeys(ti);
                if (rs != null) {
                    show(new ResultSetDataSet(rs, new int[] {3,4,8,9,10,11,12,13,14}), getSession().getMessageHandler());
                } else {
                    IMessageHandler msgHandler = getSession().getMessageHandler();
                    msgHandler.showMessage("Null ResultSet returned from java.sql.MetaData.getImportedKeys(). This could be an error in the JDBC driver.");
                }
            }
        }
    }

    // Last parm as false causes performance issues.
    private static class IndexViewer extends MyBaseViewer {
        protected void load(SQLConnection conn, ITableInfo ti) throws BaseSQLException, DataSetException, SQLException {
            if (conn != null && ti != null) {
                ResultSet rs = conn.getIndexInfo(ti);
                if (rs != null) {
                    show(new ResultSetDataSet(rs, new int[] {4,5,6,7,8,9,10,11,12,13}), getSession().getMessageHandler());
                } else {
                    IMessageHandler msgHandler = getSession().getMessageHandler();
                    msgHandler.showMessage("Null ResultSet returned from java.sql.MetaData.getIndexInfo(). This could be an error in the JDBC driver.");
                }
            }
        }
    }

    private static class TablePriviligesViewer extends MyBaseViewer {
        protected void load(SQLConnection conn, ITableInfo ti) throws BaseSQLException, DataSetException, SQLException {
            if (conn != null && ti != null) {
                ResultSet rs = conn.getTablePrivileges(ti);
                if (rs != null) {
                    show(new ResultSetDataSet(rs, new int[] {3,4,5,6,7}), getSession().getMessageHandler());
                } else {
                    IMessageHandler msgHandler = getSession().getMessageHandler();
                    msgHandler.showMessage("Null ResultSet returned from java.sql.MetaData.getTablePrivileges(). This could be an error in the JDBC driver.");
                }
            }
        }
    }

    private static class ColumnPriviligesViewer extends MyBaseViewer {
        protected void load(SQLConnection conn, ITableInfo ti) throws BaseSQLException, DataSetException, SQLException {
            if (conn != null && ti != null) {
                ResultSet rs = conn.getColumnPrivileges(ti);
                if (rs != null) {
                    show(new ResultSetDataSet(rs, new int[] {4,5,6,7,8}), getSession().getMessageHandler());
                } else {
                    IMessageHandler msgHandler = getSession().getMessageHandler();
                    msgHandler.showMessage("Null ResultSet returned from java.sql.MetaData.getColumnPrivileges(). This could be an error in the JDBC driver.");
                }
            }
        }
    }

    private static class PrimaryKeyViewer extends MyBaseViewer {
        protected void load(SQLConnection conn, ITableInfo ti) throws BaseSQLException, DataSetException, SQLException {
            if (conn != null && ti != null) {
                ResultSet rs = conn.getPrimaryKeys(ti);
                if (rs != null) {
                    show(new ResultSetDataSet(rs, new int[] {4,5,6}), getSession().getMessageHandler());
                } else {
                    IMessageHandler msgHandler = getSession().getMessageHandler();
                    msgHandler.showMessage("Null ResultSet returned from java.sql.MetaData.getPrimaryKeys(). This could be an error in the JDBC driver.");
                }
            }
        }
    }

    private static class RowIdViewer extends MyBaseViewer {
        protected void load(SQLConnection conn, ITableInfo ti) throws BaseSQLException, DataSetException, SQLException {
            if (conn != null && ti != null) {
                ResultSet rs = conn.getBestRowIdentifier(ti);
                if (rs != null) {
                    show(new ResultSetDataSet(rs), getSession().getMessageHandler());
                } else {
                    IMessageHandler msgHandler = getSession().getMessageHandler();
                    msgHandler.showMessage("Null ResultSet returned from java.sql.MetaData.getBestRowIdentifier(). This could be an error in the JDBC driver.");
                }
            }
        }
    }

    private static class VersionColumnsViewer extends MyBaseViewer {
        protected void load(SQLConnection conn, ITableInfo ti) throws BaseSQLException, DataSetException, SQLException {
            if (conn != null && ti != null) {
                ResultSet rs = conn.getVersionColumns(ti);
                if (rs != null) {
                    show(new ResultSetDataSet(rs), getSession().getMessageHandler());
                } else {
                    IMessageHandler msgHandler = getSession().getMessageHandler();
                    msgHandler.showMessage("Null ResultSet returned from java.sql.MetaData.getVersionColumns(). This could be an error in the JDBC driver.");
                }
            }
        }
    }

    private static class MyPropertiesListener implements PropertyChangeListener {
        private TablePanel _panel;

        MyPropertiesListener(TablePanel panel) {
            super();
            _panel = panel;
        }

        public void propertyChange(PropertyChangeEvent evt) {
            _panel.propertiesHaveChanged(evt);
        }
    }

    private class TabbedPaneListener implements ChangeListener {
        public void stateChanged(ChangeEvent evt) {
            Object src = evt.getSource();
            if (src instanceof JTabbedPane) {
                int idx = ((JTabbedPane)src).getSelectedIndex();
                if (idx != -1) {
                    ((MyBaseViewer)TablePanel.this._viewersArray.get(idx)).load();
                }
            }
        }
    }
}
