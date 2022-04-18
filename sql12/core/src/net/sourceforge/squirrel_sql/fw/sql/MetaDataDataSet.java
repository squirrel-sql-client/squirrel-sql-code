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

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.NullMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Method;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MetaDataDataSet implements IDataSet
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(MetaDataDataSet.class);

	private final static Map<String, Object> s_ignoreMethods = new HashMap<>();

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
	private List<Object[]> _data = new ArrayList<>();

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

		// To make sure these items are on top of the Metadata list.
		Object[][] onTopLines = new Object[][]
				{
						new Object[]{"getURL", null},
						new Object[]{"getDriverName", null},
						new Object[]{"getDatabaseProductName", null},
						new Object[]{"getDatabaseProductVersion", null},
						new Object[]{"getDriverVersion", null},
						new Object[]{"getUserName", null},
						new Object[]{"getDefaultTransactionIsolation", null}
				};

		for (int i = 0; i < methods.length; ++i)
		{
			final Method method = methods[i];
			if (method.getParameterTypes().length == 0
				&& method.getReturnType() != Void.TYPE
				&& !s_ignoreMethods.containsKey(method.getName()))
			{
				Object[] generatedLine = generateLine(md, method);

				Object[] onTopLine = getMatchingOnTopLine(onTopLines, method.getName());
				if (null != onTopLine)
				{
					onTopLine[1] = generatedLine[1];
				}
				else
				{
					_data.add(generatedLine);
				}
			}
		}

		ArrayUtils.reverse(onTopLines);
		for (Object[] onTopLine : onTopLines)
		{
			_data.add(0, onTopLine);
		}

		_rowsIter = _data.iterator();
	}

	private Object[] getMatchingOnTopLine(Object[][] onTopLines, String methodName)
	{
		for (Object[] onTopLine : onTopLines)
		{
			if(onTopLine[0].equals(methodName))
			{
				return onTopLine;
			}
		}

		return null;
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
			_readClientInfoPropertiesFailSave(md, getter, line);
		} 
		else
		{
			Object obj = executeGetter(md, getter);
			line[1] = obj;
		}
		return line;
	}

	private void _readClientInfoPropertiesFailSave(DatabaseMetaData md, Method getter, Object[] line)
	{
		try
		{
			Object obj = executeGetter(md, getter);
			if (obj instanceof ResultSet)
			{
				try(ResultSet rs = (ResultSet) obj)
				{
					int clientInfoColumnCount = 4;
					if (rs.getMetaData() != null)
					{
						clientInfoColumnCount = rs.getMetaData().getColumnCount();
					}

					StringBuilder tmp = new StringBuilder();
					while (rs.next())
					{
						tmp.append(rs.getString(1));

						if (clientInfoColumnCount > 1)
						{
							if (rs.getMetaData()==null || rs.getMetaData().getColumnType(2) == Types.INTEGER)
							{
								tmp.append("\t").append(rs.getInt(2));
							}
							else
							{
								// To cope with the issue discussed in bug #1387
								tmp.append("\t").append(rs.getString(2));
							}
						}

						if (clientInfoColumnCount > 2)
						{
							tmp.append("\t").append(rs.getString(3));
						}

						if (clientInfoColumnCount > 3)
						{
							tmp.append("\t").append(rs.getString(4));
						}

						tmp.append("\n");
					}
					line[1] = tmp.toString();
				}
			}
			else
			{
				line[1] = obj;
			}
		}
		catch (Throwable ex)
		{
			_msgHandler.showMessage(ex, null);
			line[1] = ex.toString();
		}
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
