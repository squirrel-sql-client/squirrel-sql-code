package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
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
import java.lang.reflect.Method;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.NullMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class MetaDataDataSet implements IDataSet
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(MetaDataDataSet.class);

	private final static Map<String, Object> s_ignoreMethods = 
        new HashMap<String, Object>();
	static
	{
		s_ignoreMethods.put("getCatalogs", null);
		s_ignoreMethods.put("getConnection", null);
		s_ignoreMethods.put("getSchemas", null);
		s_ignoreMethods.put("getTableTypes", null);
		s_ignoreMethods.put("getTypeInfo", null);
		s_ignoreMethods.put("fail", null);
		s_ignoreMethods.put("hashCode", null);
		s_ignoreMethods.put("toString", null);
		s_ignoreMethods.put("getNumericFunctions", null);
		s_ignoreMethods.put("getStringFunctions", null);
		s_ignoreMethods.put("getSystemFunctions", null);
		s_ignoreMethods.put("getTimeDateFunctions", null);
		s_ignoreMethods.put("getSQLKeywords", null);
	}

	private static interface IStrings
	{
		String UNSUPPORTED = s_stringMgr.getString("MetaDataDataSet.unsupported");
		String NAME_COLUMN = s_stringMgr.getString("MetaDataDataSet.propname");
		String VALUE_COLUMN = s_stringMgr.getString("MetaDataDataSet.value");
	}

	private final static String[] s_hdgs =
		new String[] { IStrings.NAME_COLUMN, IStrings.VALUE_COLUMN };
	private DataSetDefinition _dsDef;

	private Iterator<Object[]> _rowsIter;
	private Object[] _row;

	private IMessageHandler _msgHandler;

	/**
	 * Data. Each element represents a row of the table and is made up of
	 * an array of strings. Each string is an element in the row.
	 */
	private List<Object[]> _data = new ArrayList<Object[]>();

	public MetaDataDataSet(DatabaseMetaData md)
	{
		this(md, null);
	}

	public MetaDataDataSet(DatabaseMetaData md, IMessageHandler msgHandler)
	{
		super();
		_msgHandler =
			msgHandler != null ? msgHandler : NullMessageHandler.getInstance();
		_dsDef = new DataSetDefinition(createColumnDefinitions());
		load(md);
	}

	public final int getColumnCount()
	{
		return s_hdgs.length;
	}

	public DataSetDefinition getDataSetDefinition()
	{
		return _dsDef;
	}

	public synchronized boolean next(IMessageHandler msgHandler)
	{
		if (_rowsIter.hasNext())
		{
			_row = _rowsIter.next();
		}
		else
		{
			_row = null;
		}
		return _row != null;
	}

	public synchronized Object get(int columnIndex)
	{
		return _row[columnIndex];
	}

	private ColumnDisplayDefinition[] createColumnDefinitions()
	{
		final int columnCount = getColumnCount();
		ColumnDisplayDefinition[] columnDefs =
			new ColumnDisplayDefinition[columnCount];
		for (int i = 0; i < columnCount; ++i)
		{
			columnDefs[i] = new ColumnDisplayDefinition(200, s_hdgs[i]);
		}
		return columnDefs;
	}

	private void load(DatabaseMetaData md)
	{
		Method[] methods = DatabaseMetaData.class.getMethods();
		for (int i = 0; i < methods.length; ++i)
		{
			final Method method = methods[i];
			if (method.getParameterTypes().length == 0
				&& method.getReturnType() != Void.TYPE
				&& !s_ignoreMethods.containsKey(method.getName()))
			{
				_data.add(generateLine(md, method));
			}
		}

		// Sort the rows by the property name.
//		Collections.sort(_data, new DataSorter());

		_rowsIter = _data.iterator();
	}

	/**
	 * Generate a line for the result of calling the passed method.
	 *
	 * @param   getter	  The "getter" function to retrieve the
	 *					  properties value.
	 *
	 * @return  An <TT>Object[]</CODE> containing the cells for the line in
	 *		  the table. Element zero the first cell etc. Return
	 *		  <CODE>null</CODE> if this property is <B>not</B> to be added
	 *		  to the table.
	 */
	private Object[] generateLine(DatabaseMetaData md, Method getter)
	{
		final Object[] line = new Object[2];
		line[0] = getter.getName();
		if (line[0].equals("getDefaultTransactionIsolation"))
		{
			try
			{
				line[1] = IStrings.UNSUPPORTED;
				final int isol = md.getDefaultTransactionIsolation();
				switch (isol)
				{
					case java.sql.Connection.TRANSACTION_NONE :
						{
							line[1] = "TRANSACTION_NONE";
							break;
						}
					case java.sql.Connection.TRANSACTION_READ_COMMITTED :
						{
							line[1] = "TRANSACTION_READ_COMMITTED";
							break;
						}
					case java.sql.Connection.TRANSACTION_READ_UNCOMMITTED :
						{
							line[1] = "TRANSACTION_READ_UNCOMMITTED";
							break;
						}
					case java.sql.Connection.TRANSACTION_REPEATABLE_READ :
						{
							line[1] = "TRANSACTION_REPEATABLE_READ";
							break;
						}
					case java.sql.Connection.TRANSACTION_SERIALIZABLE :
						{
							line[1] = "TRANSACTION_SERIALIZABLE";
							break;
						}
					default :
						{
							line[1] = "" + isol + "?";
							break;
						}
				}
			}
			catch (SQLException ex)
			{
				_msgHandler.showMessage(ex, null);
			}

		}
		else if (line[0].equals("getClientInfoProperties")) 
		{
			Object obj = executeGetter(md, getter);
			if (obj instanceof ResultSet) {
				ResultSet rs = (ResultSet)obj;
				try {
					StringBuilder tmp = new StringBuilder();
					while (rs.next()) {
						tmp.append(rs.getString(1)).append("\t");
						tmp.append(rs.getInt(2)).append("\t");
						tmp.append(rs.getString(3)).append("\t");
						tmp.append(rs.getString(4)).append("\n");
					}
					line[1] = tmp.toString();
				} catch (SQLException ex) {
					_msgHandler.showMessage(ex, null);
				} finally {
					SQLUtilities.closeResultSet(rs);
				}
			} else {
				line[1] = obj;
			}
		} 
		else
		{
			Object obj = executeGetter(md, getter);
			line[1] = obj;
		}
		return line;
	}

	protected Object executeGetter(Object bean, Method getter)
	{
		try
		{
			return getter.invoke(bean, (Object[])null);
		}
		catch (Throwable th)
		{
			return IStrings.UNSUPPORTED;
		}
	}
}
