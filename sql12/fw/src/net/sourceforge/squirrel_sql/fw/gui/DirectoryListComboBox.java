package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import java.io.FilenameFilter;

import javax.swing.JComboBox;
/**
 * This combo box lists the files in a directory.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DirectoryListComboBox extends JComboBox
{
	/**
	 * Default ctor.
	 */
	public DirectoryListComboBox()
	{
		super();
	}

	/**
	 * Load the list with all files in the specified directory.
	 * 
	 * @param	dir		A <TT>File</TT> object specifying the
	 * 					directory to be listed. If <TT>null</TT>
	 * 					the combobox is cleared.
	 */
	public void load(File dir)
	{
		load(dir, null);
	}

	/**
	 * Load the list with all files in the specified directory that
	 * matches the passed filter.
	 * 
	 * @param	dir		A <TT>File</TT> object specifying the
	 * 					directory to be listed. If <TT>null</TT>
	 * 					the combobox is cleared.
	 * @param	filter	Filter specifying the subset of files to load.
	 */
	public void load(File dir, FilenameFilter filter)
	{
		removeAllItems();
		if (dir != null && dir.isDirectory() && dir.canRead())
		{
			String[] files = null;
			if (filter == null)
			{
				files = dir.list();
			}
			else
			{
				files = dir.list(filter);
			}
			for (int i = 0; i < files.length; ++i)
			{
				addItem(files[i]);
			}
		}
	}
}
