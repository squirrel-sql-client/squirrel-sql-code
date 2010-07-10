package net.sourceforge.squirrel_sql.fw.sql;
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
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

public class SQLConnection {

    private interface DriverNames {
        String FREE_TDS = "InternetCDS Type 4 JDBC driver for MS SQLServer";
        String JCONNECT = "jConnect (TM) for JDBC (TM)";
    }

    private String _url;
    private Connection _conn;
    private DatabaseMetaData _md;

    private String _dbProductName;
    private String _dbDriverName;

    private boolean _autoCommitOnClose = false;


    public SQLConnection(String url) throws BaseSQLException {
        this(null, url);
    }

    public SQLConnection(String className, String url) throws BaseSQLException {
        super();
        _url = url;
        if (className != null) {
            try {
                Class.forName(className);
            } catch (ClassNotFoundException ex) {
                throw new BaseSQLException(ex);
            }
        }
    }

    public SQLConnection(Connection conn) throws BaseSQLException {
        super();
        _conn = conn;
        try {
            loadMetaData();
            _url = _md.getURL();
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public void close() throws SQLException {
        SQLException savedEx = null;
        if (_conn != null) {
            try {
                if (!_conn.getAutoCommit()) {
                    if (_autoCommitOnClose) {
                        _conn.commit();
                    } else {
                        _conn.rollback();
                    }
                }
            } catch (SQLException ex) {
                savedEx = ex;
            }
            _conn.close();
            _conn = null;
            _md = null;

            if (savedEx != null) {
                throw savedEx;
            }
        }
    }

    public boolean isConnected() {
        return _conn != null;
    }

    public void commit() throws NoConnectionException, BaseSQLException {
        validateConnection();
        try {
            _conn.commit();
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public void rollback() throws NoConnectionException, BaseSQLException {
        validateConnection();
        try {
            _conn.rollback();
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public boolean getAutoCommit()
            throws NoConnectionException, BaseSQLException {
        validateConnection();
        try {
            return _conn.getAutoCommit();
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public void setAutoCommit(boolean value)
            throws NoConnectionException, BaseSQLException {
        validateConnection();
        try {
            _conn.setAutoCommit(value);
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }


    public boolean getCommitOnClose() {
        return _autoCommitOnClose;
    }

    public void setCommitOnClose(boolean value) {
        _autoCommitOnClose = value;
    }

    public Statement createStatement()
            throws NoConnectionException, BaseSQLException {
        validateConnection();
        try {
            return _conn.createStatement();
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public PreparedStatement prepareStatement(String sql)
            throws NoConnectionException, BaseSQLException {
        validateConnection();
        try {
            return _conn.prepareStatement(sql);
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public ResultSet getBestRowIdentifier(ITableInfo ti)
            throws NoConnectionException, BaseSQLException {
        try {
            return getMetaData().getBestRowIdentifier(ti.getCatalogName(),
                                            ti.getSchemaName(),
                                            ti.getSimpleName(),
                                            DatabaseMetaData.bestRowSession, true);
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public String[] getCatalogs()
            throws NoConnectionException, BaseSQLException {
        DatabaseMetaData md = getMetaData();
        ArrayList list = new ArrayList();
        try {
            ResultSet rs = md.getCatalogs();
            while (rs.next()) {
                list.add(rs.getString(1));
            }
            return (String[])list.toArray(new String[list.size()]);
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public String getCatalogSeparator()
            throws NoConnectionException, BaseSQLException {
        try {
            return getMetaData().getCatalogSeparator();
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public ResultSet getColumnPrivileges(ITableInfo ti)
            throws NoConnectionException, BaseSQLException {
        try {
            return getMetaData().getColumnPrivileges(ti.getCatalogName(),
                                                        ti.getSchemaName(),
                                                        ti.getSimpleName(),
                                                        null);
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public ResultSet getColumns(ITableInfo ti)
            throws NoConnectionException, BaseSQLException {
        try {
            return getMetaData().getColumns(ti.getCatalogName(),
                                            ti.getSchemaName(),
                                            ti.getSimpleName(), "%");
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public ResultSet getExportedKeys(ITableInfo ti)
            throws NoConnectionException, BaseSQLException {
        try {
            return getMetaData().getExportedKeys(ti.getCatalogName(),
                                            ti.getSchemaName(),
                                            ti.getSimpleName());
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public String getIdentifierQuoteString()
            throws NoConnectionException, BaseSQLException {
        try {
            if (_dbDriverName.equals(DriverNames.FREE_TDS)
                    || _dbDriverName.equals(DriverNames.JCONNECT)) {
                return "";
            }
            return getMetaData().getIdentifierQuoteString();
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public ResultSet getImportedKeys(ITableInfo ti)
            throws NoConnectionException, BaseSQLException {
        try {
            return getMetaData().getImportedKeys(ti.getCatalogName(),
                                            ti.getSchemaName(),
                                            ti.getSimpleName());
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public ResultSet getIndexInfo(ITableInfo ti)
            throws NoConnectionException, BaseSQLException {
        try {
            return getMetaData().getIndexInfo(ti.getCatalogName(),
                                            ti.getSchemaName(),
                                            ti.getSimpleName(),
                                            false, true);
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public ResultSet getPrimaryKeys(ITableInfo ti)
            throws NoConnectionException, BaseSQLException {
        try {
            return getMetaData().getPrimaryKeys(ti.getCatalogName(),
                                            ti.getSchemaName(),
                                            ti.getSimpleName());
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public IProcedureInfo[] getProcedures(String catalog, String schemaPattern,
                                    String procedureNamePattern)
            throws NoConnectionException, BaseSQLException {
        DatabaseMetaData md = getMetaData();
        ArrayList list = new ArrayList();
        try {
            ResultSet rs = md.getProcedures(catalog, schemaPattern, procedureNamePattern);
            while (rs.next()) {
                list.add(new ProcedureInfo(rs, this));
            }
            return (IProcedureInfo[])list.toArray(new IProcedureInfo[list.size()]);
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public String[] getSchemas()
            throws NoConnectionException, BaseSQLException {
        DatabaseMetaData md = getMetaData();
        ArrayList list = new ArrayList();
        try {
            ResultSet rs = md.getSchemas();
            while (rs.next()) {
                list.add(rs.getString(1));
            }
            return (String[])list.toArray(new String[list.size()]);
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public ResultSet getTablePrivileges(ITableInfo ti)
            throws NoConnectionException, BaseSQLException {
        try {
            return getMetaData().getTablePrivileges(ti.getCatalogName(),
                                            ti.getSchemaName(),
                                            ti.getSimpleName());
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public ITableInfo[] getTables(String catalog, String schemaPattern,
                                    String tableNamePattern, String[] types)
            throws NoConnectionException, BaseSQLException {
        DatabaseMetaData md = getMetaData();
        ArrayList list = new ArrayList();
        try {
            if (_dbDriverName.equals(DriverNames.FREE_TDS) && schemaPattern == null) {
                schemaPattern = "dbo";
            }

            ResultSet rs = md.getTables(catalog, schemaPattern, tableNamePattern,
                                            types);
            while (rs.next()) {
                list.add(new TableInfo(rs, this));
            }
            return (ITableInfo[])list.toArray(new ITableInfo[list.size()]);
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public String[] getTableTypes()
            throws NoConnectionException, BaseSQLException {
        DatabaseMetaData md = getMetaData();
        ArrayList list = new ArrayList();
        try {
            ResultSet rs = md.getTableTypes();
            while (rs.next()) {
                list.add(rs.getString(1).trim());
            }

            final int listSize = list.size();

            // InstantDB (at least version 3.13) only returns "TABLES"
            // for getTableTypes(). If you try to use this in a call to
            // DatabaseMetaData.getTables() no tables will be found. For the
            // moment hard code the types for InstantDB.
            if (listSize == 1 && _dbProductName.equals("InstantDB")) {
                list.clear();
                list.add("TABLE");
                list.add("SYSTEM TABLE");

            // At least one version of PostgreSQL through the ODBC/JDBC
            // bridge returns an empty result set for the list of table
            // types.
            } else if (listSize == 0 && _dbProductName.equals("PostgreSQL")) {
                list.add("TABLE");
                list.add("SYSTEM TABLE");
                list.add("VIEW");
                list.add("INDEX");
                list.add("SYSTEM INDEX");
                list.add("SEQUENCE");
            }

            return (String[])list.toArray(new String[list.size()]);
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public ResultSet getTypeInfo()
            throws NoConnectionException, BaseSQLException {
        try {
            return getMetaData().getTypeInfo();
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public IUDTInfo[] getUDTs(String catalog, String schemaPattern,
                                String typeNamePattern, int[] types)
            throws NoConnectionException, BaseSQLException {
        DatabaseMetaData md = getMetaData();
        ArrayList list = new ArrayList();
        try {
            ResultSet rs = md.getUDTs(catalog, schemaPattern,
                                        typeNamePattern, types);
            while (rs.next()) {
                list.add(new UDTInfo(rs, this));
            }
            return (IUDTInfo[])list.toArray(new IUDTInfo[list.size()]);
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public String getUserName()
            throws NoConnectionException, BaseSQLException {
        try {
            return getMetaData().getUserName();
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public ResultSet getVersionColumns(ITableInfo ti)
            throws NoConnectionException, BaseSQLException {
        try {
            return getMetaData().getVersionColumns(ti.getCatalogName(),
                                            ti.getSchemaName(),
                                            ti.getSimpleName());
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public boolean supportsCatalogsInTableDefinitions() throws NoConnectionException, BaseSQLException {
        try {
            return getMetaData().supportsCatalogsInTableDefinitions();
        } catch (SQLException ex) {
            if (_dbDriverName.equals(DriverNames.FREE_TDS)) {
                return true;
            }
            throw new BaseSQLException(ex);
        }
    }

    public boolean supportsCatalogsInDataManipulation() throws NoConnectionException, BaseSQLException {
        try {
            return getMetaData().supportsCatalogsInDataManipulation();
        } catch (SQLException ex) {
            if (_dbDriverName.equals(DriverNames.FREE_TDS)) {
                return true;
            }
            throw new BaseSQLException(ex);
        }
    }

    public boolean supportsCatalogsInProcedureCalls() throws NoConnectionException, BaseSQLException {
        try {
            return getMetaData().supportsCatalogsInProcedureCalls();
        } catch (SQLException ex) {
            if (_dbDriverName.equals(DriverNames.FREE_TDS)) {
                return true;
            }
            throw new BaseSQLException(ex);
        }
    }

    public boolean supportsSchemasInDataManipulation() throws NoConnectionException, BaseSQLException {
        try {
            return getMetaData().supportsSchemasInDataManipulation();
        } catch (SQLException ex) {
            if (_dbDriverName.equals(DriverNames.FREE_TDS)) {
                return true;
            }
            throw new BaseSQLException(ex);
        }
    }

    public boolean supportsSchemasInTableDefinitions() throws NoConnectionException, BaseSQLException {
        try {
            return getMetaData().supportsSchemasInTableDefinitions();
        } catch (SQLException ex) {
            if (_dbDriverName.equals(DriverNames.FREE_TDS)) {
                return true;
            }
            throw new BaseSQLException(ex);
        }
    }

    public boolean supportsStoredProcedures()
            throws NoConnectionException, BaseSQLException {
        try {
            return getMetaData().supportsStoredProcedures();
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public SQLWarning getWarnings() throws NoConnectionException, BaseSQLException {
        validateConnection();
        try {
            return _conn.getWarnings();
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    public MetaDataDataSet createMetaDataDataSet(IMessageHandler msgHandler)
            throws NoConnectionException, BaseSQLException, DataSetException {
        return new MetaDataDataSet(getMetaData(), msgHandler);
    }

    public DatabaseMetaData getMetaData()
            throws NoConnectionException {
        validateConnection();
        return _md;
    }

    public Connection getConnection() {
        return _conn;
    }

    protected void validateConnection() throws NoConnectionException {
        if (_conn == null) {
            throw new NoConnectionException();
        }
    }

    private void loadMetaData() throws SQLException {
        _md = getConnection().getMetaData();
        try {
            _dbProductName = _md.getDatabaseProductName();
        } catch (SQLException ignore) {
            _dbProductName = "";
        }
        try {
            _dbDriverName = _md.getDriverName().trim();
        } catch (SQLException ignore) {
            _dbDriverName = "";
        }
    }
}
