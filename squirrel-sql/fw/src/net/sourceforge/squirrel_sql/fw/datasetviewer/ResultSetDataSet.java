package net.sourceforge.squirrel_sql.fw.datasetviewer;
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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

public class ResultSetDataSet implements IDataSet {

	// These 2 should be handled with an Iterator!!!
	private int _iCurrent = -1;
	private Object[] _currentRow;
	
	private int[] _columnIndices;
	private int _columnCount;
	private DataSetDefinition _dataSetDefinition;
	private ArrayList _alData;

	public ResultSetDataSet() throws DataSetException {
		this(null, null);
	}

	public ResultSetDataSet(ResultSet rs) throws DataSetException {
		this(rs, null);
	}

	public ResultSetDataSet(ResultSet rs, int[] columnIndices)
			throws IllegalArgumentException, DataSetException {
		super();
		setResultSet(rs, columnIndices);
	}

	public void setResultSet(ResultSet rs) throws DataSetException {
		setResultSet(rs, null);
	}

	public void setResultSet(ResultSet rs, int[] columnIndices) throws DataSetException {
		if (columnIndices != null && columnIndices.length == 0) {
			columnIndices = null;
		}
		_columnIndices = columnIndices;
		_iCurrent = -1;
		_alData = new ArrayList();
		
		if (rs != null) {
			try {
				ResultSetMetaData md = rs.getMetaData();
				_columnCount = columnIndices != null ? columnIndices.length : md.getColumnCount();
				_dataSetDefinition = new DataSetDefinition(createColumnDefinitions(md, columnIndices));
				while (rs.next()) {
					Object[] row = new Object[_columnCount];
					for (int i = 0; i < _columnCount; ++i) {
						int idx = _columnIndices != null ? _columnIndices[i] : i + 1;
//						row[i] = rs.getString(idx);
						switch (md.getColumnType(idx)) {
							case Types.BIT:
								row[i] = new Boolean(rs.getBoolean(idx));
								break;
							case Types.TIME:
							case Types.DATE:
							case Types.TIMESTAMP:
								row[i] = rs.getDate(idx);
								break;
							case Types.BIGINT:
								row[i] = new Long(rs.getLong(idx));
								break;
							case Types.DECIMAL:
							case Types.DOUBLE:
							case Types.FLOAT:
							case Types.NUMERIC:
							case Types.REAL:
								row[i] = new Double(rs.getDouble(idx));
								break;
							case Types.INTEGER:
							case Types.SMALLINT:
							case Types.TINYINT:
								row[i] = new Integer(rs.getInt(idx));
								break;
							case Types.CHAR:
							case Types.VARCHAR:
							case Types.LONGVARCHAR:
								String s = rs.getString(idx);
								if(s != null && s.length() > 50)
								{
									s = s.substring(0,45) + ".......";
								}
								row[i] = s;
								break;
							default:
								row[i] = "<Unknown>";
						}
					}
					_alData.add(row);
				}
			} catch (SQLException ex) {
				throw new DataSetException(ex);
			}
		}
	}

	public final int getColumnCount() {
		return _columnCount;
	}

	public DataSetDefinition getDataSetDefinition() {
		return _dataSetDefinition;
	}

	public synchronized boolean next(IMessageHandler msgHandler) throws DataSetException 
	{
		// This should be handled with an Iterator!!!
		if(++_iCurrent < _alData.size())
		{
			_currentRow = (Object[])_alData.get(_iCurrent);
			return true;
		}
		return false;
	}

	public Object get(int columnIndex) 
	{
		return _currentRow[columnIndex];
	}

	private ColumnDisplayDefinition[] createColumnDefinitions(ResultSetMetaData md,
														int[] columnIndices)
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
