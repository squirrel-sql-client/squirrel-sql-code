package net.sourceforge.squirrel_sql.mo.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

/**
 * A Mock ResultSet object useful for unit tests that require ResultSets.
 * 
 * @author manningr
 *
 */
public class MockResultSet implements ResultSet {

	/**
	 * ResultSetMetaData describing this result.
	 */
	ResultSetMetaData rsmd = null;
	
	/**
	 * The TableColumnInfos that describe the columns of the result.
	 */
	TableColumnInfo[] _infos = null;
	
	/**
	 * The simulated results from the "database"
	 */
	ArrayList<Object[]> rowData = new ArrayList<Object[]>();
    
	/** 
	 * The index into rowData of the next row to be used to return column 
	 * values
	 */
	int cursorIndex = -1;
	
	public MockResultSet() {
	}
	
    public void setMetaData(ResultSetMetaData md) {
        rsmd = md;
    }
    
	/**
	 * 
	 * @param infos an array of TableColumnInfo items that describe the columns
	 *              of the result set.
	 */
	public MockResultSet(TableColumnInfo[] infos) {
		_infos = infos;
		rsmd = new MockResultSetMetaData(infos);
	}
	
	public void setTableColumnInfos(TableColumnInfo[] infos) {
		_infos = infos;
		rsmd = new MockResultSetMetaData(infos);
	}
	
	/**
	 * Adds a row of values to the result.  Successive calls create additional
	 * rows.
	 * 
	 * @param values
	 */
	public void addRow(Object[] values) {
		rowData.add(values);
	}
	
