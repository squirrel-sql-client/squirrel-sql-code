package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Driver;

import net.sourceforge.squirrel_sql.fw.util.MyURLClassLoader;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;

public class SQLDriverClassLoader extends MyURLClassLoader
{
	public SQLDriverClassLoader(ISQLDriver sqlDriver) throws MalformedURLException
	{
		super(createURLs(sqlDriver.getJarFileNames()));
	}

	public SQLDriverClassLoader(URL url)
	{
		super(url);
	}

	public Class[] getDriverClasses(ILogger logger) throws IOException
	{
		return getAssignableClasses(Driver.class, logger);
	}

	private static URL[] createURLs(String[] fileNames)
		throws MalformedURLException
	{
		if (fileNames == null)
		{
			fileNames = new String[0];
		}
		URL[] urls = new URL[fileNames.length];
		for (int i = 0; i < fileNames.length; ++i)
		{
			urls[i] = new File(fileNames[i]).toURL();
		}
		return urls;
	}
}