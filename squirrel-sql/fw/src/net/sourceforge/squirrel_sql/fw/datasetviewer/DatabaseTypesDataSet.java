package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2002 David MacLean
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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

public class DatabaseTypesDataSet implements IDataSet {

	private ResultSet _rs;
	private int[] _columnIndices;
	private int _columnCount;
	private DataSetDefinition _dataSetDefinition;
	private Object[] _row;


	public DatabaseTypesDataSet(ResultSet rs) throws DataSetException {
		this(rs, null);
	}


	public DatabaseTypesDataSet(ResultSet rs, int[] columnIndices)
			throws DataSetException {
		super();
		if (rs == null) {
			throw new IllegalArgumentException("Null ResultSet passed");
		}
	
		_rs = rs;
		if (columnIndices != null && columnIndices.length == 0) {
			columnIndices = null;
		}
		_columnIndices = columnIndices;
	
		try {
			ResultSetMetaData md = _rs.getMetaData();
			_columnCount = columnIndices != null ? columnIndices.length : md.getColumnCount();
			_dataSetDefinition = new DataSetDefinition(createColumnDefinitions(md, columnIndices));
			_row = new Object[_columnCount];
		} catch (SQLException ex) {
			throw new DataSetException(ex);
		}
	}

	public final int getColumnCount() {
		return _columnCount;
	}

	public DataSetDefinition getDataSetDefinition() {
		return _dataSetDefinition;
	}

	public synchronized boolean next(IMessageHandler msgHandler)
			throws DataSetException {
		boolean rc = false;

		try {
			rc = _rs.next();
			if (rc) {
				for (int i = 0; i < _columnCount; ++i) {
					int idx = _columnIndices != null ? _columnIndices[i] : i + 1;
					try {
						switch (idx) {

							case 2:
								// DATA_TYPE column of result set.
								int data = _rs.getInt(idx);
								_row[i] = String.valueOf(data) + " [" + JDBCTypeMapper.getJdbcTypeName(data) + "]";
								break;

							case 7:
								// NULLABLE column of result set.
								short nullable = _rs.getShort(idx);
								switch (nullable) {
									case DatabaseMetaData.typeNoNulls: {
										_row[i] = "false";
										break;
									}
									case DatabaseMetaData.typeNullable: {
										_row[i] = "true";
										break;
									}
									case DatabaseMetaData.typeNullableUnknown: {
										_row[i] = "unknown";
										break;
									}
									default: {
										_row[i] = nullable + "[error]";
										break;
									}
								}
								break;

							case 8:
							case 10:
							case 11:
							case 12:
								// boolean columns
								_row[i] = _rs.getBoolean(idx) ? "true" : "false";
								break;

							case 9:
								// SEARCHABLE column of result set.
								short searchable = _rs.getShort(idx);
								switch (searchable) {
									case DatabaseMetaData.typePredNone: {
										_row[i] = "no support";
										break;
									}
									case DatabaseMetaData.typePredChar: {
										_row[i] = "only supports 'WHERE...like'";
										break;
									}
									case DatabaseMetaData.typePredBasic: {
										_row[i] = "supports all except 'WHERE...LIKE'";
										break;
									}
									case DatabaseMetaData.typeSearchable: {
										_row[i] = "supports all WHERE";
										break;
									}
									default: {
										_row[i] = searchable + "[error]";
										break;
									}
								}
								break;

							default:
								_row[i] = _rs.getString(idx);
								break;

						}
					} catch (SQLException ex) {
						if (msgHandler != null) {
							_row[i] = "<error>"; //i18n
							msgHandler.showMessage(ex);
						} else {
							throw new DataSetException(ex);
						}
					}
				}
			}

			return rc;
	
		} catch (SQLException ex) {
			if (msgHandler != null) {
				msgHandler.showMessage(ex);
			} else {
				throw new DataSetException(ex);
			}
		}
	
		return rc;
	}

	public Object get(int columnIndex) {
		return _row[columnIndex];
	}

	private ColumnDisplayDefinition[] createColumnDefinitions(
				ResultSetMetaData md, int[] columnIndices)
			throws SQLException {

		ColumnDisplayDefinition[] columnDefs = new ColumnDisplayDefinition[_columnCount];
		for (int i = 0; i < _columnCount; ++i) {
			int idx = columnIndices != null ? columnIndices[i] : i + 1;
			columnDefs[i] = new ColumnDisplayDefinition(
			md.getColumnDisplaySize(idx), md.getColumnLabel(idx));
		}
		return columnDefs;
	}
}
