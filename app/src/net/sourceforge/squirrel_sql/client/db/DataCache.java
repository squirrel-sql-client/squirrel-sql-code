package net.sourceforge.squirrel_sql.client.db;
/*
 * TODO: Delete this class. Its been moved to fw
 *
 * Copyright (C) 2001-2003 Colin Bell
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
import java.net.URL;

import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
/**
 * XML cache of JDBC drivers and aliases.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DataCache
{
	/** Class for objects that define aliases to JDBC data sources. */
//	private final static Class SQL_ALIAS_IMPL = SQLAlias.class;
//
//	/** Class for objects that define JDBC drivers. */
//	private final static Class SQL_DRIVER_IMPL = SQLDriver.class;
//
//	/** Logger for this class. */
//	private static final ILogger s_log = LoggerController.createLogger(DataCache.class);
//
//	/** Driver manager. */
//	private final SQLDriverManager _driverMgr;
//
//	/** Cache that contains data. */
//	private final XMLObjectCache _cache = new XMLObjectCache();

	/**
	 * Ctor. Loads drivers and aliases from the XML document.
	 * 
	 * @param	driverMgr		Manages JDBC drivers.
	 * @param	dftDriversURL	URL that the default rivers can be loaded from.
	 * @param	msgHandler		Message handler to report on errors in this object.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if null <TT>SQLDriverManager</TT> or
	 * 			<TT>dftDriversURL</TT> passed.
	 */
	private DataCache(SQLDriverManager driverMgr, URL dftDriversURL,
						IMessageHandler msgHandler)
	{
		super();
//		if (driverMgr == null)
//		{
//			throw new IllegalArgumentException("SQLDriverManager == null");
//		}
//		if (dftDriversURL == null)
//		{
//			throw new IllegalArgumentException("dftDriversURL == null");
//		}
//
//		_driverMgr = driverMgr;
//
//		IMessageHandler myMsgHandler = msgHandler;
//		if (myMsgHandler == null)
//		{
//			myMsgHandler = NullMessageHandler.getInstance();
//		}
//		loadDrivers(dftDriversURL, myMsgHandler);
//		loadAliases(myMsgHandler);
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
//	public void saveDrivers(File file) throws IOException, XMLException
//	{
//		if (file == null)
//		{
//			throw new IllegalArgumentException("File == null");
//		}
//		_cache.saveAllForClass(file.getPath(), SQL_DRIVER_IMPL);
//	}

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
//	public void saveAliases(File file) throws IOException, XMLException
//	{
//		if (file == null)
//		{
//			throw new IllegalArgumentException("File == null");
//		}
//		_cache.saveAllForClass(file.getPath(), SQL_ALIAS_IMPL);
//	}

	/**
	 * Retrieve the <TT>ISQLDriver</TT> for the passed identifier.
	 * 
	 * @param	id	Identifier to retrieve driver for.
	 * 
	 * @return	the <TT>ISQLDriver</TT> for the passed identifier.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ISQLDriver</TT> passed.
	 */
//	public ISQLDriver getDriver(IIdentifier id)
//	{
//		if (id == null)
//		{
//			throw new IllegalArgumentException("ISQLDriver == null");
//		}
//
//		return (ISQLDriver)_cache.get(SQL_DRIVER_IMPL, id);
//	}

	/**
	 * Add a driver to the cache.
	 * 
	 * @param	sqlDriver	The driver to add.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>ISQLDriver</TT> is null.
	 */
//	public void addDriver(ISQLDriver sqlDriver)
//		throws ClassNotFoundException, IllegalAccessException,
//				InstantiationException, DuplicateObjectException,
//				MalformedURLException
//	{
//		if (sqlDriver == null)
//		{
//			throw new IllegalArgumentException("ISQLDriver == null");
//		}
//		_driverMgr.registerSQLDriver(sqlDriver);
//		_cache.add(sqlDriver);
//	}

//	public void removeDriver(ISQLDriver sqlDriver)
//	{
//		_cache.remove(SQL_DRIVER_IMPL, sqlDriver.getIdentifier());
//		_driverMgr.unregisterSQLDriver(sqlDriver);
//	}
//
//	public Iterator drivers()
//	{
//		return _cache.getAllForClass(SQL_DRIVER_IMPL);
//	}
//
//	public void addDriversListener(IObjectCacheChangeListener lis)
//	{
//		_cache.addChangesListener(lis, SQL_DRIVER_IMPL);
//	}

//	public void removeDriversListener(IObjectCacheChangeListener lis)
//	{
//		_cache.removeChangesListener(lis, SQL_DRIVER_IMPL);
//	}
//
//	public ISQLAlias getAlias(IIdentifier id)
//	{
//		return (ISQLAlias) _cache.get(SQL_ALIAS_IMPL, id);
//	}
//
//	public Iterator aliases()
//	{
//		return _cache.getAllForClass(SQL_ALIAS_IMPL);
//	}
//
//	public void addAlias(ISQLAlias alias) throws DuplicateObjectException
//	{
//		_cache.add(alias);
//	}
//
//	public void removeAlias(ISQLAlias alias)
//	{
//		_cache.remove(SQL_ALIAS_IMPL, alias.getIdentifier());
//	}
//
//	public Iterator getAliasesForDriver(ISQLDriver driver)
//	{
//		ArrayList data = new ArrayList();
//		for (Iterator it = aliases(); it.hasNext();)
//		{
//			ISQLAlias alias = (ISQLAlias) it.next();
//			if (driver.equals(getDriver(alias.getDriverIdentifier())))
//			{
//				data.add(alias);
//			}
//		}
//		return data.iterator();
//	}
//
//	public void addAliasesListener(IObjectCacheChangeListener lis)
//	{
//		_cache.addChangesListener(lis, SQL_ALIAS_IMPL);
//	}
//
//	public void removeAliasesListener(IObjectCacheChangeListener lis)
//	{
//		_cache.removeChangesListener(lis, SQL_ALIAS_IMPL);
//	}
//
//	/**
//	 * Load <TT>IISqlDriver</TT> objects from XML file.
//	 */
//	private void loadDrivers(URL dftDriversURL, IMessageHandler msgHandler)
//	{
//		final ApplicationFiles appFiles = new ApplicationFiles();
//		final File driversFile = appFiles.getDatabaseDriversFile();
//		try
//		{
//			try
//			{
//				_cache.load(driversFile.getPath());
//				if (!drivers().hasNext())
//				{
//					loadDefaultDrivers(dftDriversURL);
//				}
//				else
//				{
//					fixupDrivers();
//				}
//			}
//			catch (FileNotFoundException ex)
//			{
//				loadDefaultDrivers(dftDriversURL); // first time user has run pgm.
//			}
//			catch (Exception ex)
//			{
//				String msg = "Error loading driver file: " + driversFile.getPath()
//								+ ". Default drivers loaded instead.";
//				s_log.error(msg, ex);
//				msgHandler.showErrorMessage(msg);
//				msgHandler.showErrorMessage(ex);
//				loadDefaultDrivers(dftDriversURL);
//			}
//		}
//		catch (XMLException ex)
//		{
//			s_log.error("Error loading drivers", ex);
//		}
//		catch (IOException ex)
//		{
//			s_log.error("Error loading drivers", ex);
//		}
//
//		registerDrivers(msgHandler);
//	}
//
//	public ISQLAlias createAlias(IIdentifier id)
//	{
//		return new SQLAlias(id);
//	}
//
//	public ISQLDriver createDriver(IIdentifier id)
//	{
//		return new SQLDriver(id);
//	}
//
//	public void loadDefaultDrivers(URL url) throws IOException, XMLException
//	{
//		InputStreamReader isr = new InputStreamReader(url.openStream());
//		try
//		{
//			_cache.load(isr, null, true);
//		}
//		catch (DuplicateObjectException ex)
//		{
//			// If this happens then this is a programming error as we said
//			// in the above call to ingore these errors.
//			s_log.error("Received an unexpected DuplicateObjectException", ex);
//		}
//		finally
//		{
//			isr.close();
//		}
//	}
//
//	private void registerDrivers(IMessageHandler msgHandler)
//	{
//		SQLDriverManager driverMgr = _driverMgr;
//		for (Iterator it = drivers(); it.hasNext();)
//		{
//			ISQLDriver sqlDriver = (ISQLDriver) it.next();
//			try
//			{
//				driverMgr.registerSQLDriver(sqlDriver);
//			}
//			catch (ClassNotFoundException ignore)
//			{
//			}
//			catch (Throwable th)
//			{
//				String msg = "Unable to register JDCB driver " + sqlDriver.getName();
//				s_log.error(msg, th);
//				msgHandler.showErrorMessage(msg);
//				msgHandler.showErrorMessage(th);
//			}
//		}
//	}
//
//	private void loadAliases(IMessageHandler msgHandler)
//	{
//		final ApplicationFiles appFiles = new ApplicationFiles();
//		final File aliasesFile = appFiles.getDatabaseAliasesFile();
//		try
//		{
//			_cache.load(aliasesFile.getPath());
//		}
//		catch (FileNotFoundException ignore)
//		{ // first time user has run pgm.
//		}
//		catch (Exception ex)
//		{
//			String msg = "Error loading aliases file: " + aliasesFile.getPath();
//			s_log.error(msg, ex);
//			msgHandler.showErrorMessage(msg);
//			msgHandler.showErrorMessage(ex);
//		}
//	}
//
//	/**
//	 * In 1.1beta? the jar file for a driver was changed from only one allowed
//	 * to multiple ones allowed. This method changes the driver from the old
//	 * version to the new one to allow for loading old versions of the
//	 * SQLDrivers.xml file.
//	 */
//	private void fixupDrivers()
//	{
//		for (Iterator it = drivers(); it.hasNext();)
//		{
//			ISQLDriver driver = (ISQLDriver)it.next();
//			String[] fileNames = driver.getJarFileNames();
//			if (fileNames == null || fileNames.length == 0)
//			{
//				String fileName = driver.getJarFileName();
//				if (fileName != null && fileName.length() > 0)
//				{
//					driver.setJarFileNames(new String[] {fileName});
//					try
//					{
//						driver.setJarFileName(null);
//					}
//					catch (ValidationException ignore)
//					{
//					}
//				}
//			}
//		}
//	}
}
