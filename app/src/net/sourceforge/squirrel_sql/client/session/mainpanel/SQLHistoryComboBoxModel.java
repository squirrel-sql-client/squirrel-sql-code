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

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
/**
 * This class provides a model for a MemoryComboBox object allowing it to
 * save/restore its data from disk.
 * TODO: Delete this class
 * 
 * @author Lynn Pye
 */
public class SQLHistoryComboBoxModel extends DefaultComboBoxModel
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(SQLHistoryComboBoxModel.class);

	/** The file to save/restore data to. */
//	private File _file;

	public SQLHistoryComboBoxModel()
	{
		super();
	}

	public SQLHistoryComboBoxModel(Object[] data)
	{
		super(data);
	}

	/**
	 * Ctor specifying File to save/restore from.
	 * 
	 * @param	file	File to save/restore from.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>File</TT> passed.
	 */
//	public SQLHistoryComboBoxModel(File file)
//	{
//		super(loadDataFromStorage(file));
//		_file = file;
//	}

//	public void addElement(Object anObject)
//	{
//		super.addElement(anObject);
//		writeDataToStorage();
//	}
//
//	public void insertElementAt(Object anObject, int index)
//	{
//		super.insertElementAt(anObject, index);
//		writeDataToStorage();
//	}
//
//	public void removeAllElements()
//	{
//		super.removeAllElements();
//		writeDataToStorage();
//	}
//
//	public void removeElement(Object anObject)
//	{
//		super.removeElement(anObject);
//		writeDataToStorage();
//	}
//
//	public void removeElementAt(int index)
//	{
//		super.removeElementAt(index);
//		writeDataToStorage();
//	}

//	private synchronized void writeDataToStorage()
//	{
//		// Get the history into an array.
//		final int historySize = getSize();
//		final List history = new ArrayList(historySize);
//		for (int i = 0; i < historySize; ++i)
//		{
//			history.add(getElementAt(i));
//		}
//		SQLHistoryComboBoxItem[] data = new SQLHistoryComboBoxItem[historySize];
//		data = (SQLHistoryComboBoxItem[])history.toArray(data);
//
//		// Wrap a JavaBean around the array and save it.
//		final SQLHistoryArrayBean bean = new SQLHistoryArrayBean();
//		bean.setData(data);
//		try
//		{
//			XMLBeanWriter wtr = new XMLBeanWriter(bean);
//			wtr.save(_file);
//		}
//		catch (Exception ex)
//		{
//			s_log.error("Unable to write SQL queries to persistant storage.", ex);
//		}
//	}

	/**
	 * Load SQL history from the passed file and return it in a Vector.
	 * 
	 * @param	file	File containing SQL history.
	 * 
	 * @return		Vector containing SQL history. 
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>File</TT> passed.
	 */
//	private static Vector loadDataFromStorage(File file)
//	{
//		if (file == null)
//		{
//			throw new IllegalArgumentException("File == null");
//		}
//
//		try
//		{
//			XMLBeanReader doc = new XMLBeanReader();
//			doc.load(file);
//			Iterator it = doc.iterator();
//			if (it.hasNext())
//			{
//				SQLHistoryArrayBean bean = (SQLHistoryArrayBean)it.next();
//				SQLHistoryComboBoxItem[] data = bean.getData();
//				return new Vector(Arrays.asList(data));
//
//			}
//		}
//		catch (FileNotFoundException ignore)
//		{
//			// History file not found for user - first time user ran pgm.
//		}
//		catch (Exception ex)
//		{
//			s_log.error("Unable to load SQL queries from persistant storage.", ex);
//		}
//
//		return new Vector(0);
//	}

//	public static class SQLHistoryArrayBean
//	{
//		private SQLHistoryComboBoxItem[] _data = new SQLHistoryComboBoxItem[0];
//
//		public SQLHistoryArrayBean()
//		{
//		}
//
//		public SQLHistoryComboBoxItem[] getData()
//		{
//			return _data;
//		}
//
//		public void setData(SQLHistoryComboBoxItem[] data)
//		{
//			_data = data;
//		}
//	}
}
