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
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class ResultSetMetaDataDataSet implements IDataSet {
	private interface i18n {
		String UNSUPPORTED = "<Unsupported>";
		String NAME_COLUMN = "Property Name";
		String NULL = "<null>";
		String VALUE_COLUMN = "Value";
	}

	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(ResultSetMetaDataDataSet.class);

	private DataSetDefinition _dsDef;
	private boolean[] _propertyMethodIndicators;
	private Iterator _rowsIter;
	private String[] _row;

	/**
	 * Data. Each element is an array of String objects representing a column from
	 * the result set metadata.
	 */
	private ArrayList _data = new ArrayList();

	public ResultSetMetaDataDataSet() throws DataSetException {
		this((ResultSetMetaData)null);
	}

	public ResultSetMetaDataDataSet(ResultSet rs)
			throws IllegalArgumentException, DataSetException {
		this(getMetaDataFromResultSet(rs));
	}

	public ResultSetMetaDataDataSet(ResultSetMetaData md)
			throws IllegalArgumentException, DataSetException {
		super();
		setResultSetMetaData(md);
	}

	public synchronized void setResultSetMetaData(ResultSetMetaData md) throws DataSetException {
		_dsDef = new DataSetDefinition(createColumnDefinitions(md));
		load(md);
	}

	public final int getColumnCount() {
		return _dsDef.getColumnDefinitions().length;
	}

	public DataSetDefinition getDataSetDefinition() {
		return _dsDef;
	}

	public synchronized boolean next(IMessageHandler msgHandler) throws DataSetException {
		if (_rowsIter.hasNext()) {
			_row = (String[])_rowsIter.next();
		} else {
			_row = null;
		}
		return _row != null;
	}

	public Object get(int columnIndex) {
		return _row[columnIndex];
	}

	private ColumnDisplayDefinition[] createColumnDefinitions(ResultSetMetaData md) {
		final Method[] methods = ResultSetMetaData.class.getMethods();
		_propertyMethodIndicators = new boolean[methods.length];
		final List colDefs = new ArrayList();
		for (int i = 0; i < methods.length; ++i) {
			if (isPropertyMethod(methods[i])) {
				colDefs.add(new ColumnDisplayDefinition(200, methods[i].getName()));
				_propertyMethodIndicators[i] = true;
			} else {
				_propertyMethodIndicators[i] = false;
			}
		}
		return (ColumnDisplayDefinition[])colDefs.toArray(new ColumnDisplayDefinition[colDefs.size()]);
	}

	private void load(ResultSetMetaData md) {
		try {
			final Method[] methods = ResultSetMetaData.class.getMethods();
			final ArrayList line = new ArrayList();
			for (int metaIdx = 1, metaLimit = md.getColumnCount() + 1;
					metaIdx < metaLimit; ++metaIdx) {
				Object[] methodParms = new Object[] {
					new Integer(metaIdx),
				};
				line.clear();
				line.ensureCapacity(methods.length);
				for (int methodIdx = 0; methodIdx < methods.length; ++methodIdx) {
					if (_propertyMethodIndicators[methodIdx]) {
						Object obj = executeGetter(md, methods[methodIdx], methodParms);
						line.add(obj != null ? obj.toString() : i18n.NULL);
					}
				}
				_data.add(line.toArray(new String[line.size()]));
			}
	
			_rowsIter = _data.iterator();
		} catch (Throwable th) {
			//??Alert the user.
			s_log.error("Error occured processing result set", th);
		}
	}

	/**
	 * A valid method for a property in <TT>ResultSetMetaData</TT> is one that
	 * has a non-void ouput and takes a single integer parameter (the column index).
	 * 
	 * @return	<TT>true</TT> if method is a property getter else <TT>false</TT>.
	 */
	protected boolean isPropertyMethod(Method method) {
		return method.getParameterTypes().length == 1 &&
				method.getParameterTypes()[0] == int.class &&
				method.getReturnType() != Void.TYPE;
	}

    protected Object executeGetter(Object bean, Method getter, Object[] parms) {
        try {
            return getter.invoke(bean, parms);
        } catch(Throwable th) {
            return i18n.UNSUPPORTED;
        }
    }

	private static ResultSetMetaData getMetaDataFromResultSet(ResultSet rs)
			throws IllegalArgumentException, DataSetException {
		if (rs == null) {
			throw new IllegalArgumentException("Null ResultSet passed");
		}
		try {
	   		return rs.getMetaData();
		} catch (SQLException ex) {
			throw new DataSetException(ex);
		}
	}
}
