/*
 * Copyright (C) 2006 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.squirrel_sql.jdbcproxy;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ProxyResultSetMetaData implements ResultSetMetaData {

    private ResultSetMetaData _rsmd = null;
    
    public ProxyResultSetMetaData(ResultSetMetaData rsmd) {
        _rsmd = rsmd;
    }
    
    public int getColumnCount() throws SQLException {
		ProxyMethodManager.check("ProxyResultSetMetaData", "getColumnCount");
		return _rsmd.getColumnCount();
	}
    public int getColumnDisplaySize(int column) throws SQLException {
		ProxyMethodManager.check("ProxyResultSetMetaData", "getColumnDisplaySize");
		return _rsmd.getColumnDisplaySize(column);
	}
    public int getColumnType(int column) throws SQLException {
		ProxyMethodManager.check("ProxyResultSetMetaData", "getColumnType");
		return _rsmd.getColumnType(column);
	}
    public int getPrecision(int column) throws SQLException {
		ProxyMethodManager.check("ProxyResultSetMetaData", "getPrecision");
		return _rsmd.getPrecision(column);
	}
    public int getScale(int column) throws SQLException {
		ProxyMethodManager.check("ProxyResultSetMetaData", "getScale");
		return _rsmd.getScale(column);
	}
    public int isNullable(int column) throws SQLException {
		ProxyMethodManager.check("ProxyResultSetMetaData", "isNullable");
		return _rsmd.isNullable(column);
	}
    public boolean isAutoIncrement(int column) throws SQLException {
		ProxyMethodManager.check("ProxyResultSetMetaData", "isAutoIncrement");
		return _rsmd.isAutoIncrement(column);
	}
    public boolean isCaseSensitive(int column) throws SQLException {
		ProxyMethodManager.check("ProxyResultSetMetaData", "isCaseSensitive");
		return _rsmd.isCaseSensitive(column);
	}
    public boolean isCurrency(int column) throws SQLException {
		ProxyMethodManager.check("ProxyResultSetMetaData", "isCurrency");
		return _rsmd.isCurrency(column);
	}
    public boolean isDefinitelyWritable(int column) throws SQLException {
		ProxyMethodManager.check("ProxyResultSetMetaData", "isDefinitelyWritable");
		return _rsmd.isDefinitelyWritable(column);
	}
    public boolean isReadOnly(int column) throws SQLException {
		ProxyMethodManager.check("ProxyResultSetMetaData", "isReadOnly");
		return _rsmd.isReadOnly(column);
	}
    public boolean isSearchable(int column) throws SQLException {
		ProxyMethodManager.check("ProxyResultSetMetaData", "isSearchable");
		return _rsmd.isSearchable(column);
	}
    public boolean isSigned(int column) throws SQLException {
		ProxyMethodManager.check("ProxyResultSetMetaData", "isSigned");
		return _rsmd.isSigned(column);
	}
    public boolean isWritable(int column) throws SQLException {
		ProxyMethodManager.check("ProxyResultSetMetaData", "isWritable");
		return _rsmd.isWritable(column);
	}
    public String getCatalogName(int column) throws SQLException {
		ProxyMethodManager.check("ProxyResultSetMetaData", "getCatalogName");
		return _rsmd.getCatalogName(column);
	}
    public String getColumnClassName(int column) throws SQLException {
		ProxyMethodManager.check("ProxyResultSetMetaData", "getColumnClassName");
		return _rsmd.getColumnClassName(column);
	}
    public String getColumnLabel(int column) throws SQLException {
		ProxyMethodManager.check("ProxyResultSetMetaData", "getColumnLabel");
		return _rsmd.getColumnLabel(column);
	}
    public String getColumnName(int column) throws SQLException {
		ProxyMethodManager.check("ProxyResultSetMetaData", "getColumnName");
		return _rsmd.getColumnName(column);
	}
    public String getColumnTypeName(int column) throws SQLException {
		ProxyMethodManager.check("ProxyResultSetMetaData", "getColumnTypeName");
		return _rsmd.getColumnTypeName(column);
	}
    public String getSchemaName(int column) throws SQLException {
		ProxyMethodManager.check("ProxyResultSetMetaData", "getSchemaName");
		return _rsmd.getSchemaName(column);
	}
    public String getTableName(int column) throws SQLException {
		ProxyMethodManager.check("ProxyResultSetMetaData", "getTableName");
		return _rsmd.getTableName(column);
	}

	/**
	 * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
	 */
	public boolean isWrapperFor(Class<?> iface) throws SQLException
	{

		return false;
	}

	/**
	 * @see java.sql.Wrapper#unwrap(java.lang.Class)
	 */
	public <T> T unwrap(Class<T> iface) throws SQLException
	{

		return null;
	}
    
    
}
