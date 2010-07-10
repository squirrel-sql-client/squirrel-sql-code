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
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;

/**
 * Factory to handle creation of maintenance sheets for SQL Alias objects.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class AliasMaintSheetFactory implements AliasMaintSheet.MaintenanceType {
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(AliasMaintSheetFactory.class);

	/** Application API. */
	private IApplication _app;

	/**
	 * Collection of <TT>AliasMaintDialog</TT> that are currently visible modifying
	 * an existing aliss. Keyed by <TT>ISQLAlias.getIdentifier()</TT>.
	 */
	private Map _modifySheets = new HashMap();

	/** Singleton instance of this class. */
	private static AliasMaintSheetFactory s_instance = new AliasMaintSheetFactory();

	/**
	 * ctor. Private as cass is a singleton.
	 */
	private AliasMaintSheetFactory() {
		super();
	}

	/**
	 * Return the single instance of this class.
	 *
	 * @return	the single instance of this class.
	 */
	public static AliasMaintSheetFactory getInstance() {
		return s_instance;
	}

	/**
	 * Initialize this class. This <EM>must</EM> be called prior to using this class.
	 */
	public static void initialize(IApplication app) {
		getInstance()._app = app;
	}

	/**
	 * Get a maintenance sheet for the passed alias. If a maintenance sheet already
	 * exists it will be brought to the front. If one doesn't exist it will be
	 * created.
	 *
	 * @param	alias	The alias that user has requested to modify.
	 *
	 * @return	The maintenance sheet for the passed alias.
	 *
	 * @throws	IllegalArgumentException	if a <TT>null</TT> <TT>ISQLAlias</TT> passed.
	 */
	public synchronized AliasMaintSheet showModifySheet(ISQLAlias alias) {
		if (alias == null) {
			throw new IllegalArgumentException("ISQLALias == null");
		}

		AliasMaintSheet sheet = get(alias);
		if (sheet == null) {
			sheet = new AliasMaintSheet(_app, alias, MODIFY);
			_modifySheets.put(alias.getIdentifier(), sheet);
			_app.getMainFrame().addInternalFrame(sheet, true, null);

			sheet.addInternalFrameListener(new InternalFrameAdapter() {
				public void internalFrameClosed(InternalFrameEvent evt) {
					synchronized( getInstance()) {
						AliasMaintSheet frame = (AliasMaintSheet)evt.getInternalFrame();
						_modifySheets.remove(frame.getSQLAlias().getIdentifier());
					}
				}
			});
			positionSheet(sheet);
		}

		sheet.moveToFront();

		return sheet;
	}

	/**
	 * Create and show a new maintenance sheet to allow the user to create a new alias.
	 *
	 * @return	The new maintenance sheet.
	 */
	public AliasMaintSheet showCreateSheet() {
		final DataCache cache = _app.getDataCache();
		final IdentifierFactory factory = IdentifierFactory.getInstance();
		final ISQLAlias alias = cache.createAlias(factory.createIdentifier());
		final AliasMaintSheet sheet = new AliasMaintSheet(_app, alias, NEW);
		_app.getMainFrame().addInternalFrame(sheet, true, null);
		positionSheet(sheet);
		return sheet;
	}

	/**
	 * Create and show a new maintenance sheet that will allow the user to create a
	 * new alias that is a copy of the passed one.
	 *
	 * @return	The new maintenance sheet.
	 *
	 * @throws	IllegalArgumentException	if a <TT>null</TT> <TT>ISQLAlias</TT> passed.
	 */
	public AliasMaintSheet showCopySheet(ISQLAlias alias) {
		if (alias == null) {
			throw new IllegalArgumentException("ISQLALias == null");
		}

		final DataCache cache = _app.getDataCache();
		final IdentifierFactory factory = IdentifierFactory.getInstance();
		ISQLAlias newAlias = cache.createAlias(factory.createIdentifier());
		try {
			newAlias.assignFrom(alias);
		} catch (ValidationException ex) {
			s_log.error("Error occured copying the alias", ex);
		}
		final AliasMaintSheet sheet = new AliasMaintSheet(_app, newAlias, COPY);
		_app.getMainFrame().addInternalFrame(sheet, true, null);
		positionSheet(sheet);
		return sheet;
	}

	private AliasMaintSheet get(ISQLAlias alias) {
		return (AliasMaintSheet)_modifySheets.get(alias.getIdentifier());
	}

	private void positionSheet(AliasMaintSheet sheet) {
		GUIUtils.centerWithinDesktop(sheet);
		sheet.setVisible(true);
		sheet.moveToFront();
	}
}

