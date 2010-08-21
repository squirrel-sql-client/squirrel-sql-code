package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2001-2004 Colin Bell
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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class ResultSetMetaDataDataSet implements IDataSet
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ResultSetMetaDataDataSet.class);


   private interface i18n
	{
		String UNSUPPORTED = "<Unsupported>";
		// i18n[resultSetMentaDataSet.propName=Property Name]
		String NAME_COLUMN = s_stringMgr.getString("resultSetMentaDataSet.propName");
//		String NULL = "<null>";
		// i18n[resultSetMentaDataSet.val=Value]
		String VALUE_COLUMN = s_stringMgr.getString("resultSetMentaDataSet.val");
	}

	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(ResultSetMetaDataDataSet.class);

	private DataSetDefinition _dsDef;
	private Iterator<Object[]> _rowsIter;
	private Object[] _row;

	/**
	 * Data. Each element is an array of String objects representing a column from
	 * the result set metadata.
	 */
	private List<Object[]> _data = new ArrayList<Object[]>();

	/**
	 * Collection of method names that are considered to the
	 * &quot;properties&quot; of the <TT>ResultSetMetaData</TT> class.
	 */
	private static final Method[] s_methods = createMetaDataMethodArray();

   private static Method[] createMetaDataMethodArray()
   {
      try
      {
         return new Method[]
            {
               ResultSetMetaData.class.getMethod("getColumnName", Integer.TYPE),
               ResultSetMetaData.class.getMethod("getColumnTypeName", Integer.TYPE),
               ResultSetMetaData.class.getMethod("getPrecision", Integer.TYPE),
               ResultSetMetaData.class.getMethod("getScale", Integer.TYPE),
               ResultSetMetaData.class.getMethod("isNullable", Integer.TYPE),
               ResultSetMetaData.class.getMethod("getTableName", Integer.TYPE),
               ResultSetMetaData.class.getMethod("getSchemaName", Integer.TYPE),
               ResultSetMetaData.class.getMethod("getCatalogName", Integer.TYPE),
               ResultSetMetaData.class.getMethod("getColumnClassName", Integer.TYPE),
               ResultSetMetaData.class.getMethod("getColumnDisplaySize", Integer.TYPE),
               ResultSetMetaData.class.getMethod("getColumnLabel", Integer.TYPE),
               ResultSetMetaData.class.getMethod("getColumnType", Integer.TYPE),
               ResultSetMetaData.class.getMethod("isAutoIncrement", Integer.TYPE),
               ResultSetMetaData.class.getMethod("isCaseSensitive", Integer.TYPE),
               ResultSetMetaData.class.getMethod("isCurrency", Integer.TYPE),
               ResultSetMetaData.class.getMethod("isDefinitelyWritable", Integer.TYPE),
               ResultSetMetaData.class.getMethod("isReadOnly", Integer.TYPE),
               ResultSetMetaData.class.getMethod("isSearchable", Integer.TYPE),
               ResultSetMetaData.class.getMethod("isSigned", Integer.TYPE),
               ResultSetMetaData.class.getMethod("isWritable", Integer.TYPE)
            };
      }
      catch (NoSuchMethodException e)
      {
         throw new RuntimeException(e);
      }
   }

   public ResultSetMetaDataDataSet(ResultSet rs)
		throws IllegalArgumentException, DataSetException
	{
		this(getMetaDataFromResultSet(rs));
	}

	public ResultSetMetaDataDataSet(ResultSetMetaData md)
		throws IllegalArgumentException, DataSetException
	{
		super();
		setResultSetMetaData(md);
	}

	public synchronized void setResultSetMetaData(ResultSetMetaData md)
		throws DataSetException
	{
		_dsDef = new DataSetDefinition(createColumnDefinitions());
		load(md);
	}

	public final int getColumnCount()
	{
		return _dsDef.getColumnDefinitions().length;
	}

	public DataSetDefinition getDataSetDefinition()
	{
		return _dsDef;
	}

	public synchronized boolean next(IMessageHandler msgHandler)
		throws DataSetException
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
      ColumnDisplayDefinition[] colDefs = new ColumnDisplayDefinition[s_methods.length + 1];

      colDefs[0] = new ColumnDisplayDefinition(25, "ColumnIndex");

      for (int i = 0; i < s_methods.length; i++)
      {
         colDefs[i+1] = new ColumnDisplayDefinition(25, s_methods[i].getName());
      }

		return colDefs;
	}

	private void load(ResultSetMetaData md) throws DataSetException
	{
		try
		{
         for (int i = 1; i < md.getColumnCount() + 1; ++i)
         {
            Object[] line = new Object[s_methods.length + 1];
            line[0] = i;

            Object[] methodParms = new Object[] { Integer.valueOf(i) };
            for (int j = 0; j < s_methods.length; j++)
            {
               line[j+1] = executeGetter(md, s_methods[j], methodParms);
            }
            _data.add(line);
         }

			_rowsIter = _data.iterator();
		}
		catch (SQLException ex)
		{
			s_log.error("Error occured processing result set", ex);
			throw new DataSetException(ex);
		}
	}

   protected Object executeGetter(Object bean, Method getter, Object[] parms)
	{
		try
		{
			return getter.invoke(bean, parms);
		}
		catch (Throwable th)
		{
			return i18n.UNSUPPORTED;
		}
	}

	private static ResultSetMetaData getMetaDataFromResultSet(ResultSet rs)
		throws IllegalArgumentException, DataSetException
	{
		if (rs == null)
		{
			throw new IllegalArgumentException("Null ResultSet passed");
		}
		try
		{
			return rs.getMetaData();
		}
		catch (SQLException ex)
		{
			throw new DataSetException(ex);
		}
	}
}
