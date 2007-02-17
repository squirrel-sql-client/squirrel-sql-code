package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2003 Colin Bell
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
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class JavabeanArrayDataSet implements IDataSet
{
	private final ILogger s_log =
		LoggerController.createLogger(JavabeanArrayDataSet.class);

	private Object[] _currentRow;

	private List _data;
	private Iterator _dataIter;

	private int _columnCount;
	private DataSetDefinition _dataSetDefinition;

	/**
	 * @throws	IllegalArgumentException
	 * 			Thrrown if all objects in <TT>beans</TT> are not the same class.
	 */
	public JavabeanArrayDataSet(Object[] beans) throws DataSetException
	{
		super();
		setJavabeanArray(beans);
	}

	/**
	 * Retrieve the number of columns in this <TT>DataSet</TT>.
	 * 
	 * @return	Number of columns.
	 */
	public final int getColumnCount()
	{
		return _columnCount;
	}

	public DataSetDefinition getDataSetDefinition()
	{
		return _dataSetDefinition;
	}

	public synchronized boolean next(IMessageHandler msgHandler)
		throws DataSetException
	{
		if (_dataIter.hasNext())
		{
			_currentRow = (Object[])_dataIter.next();
			return true;
		}
		return false;
	}

	public Object get(int columnIndex)
	{
		return _currentRow[columnIndex];
	}

	protected Object executeGetter(Object bean, Method getter)
		throws InvocationTargetException, IllegalAccessException
	{
		return getter.invoke(bean, (Object[])null);
	}

	/**
	 * @throws	IllegalArgumentException
	 * 			Thrrown if all objects in <TT>beans</TT> are not the same class.
	 */
	private void setJavabeanArray(Object[] beans) throws DataSetException
	{
		if (beans == null)
		{
			beans = new Object[0];
		}

		if (beans.length > 0)
		{
			BeanInfo info = null;
			try
			{
				info = Introspector.getBeanInfo(beans[0].getClass(), Introspector.USE_ALL_BEANINFO);
				validateBeans(beans);
				initialize(info);
			}
			catch (IntrospectionException ex)
			{
				throw new DataSetException(ex);
			}

			try
			{
				for (int i = 0; i < beans.length; ++i)
				{
					processBeanInfo(beans[i], info);
				}
			}
			catch (Exception ex)
			{
				throw new DataSetException(ex);
			}
			_dataIter = _data.iterator();
		}
	}

	private void processBeanInfo(Object bean, BeanInfo info)
		throws InvocationTargetException, IllegalAccessException
	{
//		BeanInfo[] extra = info.getAdditionalBeanInfo();
//		if (extra != null)
//		{
//			for (int i = 0; i < extra.length; ++i)
//			{
//				processBeanInfo(bean, extra[i]);
//			}
//		}

		PropertyDescriptor[] propDesc = info.getPropertyDescriptors();
		Object[] line = new Object[propDesc.length];
		for (int i = 0; i < propDesc.length; ++i)
		{
			final Method getter = propDesc[i].getReadMethod();
			if (getter != null)
			{
				line[i] = executeGetter(bean, getter);
			}
		}

		if (line != null)
		{
			_data.add(line);
		}
	}

	private void validateBeans(Object[] beans)
		throws IllegalArgumentException
	{
		if (beans.length > 0)
		{
			final Class clazz = beans[0].getClass();
			for (int i = 1; i < beans.length; ++i)
			{
				if (beans[i].getClass() != clazz)
				{
					throw new IllegalArgumentException("All beans must be the same Class");
				}
			}
		}
		
	}

	private void initialize(BeanInfo info)
	{
		_data = new ArrayList();
		ColumnDisplayDefinition[] colDefs = createColumnDefinitions(info, null);
		_dataSetDefinition = new DataSetDefinition(colDefs);
		_columnCount = _dataSetDefinition.getColumnDefinitions().length;
	}

	private ColumnDisplayDefinition[] createColumnDefinitions(BeanInfo info,
 																int[] columnWidths)
	{
		PropertyDescriptor[] propDesc = info.getPropertyDescriptors(); 
		ColumnDisplayDefinition[] columnDefs = new ColumnDisplayDefinition[propDesc.length];
		for (int i = 0; i < propDesc.length; ++i)
		{
			int colWidth = columnWidths != null && columnWidths.length >= i ? columnWidths[i] : 200;
			columnDefs[i] = new ColumnDisplayDefinition(colWidth, propDesc[i].getDisplayName());
		}
		return columnDefs;
	}
}
