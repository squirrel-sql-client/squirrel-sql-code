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
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;
/**
 * Factory to handle creation of maintenance sheets for SQL Alias objects.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class AliasWindowFactory implements AliasInternalFrame.IMaintenanceType
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(AliasWindowFactory.class);

	/** Application API. */
	private final IApplication _app;

	/**
	 * Collection of <TT>AliasMaintDialog</TT> that are currently visible modifying
	 * an existing aliss. Keyed by <TT>ISQLAlias.getIdentifier()</TT>.
	 */
	private Map _modifySheets = new HashMap();

	/**
	 * ctor.
	 */
	public AliasWindowFactory(IApplication app)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}

		_app = app;
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
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISQLAlias</TT> passed.
	 */
	public synchronized AliasInternalFrame showModifySheet(ISQLAlias alias)
	{
		if (alias == null)
		{
			throw new IllegalArgumentException("ISQLALias == null");
		}

		AliasInternalFrame sheet = get(alias);
		if (sheet == null)
		{
			sheet = new AliasInternalFrame(_app, alias, MODIFY);
			_modifySheets.put(alias.getIdentifier(), sheet);
			_app.getMainFrame().addInternalFrame(sheet, true, null);

			sheet.addInternalFrameListener(new InternalFrameAdapter()
			{
				public void internalFrameClosed(InternalFrameEvent evt)
				{
					synchronized (AliasWindowFactory.this)
					{
						AliasInternalFrame frame = (AliasInternalFrame)evt.getInternalFrame();
						_modifySheets.remove(frame.getSQLAlias().getIdentifier());
					}
				}
			});
			GUIUtils.centerWithinDesktop(sheet);
		}

		return sheet;
	}

	/**
	 * Create and show a new maintenance sheet to allow the user to create a new alias.
	 *
	 * @return	The new maintenance sheet.
	 */
	public AliasInternalFrame showCreateSheet()
	{
		final DataCache cache = _app.getDataCache();
		final IIdentifierFactory factory = IdentifierFactory.getInstance();
		final ISQLAlias alias = cache.createAlias(factory.createIdentifier());
		final AliasInternalFrame sheet = new AliasInternalFrame(_app, alias, NEW);
		_app.getMainFrame().addInternalFrame(sheet, true, null);
		GUIUtils.centerWithinDesktop(sheet);
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
	public AliasInternalFrame showCopySheet(ISQLAlias alias)
	{
		if (alias == null)
		{
			throw new IllegalArgumentException("ISQLALias == null");
		}

		final DataCache cache = _app.getDataCache();
		final IIdentifierFactory factory = IdentifierFactory.getInstance();
		ISQLAlias newAlias = cache.createAlias(factory.createIdentifier());
		try
		{
			newAlias.assignFrom(alias);
		}
		catch (ValidationException ex)
		{
			s_log.error("Error occured copying the alias", ex);
		}
		final AliasInternalFrame sheet = new AliasInternalFrame(_app, newAlias, COPY);
		_app.getMainFrame().addInternalFrame(sheet, true, null);
		GUIUtils.centerWithinDesktop(sheet);
		return sheet;
	}

	private AliasInternalFrame get(ISQLAlias alias)
	{
		return (AliasInternalFrame) _modifySheets.get(alias.getIdentifier());
	}
}
