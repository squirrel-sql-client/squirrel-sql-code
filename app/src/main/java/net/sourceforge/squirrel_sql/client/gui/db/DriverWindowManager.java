package net.sourceforge.squirrel_sql.client.gui.db;
/*
 * Copyright (C) 2001-2004 Colin Bell
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
import javax.swing.JInternalFrame;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.WindowManager;
/**
 * This class manages the windows relating to JDBC drivers.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 * @author <A HREF="mailto:jmheight@users.sourceforge.net">Jason Height</A>
 */
public class DriverWindowManager
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(WindowManager.class);

	/** Application API. */
	private final IApplication _app;

	/** Window Factory for driver maintenace windows. */
	private final DriverWindowFactory _driverWinFactory;

	/**
	 * Ctor.
	 *
	 * @param	app		Application API.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>IApplication</TT> passed.
	 */
	public DriverWindowManager(IApplication app)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}

		_app = app;
		_driverWinFactory = new DriverWindowFactory(_app);
	}

	/**
	 * Get a maintenance sheet for the passed driver. If a maintenance sheet
	 * already exists it will be brought to the front. If one doesn't exist
	 * it will be created.
	 *
	 * @param	driver	The driver that user has requested to modify.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISQLDriver</TT> passed.
	 */
	public void showModifyDriverInternalFrame(final ISQLDriver driver)
	{
		if (driver == null)
		{
			throw new IllegalArgumentException("ISQLDriver == null");
		}

      _driverWinFactory.getModifySheet(driver).moveToFront();
	}

	/**
	 * Create and show a new maintenance window to allow the user to create a
	 * new driver.
	 */
	public void showNewDriverInternalFrame()
	{
      _driverWinFactory.getCreateSheet().moveToFront();
	}

	/**
	 * Create and show a new maintenance sheet that will allow the user to
	 * create a new driver that is a copy of the passed one.
	 *
	 * @return	The new maintenance sheet.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISQLDriver</TT> passed.
	 */
	public void showCopyDriverInternalFrame(final ISQLDriver driver)
	{
		if (driver == null)
		{
			throw new IllegalArgumentException("ISQLDriver == null");
		}

      _driverWinFactory.showCopySheet(driver).moveToFront();
   }

	public void moveToFront(final JInternalFrame fr)
	{
		if (fr != null)
		{
			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					GUIUtils.moveToFront(fr);
				}
			});
		}
		else
		{
			s_log.debug("JInternalFrame == null");
		}
	}
}
