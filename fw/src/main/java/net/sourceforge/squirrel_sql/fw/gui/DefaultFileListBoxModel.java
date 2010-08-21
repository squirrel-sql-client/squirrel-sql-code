package net.sourceforge.squirrel_sql.fw.gui;
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
import javax.swing.DefaultListModel;

import java.io.File;

/**
 * This is the default model for the <TT>FileListBox</TT>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DefaultFileListBoxModel extends DefaultListModel
										implements IFileListBoxModel
{
	/**
	 * Default ctor.
	 */
	public DefaultFileListBoxModel()
	{
		super();
	}

	public void addFile(File file)
	{
		addElement(file);
	}

	/**
	 * Return the File at the passed index.
	 * 
	 * @param	idx		Index to return File for.
	 * 
	 * @return	The File at <TT>idx</TT>.
	 * 
	 * @throws	ArrayInexOutOfBoundsException
	 * 			Thrown if <TT>idx</TT> < 0 or >= <TT>getSize()</TT>.
	 */
	public File getFile(int idx)
	{
		return (File)get(idx);
	}
	
	/**
	 * Return array of File names in list.
	 * 
	 * @return	array of File names in list.
	 */
	public String[] getFileNames()
	{
		String[] fileNames = new String[getSize()];
		for (int i = 0, limit = fileNames.length; i < limit; ++i)
		{
			fileNames[i] = getFile(i).getAbsolutePath();
		}
		return fileNames;
	}

	/**
	 * Build list. Empty method.
	 */
//	public void reload()
//	{
//	}

	public void insertFileAt(File file, int idx)
	{
		insertElementAt(file, idx);
	}


	public File removeFile(int idx)
	{
		return (File)remove(idx);
	}

}
