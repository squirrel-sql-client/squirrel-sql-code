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

import javax.swing.Action;

import net.sourceforge.squirrel_sql.fw.util.BaseException;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;

/**
 * This <CODE>Action</CODE> displays a licence file.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ViewLicenceAction extends SquirrelAction
{
	private File _file;

	/**
	 * Ctor.
	 * 
	 * @param	app		Application API.
	 * @param	file	Licence file.
	 */
	public ViewLicenceAction(IApplication app, File file)
	{
		super(app);
		_file = file;
		app.getResources().setupAction(this);
	}

	/**
	 * Display the licence window.
	 */
	public void actionPerformed(ActionEvent evt)
	{
		try
		{
			new ViewFileCommand(getApplication(), _file).execute();
		}
		catch (BaseException ex)
		{
			getApplication().showErrorDialog("Error viewing licence file", ex);
		}
	}
}
