package net.sourceforge.squirrel_sql.client.mainframe.action;
/*
 * Copyright (C) 2001-2003 Colin Bell
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
import java.awt.Frame;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.PluginSummaryDialog;
/**
 * This <CODE>ICommand</CODE> displays the Plugin Summary dialog..
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DisplayPluginSummaryCommand
{
	/** Application API. */
	private IApplication _app;

	/** Owner of the dialog. */
	private Frame _frame;

	/**
	 * Ctor.
	 *
	 * @param	app		Application API.
	 * @param	frame	Owning <TT>Frame</TT>.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>IApplication</TT> passed.
	 */
	public DisplayPluginSummaryCommand(IApplication app, Frame frame)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
		_frame = frame;
	}

	/**
	 * Display the Dialog
	 */
	public void execute()
	{
		try
		{
			new PluginSummaryDialog(_app, _frame).setVisible(true);
		}
		catch (DataSetException ex)
		{
			_app.showErrorDialog(ex);
		}
	}

}
