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
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.DataCache;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;
/**
 * Factory to handle creation of maintenance sheets for SQL Driver objects.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DriverWindowFactory implements AliasInternalFrame.IMaintenanceType
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(DriverWindowFactory.class);

	/** Application API. */
	private IApplication _app;

	/**
	 * Collection of <TT>DriverMaintDialog</TT> that are currently visible modifying
	 * an existing driver. Keyed by <TT>ISQLDriver.getIdentifier()</TT>.
	 */
	private final Map _modifySheets = new HashMap();

	/**
	 * ctor.
	 */
	public DriverWindowFactory(IApplication app)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}

		_app = app;
	}

	/**
	 * Get a maintenance sheet for the passed driver. If a maintenance sheet already
	 * exists it will be brought to the front. If one doesn't exist it will be
	 * created.
	 *
	 * @param	driver	The driver that user has requested to modify.
	 *
	 * @return	The maintenance sheet for the passed driver.
	 *
	 * @throws	IllegalArgumentException	if a <TT>null</TT> <TT>ISQLDriver</TT> passed.
	 */
	public synchronized DriverInternalFrame showModifySheet(ISQLDriver driver)
	{
		if (driver == null)
		{
			throw new IllegalArgumentException("ISQLDriver == null");
		}

		DriverInternalFrame sheet = get(driver);
		if (sheet == null)
		{
			sheet = new DriverInternalFrame(_app, driver, MODIFY);
			_modifySheets.put(driver.getIdentifier(), sheet);
			_app.getMainFrame().addInternalFrame(sheet, true, null);

			sheet.addInternalFrameListener(new InternalFrameAdapter()
			{
				public void internalFrameClosed(InternalFrameEvent evt)
				{
					synchronized (DriverWindowFactory.this)
					{
						DriverInternalFrame frame = (DriverInternalFrame) evt.getInternalFrame();
						_modifySheets.remove(frame.getSQLDriver().getIdentifier());
					}
				}
			});
			GUIUtils.centerWithinDesktop(sheet);
		}

		return sheet;
	}

	/**
	 * Create and show a new maintenance sheet to allow the user to create a new driver.
	 *
	 * @return	The new maintenance sheet.
	 */
	public DriverInternalFrame showCreateSheet()
	{
		final DataCache cache = _app.getDataCache();
		final IIdentifierFactory factory = IdentifierFactory.getInstance();
		final ISQLDriver driver = cache.createDriver(factory.createIdentifier());
		final DriverInternalFrame sheet = new DriverInternalFrame(_app, driver, NEW);
		_app.getMainFrame().addInternalFrame(sheet, true, null);
		GUIUtils.centerWithinDesktop(sheet);
		return sheet;
	}

	/**
	 * Create and show a new maintenance sheet that will allow the user to create a
	 * new driver that is a copy of the passed one.
	 *
	 * @return	The new maintenance sheet.
	 *
	 * @throws	IllegalArgumentException	if a <TT>null</TT> <TT>ISQLDriver</TT> passed.
	 */
	public DriverInternalFrame showCopySheet(ISQLDriver driver)
	{
		if (driver == null)
		{
			throw new IllegalArgumentException("ISQLDriver == null");
		}

		final DataCache cache = _app.getDataCache();
		final IIdentifierFactory factory = IdentifierFactory.getInstance();
		ISQLDriver newDriver = cache.createDriver(factory.createIdentifier());
		try
		{
			newDriver.assignFrom(driver);
		}
		catch (ValidationException ex)
		{
			s_log.error("Error occured copying the driver", ex);
		}
		final DriverInternalFrame sheet =
			new DriverInternalFrame(_app, newDriver, COPY);
		_app.getMainFrame().addInternalFrame(sheet, true, null);
		GUIUtils.centerWithinDesktop(sheet);

		return sheet;
	}

	private DriverInternalFrame get(ISQLDriver driver)
	{
		return (DriverInternalFrame) _modifySheets.get(driver.getIdentifier());
	}
}

