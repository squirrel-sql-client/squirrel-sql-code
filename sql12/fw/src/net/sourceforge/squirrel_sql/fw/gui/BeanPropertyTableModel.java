package net.sourceforge.squirrel_sql.fw.gui;
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
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class BeanPropertyTableModel extends DefaultTableModel
{
    private static final long serialVersionUID = 1L;

    /** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(BeanPropertyTableModel.class);

	private Object _bean;

	private String _nameColumnName = s_stringMgr.getString("BeanPropertyTableModel.namecolumn");
	private String _valueColumnName = s_stringMgr.getString("BeanPropertyTableModel.valuecolumn");

	public BeanPropertyTableModel()
	{
		super();
	}

	public void setBean(Object bean) throws BaseException
	{
		_bean = bean;
		refresh();
	}

	public void refresh() throws BaseException
	{
		final Vector<Object> columnNames = new Vector<Object>();
		columnNames.add(_nameColumnName);
		columnNames.add(_valueColumnName);
		final Vector<Object> columnData = new Vector<Object>();
		if (_bean != null)
		{
			try
			{
				BeanInfo info = Introspector.getBeanInfo(_bean.getClass(), Introspector.USE_ALL_BEANINFO);
				processBeanInfo(info, columnData);
			}
			catch (Exception ex)
			{
				throw new BaseException(ex);
			}
		}

		// Sort the rows by the property name.
		Collections.sort(columnData, new DataSorter());

		setDataVector(columnData, columnNames);
	}

	private void processBeanInfo(BeanInfo info, Vector<Object> columnData)
		throws InvocationTargetException, IllegalAccessException
	{
		BeanInfo[] extra = info.getAdditionalBeanInfo();
		if (extra != null)
		{
			for (int i = 0; i < extra.length; ++i)
			{
				processBeanInfo(extra[i], columnData);
			}
		}

		PropertyDescriptor[] propDesc = info.getPropertyDescriptors();
		for (int i = 0; i < propDesc.length; ++i)
		{
			final String propName = propDesc[i].getName();
			final Method getter = propDesc[i].getReadMethod();
			if (propName != null && getter != null)
			{
				Vector<Object> line = generateLine(propName, _bean, getter);
				if (line != null)
				{
					columnData.add(line);
				}
			}
		}
	}

	/**
	 * Generate a line for the passed property.
	 *
	 * @param	propName	Name of the property.
	 * @param	bean		Bean containg the property.
	 * @param	getter		The "getter" function to retrieve the
	 *						properties value.
	 *
	 * @return	A <CODE>Vector</CODE> containing the cells for the line in
	 *			the table. Element zero the first cell etc. Return
	 *			<CODE>null</CODE> if this property is <B>not</B> to be added
	 *			to the table.
	 */
	protected Vector<Object> generateLine(String propName, Object bean, Method getter)
		throws InvocationTargetException, IllegalAccessException
	{
		final Vector<Object> line = new Vector<Object>();
		line.add(propName);
		line.add(executeGetter(bean, getter));
		return line;
	}

	protected Object executeGetter(Object bean, Method getter)
		throws InvocationTargetException, IllegalAccessException
	{
		return getter.invoke(bean, (Object[])null);
	}

	public void setNameColumnName(String value)
	{
		_nameColumnName = value;
	}

	public void setValueColumnName(String value)
	{
		_valueColumnName = value;
	}

	/**
	 * This comparator is compatible with the strange use of lists in this 
	 * class.  This classes lists are Vectors with Strings as the first element
	 * and any object as the other objects.
	 */
	private static final class DataSorter implements Comparator<Object>
	{
		public int compare(Object o1, Object o2)
		{
		    Vector<Object> v1 = (Vector<Object>)o1;
		    Vector<Object> v2 = (Vector<Object>)o2;
		    String lhs = (String)v1.get(0);
		    String rhs = (String)v2.get(0);
			return lhs.compareToIgnoreCase(rhs);
		}
	}
}
