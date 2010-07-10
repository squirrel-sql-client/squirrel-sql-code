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
import java.io.IOException;
import java.net.URL;

import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.db.DataCache;
/**
 * This <CODE>ICommand</CODE> allows the user to install the defautl drivers.
 *
 * @author	<A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class InstallDefaultDriversCommand implements ICommand
{
	/** Cache containing the drivers. */
	private final DataCache _cache;

	/** URL to load drivers from. */
	private final URL _url;

	/**
	 * Ctor.
	 * 
	 * @param	cache	Cache containing the drivers.
	 * @param	url		URL to load drivers from.
	 * 
	 * @throws	IllegalArgumentException	Thrown if <TT>null</TT>
	 *										<TT>DataCache</TT> passed.
	 * @throws	IllegalArgumentException	Thrown if <TT>null</TT>
	 *										<TT>URL</TT> passed.
	 */
	public InstallDefaultDriversCommand(DataCache cache, URL url)
	{
		super();
		if (cache == null)
		{
			throw new IllegalArgumentException("DataCache == null");
		}
		if (url == null)
		{
			throw new IllegalArgumentException("URL == null");
		}

		_cache = cache;
		_url = url;
	}

	public void execute() throws BaseException
	{
		try
		{
			_cache.loadDefaultDrivers(_url);
		}
		catch (IOException ex)
		{
			throw new BaseException(ex);
		}
	}
}
