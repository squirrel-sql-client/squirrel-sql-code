package net.sourceforge.squirrel_sql.client.session.mainpanel;
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
import javax.swing.DefaultComboBoxModel;
import javax.swing.MutableComboBoxModel;
import javax.swing.event.ListDataListener;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.util.ArrayList;

/**
 * TODO: JavaDoc
 * 
 * @author Colin Bell
 */
public class SQLHistoryComboBoxModel extends DefaultComboBoxModel
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(SQLHistoryComboBoxModel.class);

	/** Shared data model. */
	private static MutableComboBoxModel s_sharedDataModel;

	/** Actual data model. */
	private MutableComboBoxModel _dataModel;

	/** The currently selected model. */
	private Object _selectedObject;

	public SQLHistoryComboBoxModel(boolean useSharedModel)
	{
		super();
		if (useSharedModel && s_sharedDataModel == null)
		{
			throw new IllegalStateException("Shared instance has not been initialized");
		}
		_dataModel = useSharedModel ? s_sharedDataModel : new DefaultComboBoxModel();
	}

	public synchronized static void initializeSharedInstance(Object[] data)
	{
		if (s_sharedDataModel != null)
		{
			s_log.error("Shared data model has already been initialized");
		}
		else
		{
			s_sharedDataModel = new DefaultComboBoxModel(data);
		}
	}

	/**
	 * Is this model using the shared data model?
	 * 
	 * @return	<TT>true</TT> if this model is using the shared data model.
	 */
	public boolean isUsingSharedDataModel()
	{
		return _dataModel == s_sharedDataModel;
	}

	/**
	 * Specify whether this model is usning the shared data model.
	 * 
	 * @param use	<TT>true</TT> use the shared model.
	 */
	public synchronized void setUseSharedModel(boolean use)
	{
		if (isUsingSharedDataModel() != use)
		{
			_dataModel = use ? s_sharedDataModel : duplicateSharedDataModel();
		}
	}

	/**
	 * Add an element to this model.
	 * 
	 * This method is passed onto the data model that this data model is
	 * wrapped around.
	 * 
	 * @param	object	The object to be added.
	 */
	public void addElement(Object object)
	{
		_dataModel.addElement(object);
	}

	/**
	 * Add an item at a specified index.
	 * 
	 * This method is passed onto the data model that this data model is
	 * wrapped around.
	 * 
	 * @param	object	The object to be added.
	 * @param	index	The index to add it at.
	 */
	public void insertElementAt(Object object, int index)
	{
		_dataModel.insertElementAt(object, index);
	}

	/**
	 * Remove the passed object from this collection.
	 * 
	 * This method is passed onto the data model that this data model is
	 * wrapped around.
	 * 
	 * @param	object	The object to be removed.
	 */
	public void removeElement(Object object)
	{
		_dataModel.removeElement(object);
	}

	/**
	 * Remove the element from this collection at the passed index.
	 * 
	 * This method is passed onto the data model that this data model is
	 * wrapped around.
	 * 
	 * @param	index	The index to remove an element from.
	 */
	public void removeElementAt(int index)
	{
		_dataModel.removeElementAt(index);
	}

	/**
	 * Retrieve the element currently selected. This is <EM>not</EM> passed
	 * on to the wrapped model as this model is responsible for keeping track
	 * of the currently selected item.
	 * 
	 * @return	The object currently selected.
	 */
	public Object getSelectedItem()
	{
		return _selectedObject;
	}

	/**
	 * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
	 */
	public void setSelectedItem(Object object)
	{
		_selectedObject = object;
		fireContentsChanged(this, -1, -1);
	}

	/**
	 * This method is passed onto the data model that this data model is
	 * wrapped around.
	 */
	public void addListDataListener(ListDataListener arg0)
	{
		_dataModel.addListDataListener(arg0);
	}

	/**
	 * This method is passed onto the data model that this data model is
	 * wrapped around.
	 */
	public Object getElementAt(int arg0)
	{
		return _dataModel.getElementAt(arg0);
	}

	/**
	 * Retrieve the number of elements in this model.
	 * 
	 * This method is passed onto the data model that this data model is
	 * wrapped around.
	 * 
	 * @return	Number of elements in this model.
	 */
	public int getSize()
	{
		return _dataModel.getSize();
	}

	/**
	 * This method is passed onto the data model that this data model is
	 * wrapped around.
	 */
	public void removeListDataListener(ListDataListener arg0)
	{
		_dataModel.removeListDataListener(arg0);
	}

	protected synchronized MutableComboBoxModel duplicateSharedDataModel()
	{
		MutableComboBoxModel newModel = new DefaultComboBoxModel();
		for (int i = 0, limit = s_sharedDataModel.getSize(); i < limit; ++i)
		{
			SQLHistoryItem obj = (SQLHistoryItem)s_sharedDataModel.getElementAt(i);
			newModel.addElement(obj.clone());
		} 
		return newModel;
	}

   public ArrayList<SQLHistoryItem> getItems()
   {
      ArrayList<SQLHistoryItem> ret = new ArrayList<SQLHistoryItem>();

      for (int i = 0; i < _dataModel.getSize(); i++)
      {
         ret.add((SQLHistoryItem) _dataModel.getElementAt(i));
      }

      return ret;


   }
}
