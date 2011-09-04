/*
 * Copyright (C) 2011 Rob Manning
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

package net.sourceforge.squirrel_sql.plugins.dbcopy.cli;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

/**
 * A simple delegator to a wrapped Connection that omits any call to close.  This is
 * necessary as we want to re-use the Connection in a Session for DBMonster, yet DBMonster
 * wants to close the Connection when it is finished with it.
 */
public class NonClosingConnection implements Connection
{
	private Connection con  = null;
	
	public NonClosingConnection(Connection con) {
		this.con = con;
	}
	
	@Override
	public void clearWarnings() throws SQLException
	{
		con.clearWarnings();

	}

	@Override
	public void close() throws SQLException
	{
//		Thread.dumpStack();
	}

	@Override
	public void commit() throws SQLException
	{
		con.commit();

	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException
	{
		
		return con.createArrayOf(typeName, elements);
	}

	@Override
	public Blob createBlob() throws SQLException
	{
		
		return con.createBlob();
	}

	@Override
	public Clob createClob() throws SQLException
	{
		
		return con.createClob();
	}

	@Override
	public NClob createNClob() throws SQLException
	{
		
		return con.createNClob();
	}

	@Override
	public SQLXML createSQLXML() throws SQLException
	{
		
		return con.createSQLXML();
	}

	@Override
	public Statement createStatement() throws SQLException
	{
		
		return con.createStatement();
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException
	{
		
		return con.createStatement(resultSetType, resultSetConcurrency);
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
		throws SQLException
	{
		
		return con.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException
	{
		
		return con.createStruct(typeName, attributes);
	}

	@Override
	public boolean getAutoCommit() throws SQLException
	{
		
		return con.getAutoCommit();
	}

	@Override
	public String getCatalog() throws SQLException
	{
		
		return con.getCatalog();
	}

	@Override
	public Properties getClientInfo() throws SQLException
	{
		
		return con.getClientInfo();
	}

	@Override
	public String getClientInfo(String name) throws SQLException
	{
		
		return con.getClientInfo(name);
	}

	@Override
	public int getHoldability() throws SQLException
	{
		
		return con.getHoldability();
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException
	{
		
		return con.getMetaData();
	}

	@Override
	public int getTransactionIsolation() throws SQLException
	{
		
		return con.getTransactionIsolation();
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException
	{
		
		return con.getTypeMap();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException
	{
		
		return con.getWarnings();
	}

	@Override
	public boolean isClosed() throws SQLException
	{
		
		return con.isClosed();
	}

	@Override
	public boolean isReadOnly() throws SQLException
	{
		
		return con.isReadOnly();
	}

	@Override
	public boolean isValid(int timeout) throws SQLException
	{
		
		return con.isValid(timeout);
	}

	@Override
	public String nativeSQL(String sql) throws SQLException
	{
		
		return con.nativeSQL(sql);
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException
	{
		
		return con.prepareCall(sql);
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
		throws SQLException
	{
		
		return con.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
		int resultSetHoldability) throws SQLException
	{
		
		return con.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException
	{
		
		return con.prepareStatement(sql);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException
	{
		
		return con.prepareStatement(sql, autoGeneratedKeys);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException
	{
		
		return con.prepareStatement(sql, columnIndexes);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException
	{
		
		return con.prepareStatement(sql, columnNames);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
		throws SQLException
	{
		
		return con.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
		int resultSetHoldability) throws SQLException
	{
		
		return con.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException
	{
		con.releaseSavepoint(savepoint);

	}

	@Override
	public void rollback() throws SQLException
	{
		con.rollback();

	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException
	{
		con.rollback(savepoint);

	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException
	{
		con.setAutoCommit(autoCommit);

	}

	@Override
	public void setCatalog(String catalog) throws SQLException
	{
		con.setCatalog(catalog);

	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException
	{
		
		con.setClientInfo(properties);
	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException
	{
		con.setClientInfo(name, value);

	}

	@Override
	public void setHoldability(int holdability) throws SQLException
	{
		con.setHoldability(holdability);

	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException
	{
		con.setReadOnly(readOnly);

	}

	@Override
	public Savepoint setSavepoint() throws SQLException
	{
		
		return con.setSavepoint();
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException
	{
		return con.setSavepoint(name);
		
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException
	{
		con.setTransactionIsolation(level);

	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException
	{
		con.setTypeMap(map);

	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException
	{
		
		return con.isWrapperFor(iface);
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException
	{
		
		return con.unwrap(iface);
	}

}
