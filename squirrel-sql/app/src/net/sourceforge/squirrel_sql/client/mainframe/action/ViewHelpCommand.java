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
import java.io.File;
import java.io.IOException;

import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.HtmlViewerSheet;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
/**
 * This <CODE>ICommand</CODE> displays the Help window.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ViewHelpCommand implements ICommand
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(ViewHelpCommand.class);

	/** Help window. */
	private static HtmlViewerSheet s_sheet = null;

	/** Listenr for the Help window. */
	private static InternalFrameListener s_lis = new MyInternalFrameListener();

	/** Application API. */
	private IApplication _app;

	/**
	 * Ctor.
	 *
	 * @param	app		Application API.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>IApplication</TT> passed.
	 */
	public ViewHelpCommand(IApplication app)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
	}

	/**
	 * Display the Dialog
	 */
	public void execute()
	{
		try
		{
			synchronized (getClass())
			{
				if (s_sheet == null)
				{
					File file = new ApplicationFiles().getQuickStartGuideFile();
					s_sheet = new HtmlViewerSheet("SQuirreL SQL Client - Help",
													file.toURL());
					s_sheet.addInternalFrameListener(s_lis);
					_app.getMainFrame().addInternalFrame(s_sheet, true, null);
					s_sheet.setSize(600, 400);
					GUIUtils.centerWithinDesktop(s_sheet);
				}
				s_sheet.setVisible(true);
				s_sheet.toFront();
			}
		}
		catch (IOException ex)
		{
			final String msg = "Error occured reading quickstart file";
			s_log.error(msg, ex);
			_app.showErrorDialog(msg, ex);
		}
	}

	private static final class MyInternalFrameListener
		extends InternalFrameAdapter
	{
		/**
		 * Help frame has been closed so allow it to be garbage collected.
		 */
		public void internalFrameClosed(InternalFrameEvent evt)
		{
			super.internalFrameClosed(evt);
			synchronized (ViewHelpCommand.class)
			{
				if (s_sheet != null)
				{
					s_sheet.removeInternalFrameListener(this);
					s_sheet = null;
				}
			}
		}

	}

}
