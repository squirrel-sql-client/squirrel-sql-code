package net.sourceforge.squirrel_sql.client.mainframe.action;
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
import java.awt.event.ActionEvent;
import java.io.File;

import net.sourceforge.squirrel_sql.fw.util.BaseException;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;

/**
 * This <CODE>Action</CODE> displays the Squirrel Help Window.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ViewHelpAction extends SquirrelAction
{
	private File _file;

	/**
	 * Ctor.
	 * 
	 * @param	app	Application API.
	 */
	public ViewHelpAction(IApplication app)
	{
		this(app, null);
	}

	/**
	 * Ctor.
	 * 
	 * @param	app		Application API.
	 * @param	file	Help file.
	 */
	public ViewHelpAction(IApplication app, File file)
	{
		super(app);
		_file = file;
		if (_file == null)
		{
			_file = new ApplicationFiles().getQuickStartGuideFile();
		}
	}

	/**
	 * Display the help window.
	 */
	public void actionPerformed(ActionEvent evt)
	{
		try
		{
			new ViewFileCommand(getApplication(), _file).execute();
		}
		catch (BaseException ex)
		{
			getApplication().showErrorDialog("Error viewing help file", ex);
		}
	}
}
