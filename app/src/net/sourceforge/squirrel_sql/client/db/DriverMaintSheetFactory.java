package net.sourceforge.squirrel_sql.client.db;
/*
 * Copyright (C) 2001 Colin Bell
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

import javax.swing.event.InternalFrameListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;

/**
 * Factory to handle creation of maintenance sheets for SQL Driver objects.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DriverMaintSheetFactory implements DriverMaintSheet.MaintenanceType {
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(DriverMaintSheetFactory.class);

	/** Application API. */
	private IApplication _app;

	/**
	 * Collection of <TT>DriverMaintDialog</TT> that are currently visible modifying
	 * an existing driver. Keyed by <TT>ISQLDriver.getIdentifier()</TT>.
	 */
	private Map _modifySheets = new HashMap();

	/** Singleton instance of this class. */
	private static DriverMaintSheetFactory s_instance = new DriverMaintSheetFactory();

	/**
	 * ctor. Private as cass is a singleton.
	 */
	private DriverMaintSheetFactory() {
		super();
	}

	/**
	 * Return the single instance of this class.
	 *
	 * @return	the single instance of this class.
	 */
	public static DriverMaintSheetFactory getInstance() {
		return s_instance;
	}

	/**
	 * Initialize this class. This <EM>must</EM> be called prior to using this class.
	 */
	public static void initialize(IApplication app) {
		getInstance()._app = app;
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
	public synchronized DriverMaintSheet showModifySheet(ISQLDriver driver) {
		if (driver == null) {
			throw new IllegalArgumentException("ISQLDriver == null");
		}

		DriverMaintSheet sheet = get(driver);
		if (sheet == null) {
			sheet = new DriverMaintSheet(_app, driver, MODIFY);
			_modifySheets.put(driver.getIdentifier(), sheet);
			_app.getMainFrame().addInternalFrame(sheet, true, null);

			sheet.addInternalFrameListener(new InternalFrameAdapter() {
				public void internalFrameClosed(InternalFrameEvent evt) {
					synchronized( getInstance()) {
						DriverMaintSheet frame = (DriverMaintSheet)evt.getInternalFrame();
						_modifySheets.remove(frame.getSQLDriver().getIdentifier());
					}
				}
			});
			positionSheet(sheet);
		}

		sheet.moveToFront();

		return sheet;
	}

	/**
	 * Create and show a new maintenance sheet to allow the user to create a new driver.
	 *
	 * @return	The new maintenance sheet.
	 */
	public DriverMaintSheet showCreateSheet() {
		final DataCache cache = _app.getDataCache();
		final IdentifierFactory factory = IdentifierFactory.getInstance();
		final ISQLDriver driver = cache.createDriver(factory.createIdentifier());
		final DriverMaintSheet sheet = new DriverMaintSheet(_app, driver, NEW);
		_app.getMainFrame().addInternalFrame(sheet, true, null);
		positionSheet(sheet);
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
	public DriverMaintSheet showCopySheet(ISQLDriver driver) {
		if (driver == null) {
			throw new IllegalArgumentException("ISQLDriver == null");
		}

		final DataCache cache = _app.getDataCache();
		final IdentifierFactory factory = IdentifierFactory.getInstance();
		ISQLDriver newDriver = cache.createDriver(factory.createIdentifier());
		try {
			newDriver.assignFrom(driver);
		} catch (ValidationException ex) {
			s_log.error("Error occured copying the driver", ex);
		}
		final DriverMaintSheet sheet = new DriverMaintSheet(_app, newDriver, COPY);
		_app.getMainFrame().addInternalFrame(sheet, true, null);
		positionSheet(sheet);
		return sheet;
	}

	private DriverMaintSheet get(ISQLDriver driver) {
		return (DriverMaintSheet)_modifySheets.get(driver.getIdentifier());
	}

	private void positionSheet(DriverMaintSheet sheet) {
		GUIUtils.centerWithinDesktop(sheet);
		sheet.setVisible(true);
		sheet.moveToFront();
	}
}

