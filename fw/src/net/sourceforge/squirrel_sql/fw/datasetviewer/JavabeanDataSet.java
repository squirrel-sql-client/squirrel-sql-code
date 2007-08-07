package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2002 Colin Bell
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
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class JavabeanDataSet implements IDataSet
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(JavabeanDataSet.class);

	@SuppressWarnings("unused")
	private ILogger s_log =
		LoggerController.createLogger(JavabeanDataSet.class);

	// i18n[javaBeanDataSet.name=Property Name]
	private static final String _nameColumnName = 
        s_stringMgr.getString("javaBeanDataSet.name");
	// i18n[javaBeanDataSet.value=Value]
	private static final String _valueColumnName = 
        s_stringMgr.getString("javaBeanDataSet.value");

	// TODO: These 2 should be handled with an Iterator!!!
	private int _iCurrent = -1;
	private Object[] _currentRow;

	private List<Object[]> _data;

	private DataSetDefinition _dataSetDefinition;

	public JavabeanDataSet()
	{
		super();
		commonCtor();
	}

	public JavabeanDataSet(Object bean) throws DataSetException
	{
		super();
		setJavabean(bean);
	}

	public void setJavabean(Object bean) throws DataSetException
	{
		commonCtor();
		if (bean != null)
		{
			try
			{
				BeanInfo info = Introspector.getBeanInfo(bean.getClass(),
												Introspector.USE_ALL_BEANINFO);
				processBeanInfo(bean, info);
			}
			catch (Exception ex)
			{
				throw new DataSetException(ex);
			}
		}
	}

	private void processBeanInfo(Object bean, BeanInfo info)
		throws InvocationTargetException, IllegalAccessException
	{
		BeanInfo[] extra = info.getAdditionalBeanInfo();
		if (extra != null)
		{
			for (int i = 0; i < extra.length; ++i)
			{
				processBeanInfo(bean, extra[i]);
			}
		}

		PropertyDescriptor[] propDesc = info.getPropertyDescriptors();
		for (int i = 0; i < propDesc.length; ++i)
		{
			final String propName = propDesc[i].getName();
			final Method getter = propDesc[i].getReadMethod();
			if (propName != null && getter != null)
			{
                String displayName = propDesc[i].getDisplayName();
                if (displayName == null)
                {
                    displayName = propName;
                }
				final Object[] line = generateLine(displayName, bean, getter);
				if (line != null)
				{
					_data.add(line);
				}
			}
		}
	}

	/**
	 * Generate a line for the passed property.
	 *
	 * @param	propTitle	Descriptive name for the property.
	 * @param	bean		Bean containg the property.
	 * @param	getter		The "getter" function to retrieve the
	 *						properties value.
	 *
	 * @return	An <CODE>Object[]</CODE> containing the cells for the line in
	 *			the table. Element zero the first cell etc. Return
	 *			<CODE>null</CODE> if this property is <B>not</B> to be added
	 *			to the table.
	 */
	protected Object[] generateLine(String propTitle, Object bean, Method getter)
		throws InvocationTargetException, IllegalAccessException
	{
		final Object[] line = new Object[2];
		line[0] = propTitle;
		line[1] = executeGetter(bean, getter);
		return line;
	}

	protected Object executeGetter(Object bean, Method getter)
		throws InvocationTargetException, IllegalAccessException
	{
		return getter.invoke(bean, (Object[])null);
	}

	public final int getColumnCount()
	{
		return 2;
	}

	public DataSetDefinition getDataSetDefinition()
	{
		return _dataSetDefinition;
	}

	public synchronized boolean next(IMessageHandler msgHandler)
		throws DataSetException
	{
		// TODO: This should be handled with an Iterator!!!
		if (++_iCurrent < _data.size())
		{
			_currentRow = _data.get(_iCurrent);
			return true;
		}
		return false;
	}

	public synchronized Object get(int columnIndex)
	{
		return _currentRow[columnIndex];
	}

	private ColumnDisplayDefinition[] createColumnDefinitions()
	{
		ColumnDisplayDefinition[] columnDefs = new ColumnDisplayDefinition[2];
		columnDefs[0] = new ColumnDisplayDefinition(50, _nameColumnName);
		columnDefs[1] = new ColumnDisplayDefinition(50, _valueColumnName);
		return columnDefs;
	}

	private void commonCtor()
	{
		_iCurrent = -1;
		_data = new ArrayList<Object[]>();

		ColumnDisplayDefinition[] colDefs = createColumnDefinitions();
		_dataSetDefinition = new DataSetDefinition(colDefs);
	}
}
