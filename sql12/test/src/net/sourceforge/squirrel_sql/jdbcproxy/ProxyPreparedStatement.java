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
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

public class ProxyPreparedStatement extends ProxyStatement implements
        PreparedStatement {

    private PreparedStatement _pstmt = null;
    
    public ProxyPreparedStatement(ProxyConnection con, PreparedStatement stmt) {
        super(con, stmt);
        _pstmt = stmt;
    }
    
    public int executeUpdate() throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "executeUpdate");
		return _pstmt.executeUpdate();
	}
    public void addBatch() throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "addBatch");
		_pstmt.addBatch();
	}

    public void clearParameters() throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "clearParameters");
		_pstmt.clearParameters();
	}

    public boolean execute() throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "execute");
		return _pstmt.execute();
	}
    public void setByte(int parameterIndex, byte x) throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setByte");
		_pstmt.setByte(parameterIndex,x);
	}

    public void setDouble(int parameterIndex, double x) throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setDouble");
		_pstmt.setDouble(parameterIndex,x);
	}

    public void setFloat(int parameterIndex, float x) throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setFloat");
		_pstmt.setFloat(parameterIndex,x);
	}

    public void setInt(int parameterIndex, int x) throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setInt");
		_pstmt.setInt(parameterIndex,x);
	}

    public void setNull(int parameterIndex, int sqlType) throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setNull");
		_pstmt.setNull(parameterIndex,sqlType);
	}

    public void setLong(int parameterIndex, long x) throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setLong");
		_pstmt.setLong(parameterIndex,x);
	}

    public void setShort(int parameterIndex, short x) throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setShort");
		_pstmt.setShort(parameterIndex,x);
	}

    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setBoolean");
		_pstmt.setBoolean(parameterIndex,x);
	}

    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setBytes");
		_pstmt.setBytes(parameterIndex,x);
	}

    public void setAsciiStream(int parameterIndex, InputStream x, int length)
            throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setAsciiStream");
		_pstmt.setAsciiStream(parameterIndex,x,length);
	}

    public void setBinaryStream(int parameterIndex, InputStream x, int length)
            throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setBinaryStream");
		_pstmt.setBinaryStream(parameterIndex,x,length);
	}

    @SuppressWarnings("deprecation")
    public void setUnicodeStream(int parameterIndex, InputStream x, int length)
            throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setUnicodeStream");
		_pstmt.setUnicodeStream(parameterIndex,x,length);
	}

    public void setCharacterStream(int parameterIndex, Reader reader, int length)
            throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setCharacterStream");
		_pstmt.setCharacterStream(parameterIndex,reader,length);
	}

    public void setObject(int parameterIndex, Object x) throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setObject");
		_pstmt.setObject(parameterIndex,x);
	}

    public void setObject(int parameterIndex, Object x, int targetSqlType)
            throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setObject");
		_pstmt.setObject(parameterIndex,x,targetSqlType,targetSqlType);
	}

    public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) 
            throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setObject");
		_pstmt.setObject(parameterIndex,x,targetSqlType,scale);
	}

    public void setNull(int paramIndex, int sqlType, String typeName)
            throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setNull");
		_pstmt.setNull(paramIndex,sqlType,typeName);
	}

    public void setString(int parameterIndex, String x) throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setString");
		_pstmt.setString(parameterIndex,x);
	}

    public void setBigDecimal(int parameterIndex, BigDecimal x)
            throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setBigDecimal");
		_pstmt.setBigDecimal(parameterIndex,x);
	}

    public void setURL(int parameterIndex, URL x) throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setURL");
		_pstmt.setURL(parameterIndex,x);
	}

    public void setArray(int i, Array x) throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setArray");
		_pstmt.setArray(i,x);
	}

    public void setBlob(int i, Blob x) throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setBlob");
		_pstmt.setBlob(i,x);
	}

    public void setClob(int i, Clob x) throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setClob");
		_pstmt.setClob(i,x);
	}

    public void setDate(int parameterIndex, Date x) throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setDate");
		_pstmt.setDate(parameterIndex,x);
	}

    public ParameterMetaData getParameterMetaData() throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "getParameterMetaData");
		return _pstmt.getParameterMetaData();
	}
    public void setRef(int i, Ref x) throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setRef");
		_pstmt.setRef(i,x);
	}

    public ResultSet executeQuery() throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "executeQuery");
		return new ProxyResultSet(this, _pstmt.executeQuery());
	}
    public ResultSetMetaData getMetaData() throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "getMetaData");
		return new ProxyResultSetMetaData(_pstmt.getMetaData());
	}
    public void setTime(int parameterIndex, Time x) throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setTime");
		_pstmt.setTime(parameterIndex,x);
	}

    public void setTimestamp(int parameterIndex, Timestamp x)
            throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setTimestamp");
		_pstmt.setTimestamp(parameterIndex,x);
	}

    public void setDate(int parameterIndex, Date x, Calendar cal)
            throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setDate");
		_pstmt.setDate(parameterIndex,x);
	}

    public void setTime(int parameterIndex, Time x, Calendar cal)
            throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setTime");
		_pstmt.setTime(parameterIndex,x);
	}

    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
            throws SQLException {
		ProxyMethodManager.check("ProxyPreparedStatement", "setTimestamp");
		_pstmt.setTimestamp(parameterIndex,x);
	}

}