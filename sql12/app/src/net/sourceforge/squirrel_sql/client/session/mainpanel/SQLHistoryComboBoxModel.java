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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
/**
 * This class provides a model for a MemoryComboBox object allowing it to
 * save/restore its data from disk.
 *
 * TODO: Check if we have an issue with moving between JDKs (e.g. 1.3 to 1.4)
 * and serialization.May change it to save/retore to XML.
 * 
 * TODO: Don't save each time an element added to model etc, instead save at end.
 * 
 * @author Lynn Pye
 */
public class SQLHistoryComboBoxModel extends DefaultComboBoxModel
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(SQLHistoryComboBoxModel.class);

	/** The file to save/restore data to. */
	private File _file;

	/**
	 * Ctor specifying File to save/restore from.
	 * 
	 * @param	file	File to save/restore from.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>File</TT> passed.
	 */
	public SQLHistoryComboBoxModel(File file)
	{
		super(loadDataFromStorage(file));
		_file = file;
	}

	public void addElement(Object anObject)
	{
		super.addElement(anObject);
		writeDataToStorage();
	}

	public void insertElementAt(Object anObject, int index)
	{
		super.insertElementAt(anObject, index);
		writeDataToStorage();
	}

	public void removeAllElements()
	{
		super.removeAllElements();
		writeDataToStorage();
	}

	public void removeElement(Object anObject)
	{
		super.removeElement(anObject);
		writeDataToStorage();
	}

	public void removeElementAt(int index)
	{
		super.removeElementAt(index);
		writeDataToStorage();
	}

	private synchronized void writeDataToStorage()
	{
		Vector v = new Vector(0);
		int sz = getSize();
		for (int i = 0; i < sz; ++i)
		{
			v.add(getElementAt(i));
		}
		// now write the Vector out...serialization anyone?
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(_file));
			try
			{
				oos.writeObject(v);
				oos.flush();
			}
			finally
			{
				oos.close();
			}
		}
		catch (Exception e)
		{
			s_log.error("Unable to write SQL queries to persistant storage.", e);
		}
	}

	/**
	 * Read Vector from the pased file and return it.
	 * 
	 * @param	file	File containing vector of data.
	 * 
	 * @return	Vector read from passed file.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>File</TT> passed.
	 */
	private static Vector loadDataFromStorage(File file)
	{
		if (file == null)
		{
			throw new IllegalArgumentException("File == null");
		}
		Vector v = new Vector(0);
		try
		{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			try
			{
				v = (Vector)ois.readObject();
			}
			finally
			{
				ois.close();
			}
		}
		catch (Exception e)
		{
			s_log.error("Unable to load SQL queries from persistant storage.", e);
		}
		return v;
	}

}
