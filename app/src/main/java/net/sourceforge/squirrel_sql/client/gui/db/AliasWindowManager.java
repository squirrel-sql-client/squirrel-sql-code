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
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
/**
 * This class manages the windows relating to JDBC aliases.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 * @author <A HREF="mailto:jmheight@users.sourceforge.net">Jason Height</A>
 */
public class AliasWindowManager
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(AliasWindowManager.class);

	/** Application API. */
	private final IApplication _app;

	/** Window Factory for alias maintenace windows. */
	private final AliasWindowFactory _aliasWinFactory;

	/**
	 * Ctor.
	 *
	 * @param	app		Application API.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>IApplication</TT> passed.
	 */
	public AliasWindowManager(IApplication app)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}

		_app = app;
		_aliasWinFactory = new AliasWindowFactory(_app);
	}

	/**
	 * Get a maintenance sheet for the passed alias. If a maintenance sheet already
	 * exists it will be brought to the front. If one doesn't exist it will be
	 * created.
	 *
	 * @param	alias	The alias that user has requested to modify.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISQLAlias</TT> passed.
	 */
	public void showModifyAliasInternalFrame(final ISQLAlias alias)
	{
		if (alias == null)
		{
			throw new IllegalArgumentException("ISQLAlias == null");
		}

		moveToFront(_aliasWinFactory.getModifySheet(alias));
	}

	/**
	 * Create and show a new maintenance window to allow the user to create a
	 * new alias.
	 */
	public void showNewAliasInternalFrame()
	{
		moveToFront(_aliasWinFactory.getCreateSheet());
	}

	/**
	 * Create and show a new maintenance sheet that will allow the user to create a
	 * new alias that is a copy of the passed one.
	 *
	 * @return	The new maintenance sheet.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISQLAlias</TT> passed.
	 */
	public void showCopyAliasInternalFrame(final SQLAlias alias)
	{
		if (alias == null)
		{
			throw new IllegalArgumentException("ISQLAlias == null");
		}

		moveToFront(_aliasWinFactory.getCopySheet(alias));
	}

	public void moveToFront(final AliasInternalFrame fr)
	{
		if (fr != null)
		{
			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					fr.moveToFront();
				}
			});
		}
		else
		{
			s_log.debug("JInternalFrame == null");
		}
	}

}
