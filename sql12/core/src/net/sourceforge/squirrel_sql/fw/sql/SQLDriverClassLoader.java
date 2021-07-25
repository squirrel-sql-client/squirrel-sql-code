package net.sourceforge.squirrel_sql.fw.sql;
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

import net.sourceforge.squirrel_sql.fw.util.SquirrelURLClassLoader;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SQLDriverClassLoader extends SquirrelURLClassLoader
{
	private final static ILogger s_log = LoggerController.createLogger(SQLDriverClassLoader.class);

	public SQLDriverClassLoader(ISQLDriver sqlDriver)
	{
		super(createURLs(sqlDriver.getName(), sqlDriver.getJarFileNames()));
	}

	public SQLDriverClassLoader(URL[] urls)
	{
		super(urls);
	}

	public SQLDriverClassLoader(URL url)
	{
		super(url);
	}

	public Class<?>[] getDriverClasses(ILogger logger)
	{
		final Class<?>[] classes = getAssignableClasses(Driver.class, logger);
		final List<Class<?>> list = new ArrayList<>();
		for (int i = 0; i < classes.length; ++i)
		{
			Class<?> clazz = classes[i];
			if (!Modifier.isAbstract(clazz.getModifiers()))
			{
				list.add(clazz);
			}
		}
		return list.toArray(new Class[list.size()]);
	}

	private static URL[] createURLs(String driverName, String[] fileNames)
	{
		try
		{
			if (fileNames == null)
			{
				fileNames = new String[0];
			}
			URL[] urls = new URL[fileNames.length];
			for (int i = 0; i < fileNames.length; ++i)
			{
				File f = new File(fileNames[i]);
				if (!f.exists())
				{
					s_log.info( "For driver '" + driverName + "', the JVM says file doesn't exist: " + fileNames[i]);
				}
				if (f.isDirectory())
				{
					s_log.info("For driver '" + driverName + "', the JVM says the file is a directory: " + fileNames[i]);
				}
				if (!f.canRead())
				{
					s_log.info( "For driver '" + driverName + "', the JVM says the file can't be read: " + fileNames[i]);
				}
				urls[i] = f.toURI().toURL();

			}
			return urls;
		}
		catch (MalformedURLException e)
		{
			throw Utilities.wrapRuntime(e);
		}
	}


	//////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////
	// BEGIN: JDBC-DRIVER JARS FIRST LOADING
	// The following two methods are based upon the idea of child first class loading,
	// see https://medium.com/@isuru89/java-a-child-first-class-loader-cbd9c3d0305.
	// But quite some adjustments were made in order to ensure that JDBC-driver jars
	// (i.e. jars from the extra class path of SQuirreL's driver definitions) are accessed first.
	// The aim is to avoid exceptions resulting from version conflicts between classes contained
	// in the driver jars and in SQuirreL's libs.
	//
	// These changes where triggered by bug #1458. Attached to the this bug are JDBC-driver jars
	// that allow to reproduce the problem.
	//
	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
	{
		// has the class loaded already?
		Class<?> loadedClass = findLoadedClass(name);
		if (null != loadedClass)
		{
			checkResolveClass(resolve, loadedClass);
			return loadedClass;
		}

		try
		{
			loadedClass = findClass(name);
			checkResolveClass(resolve, loadedClass);
			return loadedClass;
		}
		catch (ClassNotFoundException e)
		{
			try
			{
				loadedClass = super.loadClass(name, resolve);
				checkResolveClass(resolve, loadedClass);
				return loadedClass;
			}
			catch (ClassNotFoundException e2)
			{
				if (null != getSystemClassLoader())
				{
					loadedClass = getSystemClassLoader().loadClass(name);
					checkResolveClass(resolve, loadedClass);
					return loadedClass;
				}
				else
				{
					throw e2;
				}
			}
		}
	}

	private void checkResolveClass(boolean resolve, Class<?> loadedClass)
	{
		if (resolve)
		{
			resolveClass(loadedClass);
		}
	}


	@Override
	public Enumeration<URL> getResources(String name) throws IOException
	{
		// Peculiar: Changing the order here results in a NullPointer
		// when the JDBS-driver classes attached to bug #1458 are loaded.

		List<URL> allRes = new LinkedList<>();

		Enumeration<URL> sysResources = getSystemClassLoader().getResources(name);
		if (sysResources != null)
		{
			while (sysResources.hasMoreElements())
			{
				allRes.add(sysResources.nextElement());
			}
		}

		Enumeration<URL> thisRes = findResources(name);
		if (thisRes != null)
		{
			while (thisRes.hasMoreElements())
			{
				allRes.add(thisRes.nextElement());
			}
		}

		Enumeration<URL> parentRes = super.findResources(name);
		if (parentRes != null)
		{
			while (parentRes.hasMoreElements())
			{
				allRes.add(parentRes.nextElement());
			}
		}

		return new Enumeration<URL>()
		{
			Iterator<URL> it = allRes.iterator();

			@Override
			public boolean hasMoreElements()
			{
				return it.hasNext();
			}

			@Override
			public URL nextElement()
			{
				return it.next();
			}
		};
	}

	@Override
	public URL getResource(String name)
	{
		URL res = findResource(name);

		if (res != null)
		{
			return res;
		}

		if (getSystemClassLoader() != null)
		{
			res = getSystemClassLoader().getResource(name);
			if (res != null)
			{
				return res;
			}
		}

		return super.getResource(name);
	}

	// END: JDBC-DRIVER JARS FIRST LOADING
	//////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////
}
