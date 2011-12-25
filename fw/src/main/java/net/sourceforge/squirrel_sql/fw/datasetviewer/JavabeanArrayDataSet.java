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
import java.util.*;

import net.sourceforge.squirrel_sql.fw.util.EmptyIterator;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

public class JavabeanArrayDataSet implements IDataSet
{
	private Object[] _currentRow;

	private List<Object[]> _data;
	private Iterator<Object[]> _dataIter = new EmptyIterator<Object[]>();

   private DataSetDefinition _dataSetDefinition;
   private BeanInfo _info;
   private Class _beanClass;
   private BeanPorpertyColumnDisplayDefinition[] _beanPorpertyColumnDisplayDefinitions;

   private HashMap<String, String> _headers = new HashMap<String, String>();
   private HashMap<String, Integer> _positions = new HashMap<String, Integer>();
   private HashMap<String, Integer> _absoluteWidths = new HashMap<String, Integer>();
   private HashSet<String> _ignoreProperties = new HashSet<String>();


   /**
	 * @throws	IllegalArgumentException
	 * 			Thrrown if all objects in <TT>beans</TT> are not the same class.
	 */
	public JavabeanArrayDataSet(Object[] beans) throws DataSetException
	{
      if (null == beans || 0 == beans.length)
      {
         _beanClass = Object.class;
      }
      else
      {
         _beanClass = beans[0].getClass();
      }
      setJavaBeanArray(beans);
	}

   public JavabeanArrayDataSet(Class beanClass) throws DataSetException
   {
      _beanClass = beanClass;
   }

   /**
	 * Retrieve the number of columns in this <TT>DataSet</TT>.
	 * 
	 * @return	Number of columns.
	 */
	public final int getColumnCount()
	{
		return _beanPorpertyColumnDisplayDefinitions.length;
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
			_currentRow = _dataIter.next();
			return true;
		}
		return false;
	}

	public synchronized Object get(int columnIndex)
	{
		return _currentRow[columnIndex];
	}

	protected Object executeGetter(Object bean, Method getter)
		throws InvocationTargetException, IllegalAccessException
	{
		return getter.invoke(bean, (Object[])null);
	}

   public void setJavaBeanList(List list) throws DataSetException
   {
      setJavaBeanArray(list.toArray(new Object[list.size()]));
   }

	public void setJavaBeanArray(Object[] beans) throws DataSetException
	{
      initColsAndBeanInfo(_beanClass);

		if (beans.length > 0)
		{
			try
			{
				for (int i = 0; i < beans.length; ++i)
				{
					processBeanInfo(beans[i], _info);
				}
			}
			catch (Exception ex)
			{
				throw new DataSetException(ex);
			}
			_dataIter = _data.iterator();
		}
	}

   void initColsAndBeanInfo(Class beanClass) throws DataSetException
   {
      try
      {
         _info = Introspector.getBeanInfo(beanClass, Introspector.USE_ALL_BEANINFO);
         initializeCols(_info);
      }
      catch (IntrospectionException ex)
      {
         throw new DataSetException(ex);
      }
   }

   private void processBeanInfo(Object bean, BeanInfo info)
		throws InvocationTargetException, IllegalAccessException
	{
		ArrayList line = new ArrayList();
		for (int i = 0; i < _beanPorpertyColumnDisplayDefinitions.length; ++i)
		{
			final Method getter = _beanPorpertyColumnDisplayDefinitions[i].getPropDesc().getReadMethod();
			if (getter != null)
			{
				line.add(executeGetter(bean, getter));
			}
		}

   	_data.add(line.toArray(new Object[line.size()]));
	}

   private void initializeCols(BeanInfo info)
	{
		_data = new ArrayList<Object[]>();
      _beanPorpertyColumnDisplayDefinitions = createColumnDefinitions(info);
		_dataSetDefinition = new DataSetDefinition(BeanPorpertyColumnDisplayDefinition.getColDefs(_beanPorpertyColumnDisplayDefinitions));
	}

	private BeanPorpertyColumnDisplayDefinition[] createColumnDefinitions(BeanInfo info)
	{
		PropertyDescriptor[] propDesc = info.getPropertyDescriptors();
		ArrayList<BeanPorpertyColumnDisplayDefinition> columnDefs = new ArrayList<BeanPorpertyColumnDisplayDefinition>();
		for (int i = 0; i < propDesc.length; ++i)
		{
         if(false  == isValidProperty(propDesc[i]))
         {
            continue;
         }

         ColumnDisplayDefinition colDef;



         if (null == _headers.get(propDesc[i].getName()))
         {
            colDef = new ColumnDisplayDefinition(200, propDesc[i].getDisplayName());
         }
         else
         {
            colDef = new ColumnDisplayDefinition(200, _headers.get(propDesc[i].getName()));
         }
         colDef.setUserProperty(propDesc[i].getName());

         if(null != _absoluteWidths.get(propDesc[i].getName()))
         {
            colDef.setAsoluteWidth(_absoluteWidths.get(propDesc[i].getName()));
         }


         columnDefs.add(new BeanPorpertyColumnDisplayDefinition(colDef, propDesc[i]));
		}

      if (0 < _positions.size())
      {
         Collections.sort(columnDefs, new Comparator<BeanPorpertyColumnDisplayDefinition>()
         {
            @Override
            public int compare(BeanPorpertyColumnDisplayDefinition o1, BeanPorpertyColumnDisplayDefinition o2)
            {
               return comparePosition(o1, o2);
            }
         });
      }


      return columnDefs.toArray(new BeanPorpertyColumnDisplayDefinition[columnDefs.size()]);
	}

   private int comparePosition(BeanPorpertyColumnDisplayDefinition o1, BeanPorpertyColumnDisplayDefinition o2)
   {
      if(null == _positions.get(o1.getColDef().getUserProperty()) && null == _positions.get(o2.getColDef().getUserProperty()))
      {
         return 0;
      }
      else if(null == _positions.get(o1.getColDef().getUserProperty()) && null != _positions.get(o2.getColDef().getUserProperty()))
      {
         return 1;
      }
      else if(null != _positions.get(o1.getColDef().getUserProperty()) && null == _positions.get(o2.getColDef().getUserProperty()))
      {
         return -1;
      }
      else
      {
         return _positions.get(o1.getColDef().getUserProperty()).compareTo(_positions.get(o2.getColDef().getUserProperty()));
      }
   }

   private boolean isValidProperty(PropertyDescriptor propertyDescriptor)
   {
      return   false == propertyDescriptor.getReadMethod().getDeclaringClass().equals(Object.class)
            && false == _ignoreProperties.contains(propertyDescriptor.getName());
   }

   public void setColHeader(String prop, String header)
   {
      _headers.put(prop, header);
   }

   public void setColPos(String prop, int pos)
   {
      _positions.put(prop, pos);
   }

   public void setAbsoluteWidht(String prop, int width)
   {
      _absoluteWidths.put(prop, width);
   }

   public void setIgnoreProperty(String ignoreProperty)
   {
      _ignoreProperties.add(ignoreProperty);
   }
}
