package net.sourceforge.squirrel_sql.client.mainframe.action;
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
import java.io.IOException;
import java.net.URL;

import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.FileViewerFactory;
import net.sourceforge.squirrel_sql.client.gui.HtmlViewerSheet;
/**
 * This <CODE>ICommand</CODE> displays the Help window.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ViewFileCommand implements ICommand
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ViewFileCommand.class);

	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(ViewFileCommand.class);

	/** Application API. */
	private IApplication _app;

	private File _file;

	/**
	 * Ctor.
	 *
	 * @param	app		Application API.
	 * @param	file	File to be displayed.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>IApplication</TT>,
	 * 			or <TT>File</TT> passed.
	 */
	public ViewFileCommand(IApplication app, File file)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (file == null)
		{
			throw new IllegalArgumentException("Null File passed");
		}
		_app = app;
		_file = file;
	}

	/**
	 * Display the Dialog
	 */
	public void execute() throws BaseException
	{
		try
		{
			URL url = _file.toURI().toURL();
			FileViewerFactory factory = FileViewerFactory.getInstance();
			HtmlViewerSheet viewer = factory.getViewer(_app.getMainFrame(), url);
			viewer.setVisible(true);
			viewer.toFront();
			viewer.requestFocus();
		}
		catch (IOException ex)
		{
			final String msg = s_stringMgr.getString("ViewFileCommand.error.reading" + _file.getAbsolutePath());
			s_log.error(msg, ex);
			throw new BaseException(ex);
		}
	}
}
