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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.ObjectCacheChangeListener;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;
import net.sourceforge.squirrel_sql.fw.xml.XMLObjectCache;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;

/**
 * XML cache of JDBC drivers and aliases.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DataCache
{
	private final static Class SQL_ALIAS_IMPL = SQLAlias.class;
	private final static Class SQL_DRIVER_IMPL = SQLDriver.class;

	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(DataCache.class);

	/** Application API. */
	private IApplication _app;

	/** Cache that contains data. */
	private XMLObjectCache _cache = new XMLObjectCache();

	/**
	 * Ctor. Loads drivers and aliases from the XML document.
	 *
	 * @param	app	Application API.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if <TT>null</TT> <TT>IApplication</TT>
	 *			passed.
	 *
	 * @throws	IllegalStateException
	 *			Thrown if no <TT>SQLDriverManager</TT>
	 *			exists in IApplication.
	 */
	public DataCache(IApplication app) throws IllegalArgumentException
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (app.getSQLDriverManager() == null)
		{
			throw new IllegalStateException("No SQLDriverManager in IApplication");
		}

		_app = app;

		loadDrivers();
		loadAliases();
	}

	/**
	 * Save cached objects. JDBC drivers are saved to
	 * <CODE>ApplicationFiles.getUserDriversFileName()</CODE> and aliases are
	 * saved to <CODE>ApplicationFiles.getUserAliasesFileName()</CODE>.
	 */
	public void save()
	{
		final ApplicationFiles appFiles = new ApplicationFiles();
		final File driversFile = appFiles.getDatabaseDriversFile();
		try
		{
			saveDrivers(driversFile);
		}
		catch (Exception ex)
		{
			String msg = "Error occured saving drivers to " + driversFile.getPath();
			s_log.error(msg, ex);
			_app.showErrorDialog(msg, ex);
		}

		final File aliasesFile = appFiles.getDatabaseAliasesFile();
		try
		{
			saveAliases(aliasesFile);
		}
		catch (Exception ex)
		{
			String msg = "Error occured saving aliases to " + aliasesFile.getPath();
			s_log.error(msg, ex);
			_app.showErrorDialog(msg, ex);
		}
	}

	/**
	 * Save JDBC drivers to the passed file as XML.
	 * 
	 * @param	file	File to save drivers to.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>File</TT> passed.
	 * @throws	IOException
	 * 			Thrown if an I/O error occurs saving.
	 * @throws	XMLException
	 * 			Thrown if an error occurs translating drivers to XML.
	 */
	public void saveDrivers(File file)
		throws IOException, XMLException
	{
		if (file == null)
		{
			throw new IllegalArgumentException("File == null");
		}
		_cache.saveAllForClass(file.getPath(), SQL_DRIVER_IMPL);
	}

	/**
	 * Save aliases to the passed file as XML.
	 * 
	 * @param	file	File to save aliases to.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>File</TT> passed.
	 * @throws	IOException
	 * 			Thrown if an I/O error occurs saving.
	 * @throws	XMLException
	 * 			Thrown if an error occurs translating aliases to XML.
	 */
	public void saveAliases(File file)
		throws IOException, XMLException
	{
		if (file == null)
		{
			throw new IllegalArgumentException("File == null");
		}
		_cache.saveAllForClass(file.getPath(), SQL_ALIAS_IMPL);
	}

	/**
	 * Return the <TT>ISQLDriver</TT> for the passed identifier.
	 */
	public ISQLDriver getDriver(IIdentifier id)
	{
		return (ISQLDriver) _cache.get(SQL_DRIVER_IMPL, id);
	}

	public void addDriver(ISQLDriver sqlDriver)
		throws ClassNotFoundException, IllegalAccessException,
				InstantiationException, DuplicateObjectException,
				MalformedURLException
	{
		_app.getSQLDriverManager().registerSQLDriver(sqlDriver);
		_cache.add(sqlDriver);
	}

	public void removeDriver(ISQLDriver sqlDriver)
	{
		_cache.remove(SQL_DRIVER_IMPL, sqlDriver.getIdentifier());
		try
		{
			_app.getSQLDriverManager().unregisterSQLDriver(sqlDriver);
		}
		catch (Exception ex)
		{
			String msg = "Error occured removing driver from cache";
			s_log.error(msg, ex);
			_app.showErrorDialog(msg, ex);
		}
	}

	public Iterator drivers()
	{
		return _cache.getAllForClass(SQL_DRIVER_IMPL);
	}

	public void addDriversListener(ObjectCacheChangeListener lis)
	{
		_cache.addChangesListener(lis, SQL_DRIVER_IMPL);
	}

	public void removeDriversListener(ObjectCacheChangeListener lis)
	{
		_cache.removeChangesListener(lis, SQL_DRIVER_IMPL);
	}

	public ISQLAlias getAlias(IIdentifier id)
	{
		return (ISQLAlias) _cache.get(SQL_ALIAS_IMPL, id);
	}

	public Iterator aliases()
	{
		return _cache.getAllForClass(SQL_ALIAS_IMPL);
	}

	public void addAlias(ISQLAlias alias) throws DuplicateObjectException
	{
		_cache.add(alias);
	}

	public void removeAlias(ISQLAlias alias)
	{
		_cache.remove(SQL_ALIAS_IMPL, alias.getIdentifier());
	}

	public Iterator getAliasesForDriver(ISQLDriver driver)
	{
		ArrayList data = new ArrayList();
		for (Iterator it = aliases(); it.hasNext();)
		{
			ISQLAlias alias = (ISQLAlias) it.next();
			if (driver.equals(getDriver(alias.getDriverIdentifier())))
			{
				data.add(alias);
			}
		}
		return data.iterator();
	}

	public void addAliasesListener(ObjectCacheChangeListener lis)
	{
		_cache.addChangesListener(lis, SQL_ALIAS_IMPL);
	}

	public void removeAliasesListener(ObjectCacheChangeListener lis)
	{
		_cache.removeChangesListener(lis, SQL_ALIAS_IMPL);
	}

	/**
	 * Load <TT>IISqlDriver</TT> objects from XML file.
	 */
	private void loadDrivers()
	{
		final ApplicationFiles appFiles = new ApplicationFiles();
		final File driversFile = appFiles.getDatabaseDriversFile();
		try
		{
			_cache.load(driversFile.getPath());
			if (!drivers().hasNext())
			{
				loadDefaultDrivers();
			}
			else
			{
				fixupDrivers();
			}
		}
		catch (FileNotFoundException ex)
		{
			loadDefaultDrivers(); // first time user has run pgm.
		}
		catch (Exception ex)
		{
			String msg = "Error loading driver file: " + driversFile.getPath()
							+ ". Default drivers loaded instead.";
			s_log.error(msg, ex);
			_app.showErrorDialog(msg, ex);
			loadDefaultDrivers();
		}

		registerDrivers();
	}

	public ISQLAlias createAlias(IIdentifier id)
	{
		return new SQLAlias(id);
	}

	public ISQLDriver createDriver(IIdentifier id)
	{
		return new SQLDriver(id);
	}

	private void loadDefaultDrivers()
	{
		final URL url = _app.getResources().getDefaultDriversUrl();
		try
		{
			InputStreamReader isr = new InputStreamReader(url.openStream());
			try
			{
				_cache.load(isr);
			}
			finally
			{
				isr.close();
			}
		}
		catch (Exception ex)
		{
			String msg = "Error loading default driver file: " +
							url != null ? url.toExternalForm() : "";
			s_log.error(msg, ex);
			_app.showErrorDialog(msg, ex);
		}
	}

	private void registerDrivers()
	{
		SQLDriverManager driverMgr = _app.getSQLDriverManager();
		for (Iterator it = drivers(); it.hasNext();)
		{
			ISQLDriver sqlDriver = (ISQLDriver) it.next();
			try
			{
				driverMgr.registerSQLDriver(sqlDriver);
			}
			catch (ClassNotFoundException ex)
			{
				s_log.warn(
					"Could not find JDBC driver class for "
						+ sqlDriver.getName()
						+ ": "
						+ sqlDriver.getDriverClassName());
			}
			catch (Throwable th)
			{
				String msg = "Unable to register JDCB driver " + sqlDriver.getName();
				s_log.error(msg, th);
				_app.showErrorDialog(msg, th);
			}
		}
	}

	private void loadAliases()
	{
		final ApplicationFiles appFiles = new ApplicationFiles();
		final File aliasesFile = appFiles.getDatabaseAliasesFile();
		try
		{
			_cache.load(aliasesFile.getPath());
		}
		catch (FileNotFoundException ignore)
		{ // first time user has run pgm.
		}
		catch (Exception ex)
		{
			String msg = "Error loading aliases file: " + aliasesFile.getPath();
			s_log.error(msg, ex);
			_app.showErrorDialog(msg, ex);
		}
	}

	/**
	 * In 1.1beta? the jar file for a driver was changed from only one allowed
	 * to multiple one allowed. This method changes the driver from the old
	 * version to the new one.
	 */
	private void fixupDrivers()
	{
		for (Iterator it = drivers(); it.hasNext();)
		{
			ISQLDriver driver = (ISQLDriver)it.next();
			String[] fileNames = driver.getJarFileNames();
			if (fileNames == null || fileNames.length == 0)
			{
				String fileName = driver.getJarFileName();
				if (fileName != null && fileName.length() > 0)
				{
					driver.setJarFileNames(new String[] {fileName});
					try
					{
						driver.setJarFileName(null);
					}
					catch (ValidationException ignore)
					{
					}
				}
			}
		}
	}
}