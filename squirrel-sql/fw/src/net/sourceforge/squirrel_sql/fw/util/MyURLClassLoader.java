package net.sourceforge.squirrel_sql.fw.util;
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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;

public class MyURLClassLoader extends URLClassLoader
{
	private Map _classes = new HashMap();

	public MyURLClassLoader(String fileName) throws IOException
	{
		this(new File(fileName).toURL());
	}

	public MyURLClassLoader(URL url)
	{
		this(new URL[] { url });
	}

	public MyURLClassLoader(URL[] urls)
	{
		super(urls, ClassLoader.getSystemClassLoader());
	}

	public Class[] getAssignableClasses(Class type, ILogger logger)
		throws IOException
	{
		List classes = new ArrayList();
		URL[] urls = getURLs();
		for (int i = 0; i < urls.length; ++i)
		{
			URL url = urls[i];
			File file = new File(url.getFile());
			if (!file.isDirectory() && file.exists() && file.canRead())
			{
				ZipFile zipFile = null;
				try
				{
					zipFile = new ZipFile(file);
				}
				catch (IOException ex)
				{
					logger.error("Error occured trying to load: " + file.getAbsolutePath(),
									ex);
				}
				for (Iterator it = new EnumerationIterator(zipFile.entries());
					it.hasNext();
					)
				{
					Class cls = null;
					String entryName = ((ZipEntry) it.next()).getName();
					String className =
						Utilities.changeFileNameToClassName(entryName);
					if (className != null)
					{
						try
						{
							cls = loadClass(className);
						}
						catch (Throwable th)
						{
							logger.error("Error loading class " + className,
											th);
						}
						if (cls != null)
						{
							if (type.isAssignableFrom(cls))
							{
								classes.add(cls);
							}
						}
					}
				}
			}
		}
		return (Class[]) classes.toArray(new Class[classes.size()]);
	}

	protected synchronized Class findClass(String className)
		throws ClassNotFoundException
	{
		Class cls = (Class) _classes.get(className);
		if (cls == null)
		{
			cls = super.findClass(className);
			_classes.put(className, cls);
		}
		return cls;
	}

	protected void classHasBeenLoaded(Class cls)
	{
	}
}
