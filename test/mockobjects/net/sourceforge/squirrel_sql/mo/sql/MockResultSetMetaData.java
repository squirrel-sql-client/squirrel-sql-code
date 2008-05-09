package net.sourceforge.squirrel_sql.mo.sql;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

public class MockResultSetMetaData implements ResultSetMetaData {

	
	TableColumnInfo[] _infos = null;
	
	public MockResultSetMetaData(TableColumnInfo[] infos) {
		_infos = infos; 
	}
	
	public String getCatalogName(int arg0) throws SQLException {
		if (_infos == null || _infos.length <= 0) {
			return null;
		}
		return _infos[0].getCatalogName();
	}

	public String getColumnClassName(int arg0) throws SQLException {
		System.err.println(
			"MockResultSetMetaData.getColumnClassName: stub not yet implemented");
		return null;
	}

	public int getColumnCount() throws SQLException {
		if (_infos == null || _infos.length <= 0) {
			return 0;
		}
		return _infos.length;
	}

	public int getColumnDisplaySize(int arg0) throws SQLException {
		System.err.println(
			"MockResultSetMetaData.getColumnDisplaySize: stub not yet implemented");
		return 0;
	}

	public String getColumnLabel(int arg0) throws SQLException {
		System.err.println(
			"MockResultSetMetaData.getColumnLabel: stub not yet implemented");
		return null;
	}

	public String getColumnName(int arg0) throws SQLException {
		if (_infos == null || _infos.length <= 0) {
			return null;
		}
		return _infos[arg0].getColumnName();
	}

	public int getColumnType(int arg0) throws SQLException {
        // Need to adjust param which is 1-based(JDBC) to a 0-based number for 
        // array access
        int idx = arg0 - 1;
		if (_infos == null || _infos.length <= 0 || idx >= _infos.length) {
			return 0;
		}
		return _infos[idx].getDataType();
	}

	public String getColumnTypeName(int arg0) throws SQLException {
        // Need to adjust param which is 1-based(JDBC) to a 0-based number for 
        // array access
        int idx = arg0 - 1;        
		if (_infos == null || _infos.length <= 0 || idx >= _infos.length) {
			return null;
		}
		return _infos[idx].getTypeName();
	}

	public int getPrecision(int arg0) throws SQLException {
        // Need to adjust param which is 1-based(JDBC) to a 0-based number for 
        // array access
        int idx = arg0 - 1;                
		if (_infos == null || _infos.length <= 0 || idx >= _infos.length) {
			return 0;
		}
		return _infos[arg0].getColumnSize();
	}

	public int getScale(int arg0) throws SQLException {
        // Need to adjust param which is 1-based(JDBC) to a 0-based number for 
        // array access
        int idx = arg0 - 1;                        
		if (_infos == null || _infos.length <= 0 || idx >= _infos.length) {
			return 0;
		}
		return _infos[arg0].getDecimalDigits();
	}

	public String getSchemaName(int arg0) throws SQLException {
		if (_infos == null || _infos.length <= 0) {
			return null;
		}
		return _infos[0].getSchemaName();
	}

	public String getTableName(int arg0) throws SQLException {
		System.err.println(
			"MockResultSetMetaData.getTableName: stub not yet implemented");
		return null;
	}

	public boolean isAutoIncrement(int arg0) throws SQLException {
		System.err.println(
			"MockResultSetMetaData.isAutoIncrement: stub not yet implemented");
		return false;
	}

	public boolean isCaseSensitive(int arg0) throws SQLException {
		System.err.println(
			"MockResultSetMetaData.isCaseSensitive: stub not yet implemented");
		return false;
	}

	public boolean isCurrency(int arg0) throws SQLException {
		System.err.println(
			"MockResultSetMetaData.isCurrency: stub not yet implemented");
		return false;
	}

	public boolean isDefinitelyWritable(int arg0) throws SQLException {
		System.err.println(
			"MockResultSetMetaData.isDefinitelyWritable: stub not yet implemented");
		return false;
	}

	public int isNullable(int arg0) throws SQLException {
		if (_infos == null || _infos.length <= 0) {
			return 0;
		}
		return _infos[arg0].isNullAllowed();
	}

	public boolean isReadOnly(int arg0) throws SQLException {
		System.err.println(
			"MockResultSetMetaData.isReadOnly: stub not yet implemented");
		return false;
	}

	public boolean isSearchable(int arg0) throws SQLException {
		System.err.println(
			"MockResultSetMetaData.isSearchable: stub not yet implemented");
		return false;
	}

	public boolean isSigned(int arg0) throws SQLException {
		System.err.println(
			"MockResultSetMetaData.isSigned: stub not yet implemented");
		return false;
	}

	public boolean isWritable(int arg0) throws SQLException {
		System.err.println(
			"MockResultSetMetaData.isWritable: stub not yet implemented");
		return false;
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

	
}
