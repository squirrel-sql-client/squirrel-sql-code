package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2002-2003 Colin Bell
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
import javax.swing.JList;

import java.io.File;
/**
 * This listbox displays all entries in the current ClassPath.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class FileListBox extends JList
{
	/**
	 * Default ctor. Creates listbox with a <TT>DefaultClassPathListBoxModel</TT>
	 * model.
	 */
	public FileListBox()
	{
		this(new ClassPathListModel());
	}

	/**
	 * Ctor specifying the model.
	 */
	public FileListBox(IFileListBoxModel model)
	{
		super(model);
	}

	/**

	 * Returns the <TT>File</TT> object representing the first selected
	 * value or <TT>null</TT> if there is no selection.
	 *
	 * @return		the <TT>File</TT> object representing the first selected
	 *				value or <TT>null</TT> if there is no selection.
	 */
	public File getSelectedFile()
	{
		return (File)getSelectedValue();
	}

	public IFileListBoxModel getTypedModel()
	{
		return (IFileListBoxModel)getModel();
	} 
}
