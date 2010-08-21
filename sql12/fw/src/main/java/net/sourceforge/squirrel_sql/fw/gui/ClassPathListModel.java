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
import java.io.File;
import java.util.StringTokenizer;
/**
 * This is a <TT>IFileListBoxModel</TT> that loads files from
 * the current class path.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ClassPathListModel extends DefaultFileListBoxModel
{
	/**
	 * Default ctor.
	 */
	public ClassPathListModel()
	{
		super();
		load();
	}

	/**
	 * Build list.
	 */
	private void load()
	{
		removeAllElements();
		String cp = System.getProperty("java.class.path");
		StringTokenizer strtok = new StringTokenizer(cp, File.pathSeparator);
		while (strtok.hasMoreTokens())
		{
			addFile(new File(strtok.nextToken()));
		}
	}
}
