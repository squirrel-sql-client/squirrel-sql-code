/*
 * Copyright (C) CollabraSpace Inc. All rights reserved.
 */
package net.sourceforge.squirrel_sql.jdbcproxy;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.jdbcproxy.ProxyResultSet;

public class ProxyResultSetMetaData implements ResultSetMetaData {

    
    
    public ProxyResultSetMetaData(ResultSetMetaData rsmd) {
        
    }
    
    public int getColumnCount() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getColumnDisplaySize(int column) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getColumnType(int column) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getPrecision(int column) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getScale(int column) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public int isNullable(int column) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean isAutoIncrement(int column) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isCaseSensitive(int column) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isCurrency(int column) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isDefinitelyWritable(int column) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isReadOnly(int column) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isSearchable(int column) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isSigned(int column) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isWritable(int column) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    public String getCatalogName(int column) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getColumnClassName(int column) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getColumnLabel(int column) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getColumnName(int column) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getColumnTypeName(int column) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getSchemaName(int column) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getTableName(int column) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

}
