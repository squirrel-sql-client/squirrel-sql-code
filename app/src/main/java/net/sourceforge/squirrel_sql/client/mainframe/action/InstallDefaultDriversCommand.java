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
import java.io.IOException;
import java.net.URL;

import net.sourceforge.squirrel_sql.client.gui.db.DataCache;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;
/**
 * This <CODE>ICommand</CODE> allows the user to install the defautl drivers.
 *
 * @author	<A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class InstallDefaultDriversCommand implements ICommand
{
	/** Application API. */
	private final IApplication _app;

	/** URL to load drivers from. */
	private final URL _url;

	/**
	 * Ctor.
	 *
	 * @param	app		Application API.
	 * @param	url		URL to load drivers from.
	 *
	 * @throws	IllegalArgumentException	Thrown if <TT>null</TT>
	 *										<TT>IApplication</TT> passed.
	 * @throws	IllegalArgumentException	Thrown if <TT>null</TT>
	 *										<TT>URL</TT> passed.
	 */
	public InstallDefaultDriversCommand(IApplication app, URL url)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		if (url == null)
		{
			throw new IllegalArgumentException("URL == null");
		}

		_app = app;
		_url = url;
	}

	/**
	 * Load the default drivers into the cache and then
	 * make sure that the drivers list is showing all
	 * drivers.
	 */
	public void execute() throws BaseException
	{
		try
		{
			final DataCache cache = _app.getDataCache();
			cache.loadDefaultDrivers(_url);
			new ShowLoadedDriversOnlyCommand(_app, false).execute();
		}
		catch (IOException ex)
		{
			throw new BaseException(ex);
		}
	}
}
