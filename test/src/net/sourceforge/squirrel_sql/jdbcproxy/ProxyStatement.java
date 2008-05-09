package net.sourceforge.squirrel_sql.jdbcproxy;

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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

public class ProxyStatement implements Statement
{

	Statement _stmt = null;

	ProxyConnection _con = null;

	protected ProxyStatement()
	{
	}

	public ProxyStatement(ProxyConnection con, Statement stmt)
	{
		_stmt = stmt;
		_con = con;
	}

	public int getFetchDirection() throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "getFetchDirection");
		return _stmt.getFetchDirection();
	}

	public int getFetchSize() throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "getFetchSize");
		return _stmt.getFetchSize();
	}

	public int getMaxFieldSize() throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "getMaxFieldSize");
		return _stmt.getMaxFieldSize();
	}

	public int getMaxRows() throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "getMaxRows");
		return _stmt.getMaxRows();
	}

	public int getQueryTimeout() throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "getQueryTimeout");
		return _stmt.getQueryTimeout();
	}

	public int getResultSetConcurrency() throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "getResultSetConcurrency");
		return _stmt.getResultSetConcurrency();
	}

	public int getResultSetHoldability() throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "getResultSetHoldability");
		return _stmt.getResultSetHoldability();
	}

	public int getResultSetType() throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "getResultSetType");
		return _stmt.getResultSetType();
	}

	public int getUpdateCount() throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "getUpdateCount");
		return _stmt.getUpdateCount();
	}

	public void cancel() throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "cancel");
		_stmt.cancel();
	}

	public void clearBatch() throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "clearBatch");
		_stmt.clearBatch();
	}

	public void clearWarnings() throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "clearWarnings");
		_stmt.clearWarnings();
	}

	public void close() throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "close");
		_stmt.close();
	}

	public boolean getMoreResults() throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "getMoreResults");
		return _stmt.getMoreResults();
	}

	public int[] executeBatch() throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "executeBatch");
		return _stmt.executeBatch();
	}

	public void setFetchDirection(int direction) throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "setFetchDirection");
		_stmt.setFetchDirection(direction);
	}

	public void setFetchSize(int rows) throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "setFetchSize");
		_stmt.setFetchSize(rows);
	}

	public void setMaxFieldSize(int max) throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "setMaxFieldSize");
		_stmt.setMaxFieldSize(max);
	}

	public void setMaxRows(int max) throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "setMaxRows");
		_stmt.setMaxRows(max);
	}

	public void setQueryTimeout(int seconds) throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "setQueryTimeout");
		_stmt.setQueryTimeout(seconds);
	}

	public boolean getMoreResults(int current) throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "getMoreResults");
		return _stmt.getMoreResults(current);
	}

	public void setEscapeProcessing(boolean enable) throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "setEscapeProcessing");
		_stmt.setEscapeProcessing(enable);
	}

	public int executeUpdate(String sql) throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "executeUpdate");
		return _stmt.executeUpdate(sql);
	}

	public void addBatch(String sql) throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "addBatch");
		_stmt.addBatch(sql);
	}

	public void setCursorName(String name) throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "setCursorName");
		_stmt.setCursorName(name);
	}

	public boolean execute(String sql) throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "execute");
		return _stmt.execute(sql);
	}

	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "executeUpdate");
		return _stmt.executeUpdate(sql, autoGeneratedKeys);
	}

	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "execute");
		return _stmt.execute(sql, autoGeneratedKeys);
	}

	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "executeUpdate");
		return _stmt.executeUpdate(sql, columnIndexes);
	}

	public boolean execute(String sql, int[] columnIndexes) throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "execute");
		return _stmt.execute(sql, columnIndexes);
	}

	public Connection getConnection() throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "getConnection");
		return _con;
	}

	public ResultSet getGeneratedKeys() throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "getGeneratedKeys");
		return new ProxyResultSet(this, _stmt.getGeneratedKeys());
	}

	public ResultSet getResultSet() throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "getResultSet");
		return new ProxyResultSet(this, _stmt.getResultSet());
	}

	public SQLWarning getWarnings() throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "getWarnings");
		return _stmt.getWarnings();
	}

	public int executeUpdate(String sql, String[] columnNames) throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "executeUpdate");
		return _stmt.executeUpdate(sql, columnNames);
	}

	public boolean execute(String sql, String[] columnNames) throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "execute");
		return _stmt.execute(sql, columnNames);
	}

	public ResultSet executeQuery(String sql) throws SQLException
	{
		ProxyMethodManager.check("ProxyStatement", "executeQuery");
		return new ProxyResultSet(this, _stmt.executeQuery(sql));
	}

	/**
	 * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
	 */
	public boolean isWrapperFor(Class<?> iface) throws SQLException
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see java.sql.Wrapper#unwrap(java.lang.Class)
	 */
	public <T> T unwrap(Class<T> iface) throws SQLException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see java.sql.Statement#isClosed()
	 */
	public boolean isClosed() throws SQLException
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see java.sql.Statement#isPoolable()
	 */
	public boolean isPoolable() throws SQLException
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see java.sql.Statement#setPoolable(boolean)
	 */
	public void setPoolable(boolean poolable) throws SQLException
	{
		// TODO Auto-generated method stub

	}

}