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
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;

public class ProxyResultSet implements ResultSet
{

	private ResultSet _rs = null;

	private ProxyStatement _stmt = null;

	private ProxyResultSetMetaData _rsmd = null;

	public ProxyResultSet(ProxyStatement stmt, ResultSet rs) throws SQLException
	{
		_rs = rs;
		_stmt = stmt;
		_rsmd = new ProxyResultSetMetaData(rs.getMetaData());
	}

	public int getConcurrency() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getConcurrency");
		return _rs.getConcurrency();
	}

	public int getFetchDirection() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getFetchDirection");
		return _rs.getFetchDirection();
	}

	public int getFetchSize() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getFetchSize");
		return _rs.getFetchSize();
	}

	public int getRow() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getRow");
		return _rs.getRow();
	}

	public int getType() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getType");
		return _rs.getType();
	}

	public void afterLast() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "afterLast");
		_rs.afterLast();
	}

	public void beforeFirst() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "beforeFirst");
		_rs.beforeFirst();
	}

	public void cancelRowUpdates() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "cancelRowUpdates");
		_rs.cancelRowUpdates();
	}

	public void clearWarnings() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "clearWarnings");
		_rs.clearWarnings();
	}

	public void close() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "close");
		_rs.close();
	}

	public void deleteRow() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "deleteRow");
		_rs.deleteRow();
	}

	public void insertRow() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "insertRow");
		_rs.insertRow();
	}

	public void moveToCurrentRow() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "moveToCurrentRow");
		_rs.moveToCurrentRow();
	}

	public void moveToInsertRow() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "moveToInsertRow");
		_rs.moveToInsertRow();
	}

	public void refreshRow() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "refreshRow");
		_rs.refreshRow();
	}

	public void updateRow() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateRow");
		_rs.updateRow();
	}

	public boolean first() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "first");
		return _rs.first();
	}

	public boolean isAfterLast() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "isAfterLast");
		return _rs.isAfterLast();
	}

	public boolean isBeforeFirst() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "isBeforeFirst");
		return _rs.isBeforeFirst();
	}

	public boolean isFirst() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "isFirst");
		return _rs.isFirst();
	}

	public boolean isLast() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "isLast");
		return _rs.isLast();
	}

	public boolean last() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "last");
		return _rs.last();
	}

	public boolean next() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "next");
		return _rs.next();
	}

	public boolean previous() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "previous");
		return _rs.previous();
	}

	public boolean rowDeleted() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "rowDeleted");
		return _rs.rowDeleted();
	}

	public boolean rowInserted() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "rowInserted");
		return _rs.rowInserted();
	}

	public boolean rowUpdated() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "rowUpdated");
		return _rs.rowUpdated();
	}

	public boolean wasNull() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "wasNull");
		return _rs.wasNull();
	}

	public byte getByte(int columnIndex) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getByte");
		return _rs.getByte(columnIndex);
	}

	public double getDouble(int columnIndex) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getDouble");
		return _rs.getDouble(columnIndex);
	}

	public float getFloat(int columnIndex) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getFloat");
		return _rs.getFloat(columnIndex);
	}

	public int getInt(int columnIndex) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getInt");
		return _rs.getInt(columnIndex);
	}

	public long getLong(int columnIndex) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getLong");
		return _rs.getLong(columnIndex);
	}

	public short getShort(int columnIndex) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getShort");
		return _rs.getShort(columnIndex);
	}

	public void setFetchDirection(int direction) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "setFetchDirection");
		_rs.setFetchDirection(direction);
	}

	public void setFetchSize(int rows) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "setFetchSize");
		_rs.setFetchSize(rows);
	}

	public void updateNull(int columnIndex) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateNull");
		_rs.updateNull(columnIndex);
	}

	public boolean absolute(int row) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "absolute");
		return _rs.absolute(row);
	}

	public boolean getBoolean(int columnIndex) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getBoolean");
		return _rs.getBoolean(columnIndex);
	}

	public boolean relative(int rows) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "relative");
		return _rs.relative(rows);
	}

	public byte[] getBytes(int columnIndex) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getBytes");
		return _rs.getBytes(columnIndex);
	}

	public void updateByte(int columnIndex, byte x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateByte");
		_rs.updateByte(columnIndex, x);
	}

	public void updateDouble(int columnIndex, double x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateDouble");
		_rs.updateDouble(columnIndex, x);
	}

	public void updateFloat(int columnIndex, float x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateFloat");
		_rs.updateFloat(columnIndex, x);
	}

	public void updateInt(int columnIndex, int x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateInt");
		_rs.updateInt(columnIndex, x);
	}

	public void updateLong(int columnIndex, long x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateLong");
		_rs.updateLong(columnIndex, x);
	}

	public void updateShort(int columnIndex, short x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateShort");
		_rs.updateShort(columnIndex, x);
	}

	public void updateBoolean(int columnIndex, boolean x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateBoolean");
		_rs.updateBoolean(columnIndex, x);
	}

	public void updateBytes(int columnIndex, byte[] x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateBytes");
		_rs.updateBytes(columnIndex, x);
	}

	public InputStream getAsciiStream(int columnIndex) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getAsciiStream");
		return _rs.getAsciiStream(columnIndex);
	}

	public InputStream getBinaryStream(int columnIndex) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getBinaryStream");
		return _rs.getBinaryStream(columnIndex);
	}

	@SuppressWarnings("deprecation")
	public InputStream getUnicodeStream(int columnIndex) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getUnicodeStream");
		return _rs.getUnicodeStream(columnIndex);
	}

	public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateAsciiStream");
		_rs.updateAsciiStream(columnIndex, x, length);
	}

	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateBinaryStream");
		_rs.updateBinaryStream(columnIndex, x, length);
	}

	public Reader getCharacterStream(int columnIndex) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getCharacterStream");
		return _rs.getCharacterStream(columnIndex);
	}

	public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateCharacterStream");
		_rs.updateCharacterStream(columnIndex, x, length);
	}

	public Object getObject(int columnIndex) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getObject");
		return _rs.getObject(columnIndex);
	}

	public void updateObject(int columnIndex, Object x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateObject");
		_rs.updateObject(columnIndex, x);
	}

	public void updateObject(int columnIndex, Object x, int scale) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateObject");
		_rs.updateObject(columnIndex, x, scale);
	}

	public String getCursorName() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getCursorName");
		return _rs.getCursorName();
	}

	public String getString(int columnIndex) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getString");
		return _rs.getString(columnIndex);
	}

	public void updateString(int columnIndex, String x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateString");
		_rs.updateString(columnIndex, x);
	}

	public byte getByte(String columnName) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getByte");
		return _rs.getByte(columnName);
	}

	public double getDouble(String columnName) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getDouble");
		return _rs.getDouble(columnName);
	}

	public float getFloat(String columnName) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getFloat");
		return _rs.getFloat(columnName);
	}

	public int findColumn(String columnName) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "findColumn");
		return _rs.findColumn(columnName);
	}

	public int getInt(String columnName) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getInt");
		return _rs.getInt(columnName);
	}

	public long getLong(String columnName) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getLong");
		return _rs.getLong(columnName);
	}

	public short getShort(String columnName) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getShort");
		return _rs.getShort(columnName);
	}

	public void updateNull(String columnName) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateNull");
		_rs.updateNull(columnName);
	}

	public boolean getBoolean(String columnName) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getBoolean");
		return _rs.getBoolean(columnName);
	}

	public byte[] getBytes(String columnName) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getBytes");
		return _rs.getBytes(columnName);
	}

	public void updateByte(String columnName, byte x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateByte");
		_rs.updateByte(columnName, x);
	}

	public void updateDouble(String columnName, double x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateDouble");
		_rs.updateDouble(columnName, x);
	}

	public void updateFloat(String columnName, float x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateFloat");
		_rs.updateFloat(columnName, x);
	}

	public void updateInt(String columnName, int x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateInt");
		_rs.updateInt(columnName, x);
	}

	public void updateLong(String columnName, long x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateLong");
		_rs.updateLong(columnName, x);
	}

	public void updateShort(String columnName, short x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateShort");
		_rs.updateShort(columnName, x);
	}

	public void updateBoolean(String columnName, boolean x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateBoolean");
		_rs.updateBoolean(columnName, x);
	}

	public void updateBytes(String columnName, byte[] x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateBytes");
		_rs.updateBytes(columnName, x);
	}

	public BigDecimal getBigDecimal(int columnIndex) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getBigDecimal");
		return _rs.getBigDecimal(columnIndex);
	}

	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getBigDecimal");
		return _rs.getBigDecimal(columnIndex);
	}

	public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateBigDecimal");
		_rs.updateBigDecimal(columnIndex, x);
	}

	public URL getURL(int columnIndex) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getURL");
		return _rs.getURL(columnIndex);
	}

	public Array getArray(int i) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getArray");
		return _rs.getArray(i);
	}

	public void updateArray(int columnIndex, Array x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateArray");
		_rs.updateArray(columnIndex, x);
	}

	public Blob getBlob(int i) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getBlob");
		return _rs.getBlob(i);
	}

	public void updateBlob(int columnIndex, Blob x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateBlob");
		_rs.updateBlob(columnIndex, x);
	}

	public Clob getClob(int i) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getClob");
		return _rs.getClob(i);
	}

	public void updateClob(int columnIndex, Clob x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateClob");
		_rs.updateClob(columnIndex, x);
	}

	public Date getDate(int columnIndex) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getDate");
		return _rs.getDate(columnIndex);
	}

	public void updateDate(int columnIndex, Date x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateDate");
		_rs.updateDate(columnIndex, x);
	}

	public Ref getRef(int i) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getRef");
		return _rs.getRef(i);
	}

	public void updateRef(int columnIndex, Ref x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateRef");
		_rs.updateRef(columnIndex, x);
	}

	public ResultSetMetaData getMetaData() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getMetaData");
		return _rsmd;
	}

	public SQLWarning getWarnings() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getWarnings");
		return _rs.getWarnings();
	}

	public Statement getStatement() throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getStatement");
		return _stmt;
	}

	public Time getTime(int columnIndex) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getTime");
		return _rs.getTime(columnIndex);
	}

	public void updateTime(int columnIndex, Time x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateTime");
		_rs.updateTime(columnIndex, x);
	}

	public Timestamp getTimestamp(int columnIndex) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getTimestamp");
		return _rs.getTimestamp(columnIndex);
	}

	public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateTimestamp");
		_rs.updateTimestamp(columnIndex, x);
	}

	public InputStream getAsciiStream(String columnName) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getAsciiStream");
		return _rs.getAsciiStream(columnName);
	}

	public InputStream getBinaryStream(String columnName) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getBinaryStream");
		return _rs.getBinaryStream(columnName);
	}

	@SuppressWarnings("deprecation")
	public InputStream getUnicodeStream(String columnName) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getUnicodeStream");
		return _rs.getUnicodeStream(columnName);
	}

	public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateAsciiStream");
		_rs.updateAsciiStream(columnName, x, length);
	}

	public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateBinaryStream");
		_rs.updateBinaryStream(columnName, x, length);
	}

	public Reader getCharacterStream(String columnName) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getCharacterStream");
		return _rs.getCharacterStream(columnName);
	}

	public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateCharacterStream");
		_rs.updateCharacterStream(columnName, reader, length);
	}

	public Object getObject(String columnName) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getObject");
		return _rs.getObject(columnName);
	}

	public void updateObject(String columnName, Object x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateObject");
		_rs.updateObject(columnName, x);
	}

	public void updateObject(String columnName, Object x, int scale) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateObject");
		_rs.updateObject(columnName, x);
	}

	@SuppressWarnings("unchecked")
	public Object getObject(int i, Map map) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getObject");
		return _rs.getObject(i, map);
	}

	public String getString(String columnName) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getString");
		return _rs.getString(columnName);
	}

	public void updateString(String columnName, String x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateString");
		_rs.updateString(columnName, x);
	}

	public BigDecimal getBigDecimal(String columnName) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getBigDecimal");
		return _rs.getBigDecimal(columnName);
	}

	public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getBigDecimal");
		return _rs.getBigDecimal(columnName);
	}

	public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateBigDecimal");
		_rs.updateBigDecimal(columnName, x);
	}

	public URL getURL(String columnName) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getURL");
		return _rs.getURL(columnName);
	}

	public Array getArray(String colName) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getArray");
		return _rs.getArray(colName);
	}

	public void updateArray(String columnName, Array x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateArray");
		_rs.updateArray(columnName, x);
	}

	public Blob getBlob(String colName) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getBlob");
		return _rs.getBlob(colName);
	}

	public void updateBlob(String columnName, Blob x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateBlob");
		_rs.updateBlob(columnName, x);
	}

	public Clob getClob(String colName) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getClob");
		return _rs.getClob(colName);
	}

	public void updateClob(String columnName, Clob x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateClob");
		_rs.updateClob(columnName, x);
	}

	public Date getDate(String columnName) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getDate");
		return _rs.getDate(columnName);
	}

	public void updateDate(String columnName, Date x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateDate");
		_rs.updateDate(columnName, x);
	}

	public Date getDate(int columnIndex, Calendar cal) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getDate");
		return _rs.getDate(columnIndex, cal);
	}

	public Ref getRef(String colName) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getRef");
		return _rs.getRef(colName);
	}

	public void updateRef(String columnName, Ref x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateRef");
		_rs.updateRef(columnName, x);
	}

	public Time getTime(String columnName) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getTime");
		return _rs.getTime(columnName);
	}

	public void updateTime(String columnName, Time x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateTime");
		_rs.updateTime(columnName, x);
	}

	public Time getTime(int columnIndex, Calendar cal) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getTime");
		return _rs.getTime(columnIndex, cal);
	}

	public Timestamp getTimestamp(String columnName) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getTimestamp");
		return _rs.getTimestamp(columnName);
	}

	public void updateTimestamp(String columnName, Timestamp x) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "updateTimestamp");
		_rs.updateTimestamp(columnName, x);
	}

	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getTimestamp");
		return _rs.getTimestamp(columnIndex, cal);
	}

	@SuppressWarnings("unchecked")
	public Object getObject(String colName, Map map) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getObject");
		return _rs.getObject(colName, map);
	}

	public Date getDate(String columnName, Calendar cal) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getDate");
		return _rs.getDate(columnName, cal);
	}

	public Time getTime(String columnName, Calendar cal) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getTime");
		return _rs.getTime(columnName, cal);
	}

	public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException
	{
		ProxyMethodManager.check("ProxyResultSet", "getTimestamp");
		return _rs.getTimestamp(columnName, cal);
	}

	/**
	 * @see java.sql.ResultSet#getHoldability()
	 */
	public int getHoldability() throws SQLException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see java.sql.ResultSet#getNCharacterStream(int)
	 */
	public Reader getNCharacterStream(int columnIndex) throws SQLException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getNCharacterStream(java.lang.String)
	 */
	public Reader getNCharacterStream(String columnLabel) throws SQLException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see java.sql.ResultSet#isClosed()
	 */
	public boolean isClosed() throws SQLException
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream, long)
	 */
	public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream)
	 */
	public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream, long)
	 */
	public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream)
	 */
	public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream, long)
	 */
	public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream)
	 */
	public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream, long)
	 */
	public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream)
	 */
	public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.sql.ResultSet#updateBlob(int, java.io.InputStream, long)
	 */
	public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.sql.ResultSet#updateBlob(int, java.io.InputStream)
	 */
	public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.sql.ResultSet#updateBlob(java.lang.String, java.io.InputStream, long)
	 */
	public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.sql.ResultSet#updateBlob(java.lang.String, java.io.InputStream)
	 */
	public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader, long)
	 */
	public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader)
	 */
	public void updateCharacterStream(int columnIndex, Reader x) throws SQLException
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader, long)
	 */
	public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader)
	 */
	public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.sql.ResultSet#updateClob(int, java.io.Reader, long)
	 */
	public void updateClob(int columnIndex, Reader reader, long length) throws SQLException
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.sql.ResultSet#updateClob(int, java.io.Reader)
	 */
	public void updateClob(int columnIndex, Reader reader) throws SQLException
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.sql.ResultSet#updateClob(java.lang.String, java.io.Reader, long)
	 */
	public void updateClob(String columnLabel, Reader reader, long length) throws SQLException
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.sql.ResultSet#updateClob(java.lang.String, java.io.Reader)
	 */
	public void updateClob(String columnLabel, Reader reader) throws SQLException
	{
		// TODO Auto-generated method stub

	}

   public RowId getRowId(int columnIndex) throws SQLException
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public RowId getRowId(String columnLabel) throws SQLException
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public void updateRowId(int columnIndex, RowId x) throws SQLException
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void updateRowId(String columnLabel, RowId x) throws SQLException
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void updateNString(int columnIndex, String nString) throws SQLException
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void updateNString(String columnLabel, String nString) throws SQLException
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void updateNClob(int columnIndex, NClob nClob) throws SQLException
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void updateNClob(String columnLabel, NClob nClob) throws SQLException
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public NClob getNClob(int columnIndex) throws SQLException
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public NClob getNClob(String columnLabel) throws SQLException
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public SQLXML getSQLXML(int columnIndex) throws SQLException
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public SQLXML getSQLXML(String columnLabel) throws SQLException
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public String getNString(int columnIndex) throws SQLException
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public String getNString(String columnLabel) throws SQLException
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void updateNClob(int columnIndex, Reader reader) throws SQLException
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void updateNClob(String columnLabel, Reader reader) throws SQLException
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public <T> T unwrap(Class<T> iface) throws SQLException
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public boolean isWrapperFor(Class<?> iface) throws SQLException
   {
      return false;  //To change body of implemented methods use File | Settings | File Templates.
   }
}
