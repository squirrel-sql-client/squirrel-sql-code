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
import javax.swing.ListModel;

import java.io.File;

public interface IFileListBoxModel extends ListModel
{
	//void reload();
	void addFile(File file);

	File removeFile(int idx);

	void insertFileAt(File file, int idx);

	/**
	 * Return array of File names in list.
	 * 
	 * @return	array of File names in list.
	 */
	String[] getFileNames();

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
	File getFile(int idx);
}