	public boolean absolute(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public void afterLast() throws SQLException {
		// TODO Auto-generated method stub

	}

	public void beforeFirst() throws SQLException {
		// TODO Auto-generated method stub

	}

	public void cancelRowUpdates() throws SQLException {
		// TODO Auto-generated method stub

	}

	public void clearWarnings() throws SQLException {
		// TODO Auto-generated method stub

	}

	public void close() throws SQLException {
		// TODO Auto-generated method stub

	}

	public void deleteRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	public int findColumn(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean first() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public Array getArray(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Array getArray(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public InputStream getAsciiStream(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public InputStream getAsciiStream(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public BigDecimal getBigDecimal(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public BigDecimal getBigDecimal(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

    @SuppressWarnings("deprecation")
	public BigDecimal getBigDecimal(int arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

    @SuppressWarnings("deprecation")
	public BigDecimal getBigDecimal(String arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public InputStream getBinaryStream(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public InputStream getBinaryStream(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Blob getBlob(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Blob getBlob(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getBoolean(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean getBoolean(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public byte getByte(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public byte getByte(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public byte[] getBytes(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getBytes(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Reader getCharacterStream(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Reader getCharacterStream(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Clob getClob(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Clob getClob(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getConcurrency() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getCursorName() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getDate(int colIdx) throws SQLException {
        Object[] currentRow = currentRow();
        if (colIdx-1 < currentRow.length) {
            
            if (currentRow[colIdx-1] instanceof Date) {
                return (Date)currentRow[colIdx-1];    
            } else {
                throw new SQLException("not a Date: "+currentRow[colIdx-1].getClass().getName());
            }
        } else {
            throw new SQLException(
                "Error: requested value for non-existant column with index="+
                (colIdx-1));
        }
	}

	public Date getDate(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getDate(int arg0, Calendar arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getDate(String arg0, Calendar arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public double getDouble(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getDouble(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getFetchDirection() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getFetchSize() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getFloat(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getFloat(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getInt(int arg0) throws SQLException {
		return getIntValue(arg0);
	}

	public int getInt(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getLong(int arg0) throws SQLException {
		return getLongValue(arg0);
	}

	public long getLong(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		return rsmd;
	}

	public Object getObject(int column) throws SQLException {
		return currentRow()[column-1];
	}

	public Object getObject(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

    @SuppressWarnings("unchecked")
	public Object getObject(int arg0, Map arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@SuppressWarnings("unchecked")
    public Object getObject(String arg0, Map arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Ref getRef(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Ref getRef(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getRow() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public short getShort(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public short getShort(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Statement getStatement() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getString(int arg0) throws SQLException {
		return getStringValue(arg0);
	}

	public String getString(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Time getTime(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Time getTime(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Time getTime(int arg0, Calendar arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Time getTime(String arg0, Calendar arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Timestamp getTimestamp(int column) throws SQLException {
	    Object date = getObject(column);
        if (date instanceof java.util.Date) {
            long time = ((java.util.Date)date).getTime();
            return new Timestamp(time);
        } else {
            return (Timestamp)date;
        }
	}

	public Timestamp getTimestamp(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Timestamp getTimestamp(int arg0, Calendar arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Timestamp getTimestamp(String arg0, Calendar arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getType() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public URL getURL(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public URL getURL(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

    @SuppressWarnings("deprecation")
	public InputStream getUnicodeStream(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

    @SuppressWarnings("deprecation")
	public InputStream getUnicodeStream(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public SQLWarning getWarnings() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void insertRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	public boolean isAfterLast() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isBeforeFirst() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isFirst() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isLast() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean last() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public void moveToCurrentRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	public void moveToInsertRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	public boolean next() throws SQLException {
		// TODO Auto-generated method stub
		if (cursorIndex + 1 < rowData.size()) {
			cursorIndex++;
			return true;
		}
		return false;
	}

	public boolean previous() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public void refreshRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	public boolean relative(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean rowDeleted() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean rowInserted() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean rowUpdated() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public void setFetchDirection(int arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void setFetchSize(int arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateArray(int arg0, Array arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateArray(String arg0, Array arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateAsciiStream(int arg0, InputStream arg1, int arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateAsciiStream(String arg0, InputStream arg1, int arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBigDecimal(int arg0, BigDecimal arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBigDecimal(String arg0, BigDecimal arg1)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBinaryStream(int arg0, InputStream arg1, int arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBinaryStream(String arg0, InputStream arg1, int arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBlob(int arg0, Blob arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBlob(String arg0, Blob arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBoolean(int arg0, boolean arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBoolean(String arg0, boolean arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateByte(int arg0, byte arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateByte(String arg0, byte arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBytes(int arg0, byte[] arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBytes(String arg0, byte[] arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateCharacterStream(int arg0, Reader arg1, int arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateCharacterStream(String arg0, Reader arg1, int arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateClob(int arg0, Clob arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateClob(String arg0, Clob arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateDate(int arg0, Date arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateDate(String arg0, Date arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateDouble(int arg0, double arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateDouble(String arg0, double arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateFloat(int arg0, float arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateFloat(String arg0, float arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateInt(int arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateInt(String arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateLong(int arg0, long arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateLong(String arg0, long arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateNull(int arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateNull(String arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateObject(int arg0, Object arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateObject(String arg0, Object arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateObject(int arg0, Object arg1, int arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateObject(String arg0, Object arg1, int arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateRef(int arg0, Ref arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateRef(String arg0, Ref arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateShort(int arg0, short arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateShort(String arg0, short arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateString(int arg0, String arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateString(String arg0, String arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateTime(int arg0, Time arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateTime(String arg0, Time arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateTimestamp(int arg0, Timestamp arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateTimestamp(String arg0, Timestamp arg1)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public boolean wasNull() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * JDBC numbers it's columns starting at 1.  Since we use an array
	 * to represent columns, and the array has first element at index 0, 
	 * be sure to subtract 1 from the specific JDBC colIdx to look in our
	 * array.
	 * 
	 * @param colIdx
	 * @return
	 * @throws SQLException
	 */
	private String getStringValue(int colIdx) throws SQLException {
		Object[] currentRow = currentRow();
		if (colIdx-1 < currentRow.length) {
			return String.valueOf(currentRow[colIdx-1]);
		} else {
			throw new SQLException(
				"Error: requested value for non-existant column with index="+
				(colIdx-1));
		}
	}
	
	private int getIntValue(int colIdx) throws SQLException {
		return Integer.parseInt(getStringValue(colIdx));
	}

	private long getLongValue(int colIdx) throws SQLException {
		return Long.parseLong(getStringValue(colIdx));
	}	
	
	private Object[] currentRow() {
		return rowData.get(cursorIndex);
	}

}
