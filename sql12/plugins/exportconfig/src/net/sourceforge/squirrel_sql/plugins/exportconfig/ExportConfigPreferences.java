package net.sourceforge.squirrel_sql.plugins.exportconfig;

import java.io.File;
import java.io.IOException;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;

/*
 * Copyright (C) 2003 Colin Bell
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

/**
 * Preferences for this plugin.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ExportConfigPreferences
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ExportConfigPreferences.class);

	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(ExportConfigPreferences.class);

	/** If <TT>true</TT> then export preferences. */
	private boolean _exportPreferences = true;

	/** If <TT>true</TT> then export drivers. */
	private boolean _exportDrivers = true;

	/** If <TT>true</TT> then export aliases. */
	private boolean _exportAliases = true;

	/** If <TT>true</TT> then include user names in aliases export. */
	private boolean _includeUserNames = true;

	/** If <TT>true</TT> then include passwords in aliases export. */
	private boolean _includePasswords = true;

	/** Name of file to export preferences to. */
	private String _preferencesFileName;

	/** Name of file to export preferences to. */
	private String _driversFileName;

	/** Name of file to export aliases to. */
	private String _aliasesFileName;

	/**
	 * Default ctor.
	 */
	public ExportConfigPreferences()
	{
		super();

		final File here = new File(".");
		final ApplicationFiles appFiles = new ApplicationFiles();
		_preferencesFileName = getFileName(here, appFiles.getUserPreferencesFile().getName());
		_driversFileName = getFileName(here, appFiles.getDatabaseDriversFile().getName());
		_aliasesFileName = getFileName(here, appFiles.getDatabaseAliasesFile().getName());
	}

	/**
	 * If <TT>true</TT> then export preferences.
	 *
	 * @return	<TT>true</TT> if preferences are to be exported.
	 */
	public boolean getExportPreferences()
	{
		return _exportPreferences;
	}

	/**
	 * Specify whether to export preferences.
	 *
	 * @param	value	<TT>true</TT> if preferences are to be exported.
	 */
	public void setExportPreferences(boolean value)
	{
		_exportPreferences = value;
	}

	/**
	 * If <TT>true</TT> then export drivers.
	 *
	 * @return	<TT>true</TT> if drivers are to be exported.
	 */
	public boolean getExportDrivers()
	{
		return _exportDrivers;
	}

	/**
	 * Specify whether to export drivers.
	 *
	 * @param	value	<TT>true</TT> if drivers are to be exported.
	 */
	public void setExportDrivers(boolean value)
	{
		_exportDrivers = value;
	}

	/**
	 * If <TT>true</TT> then export aliases.
	 *
	 * @return	<TT>true</TT> if aliases are to be exported.
	 */
	public boolean getExportAliases()
	{
		return _exportAliases;
	}

	/**
	 * Specify whether to export aliases.
	 *
	 * @param	value	<TT>true</TT> if aliases are to be exported.
	 */
	public void setExportAliases(boolean value)
	{
		_exportAliases = value;
	}

	/**
	 * If <TT>true</TT> then include user names in aliases export.
	 *
	 * @return	<TT>true</TT> if user names are to be included in aliases
	 *			export.
	 */
	public boolean getIncludeUserNames()
	{
		return _includeUserNames;
	}

	/**
	 * Specify whether to include user names in aliases export.
	 *
	 * @param	value	<TT>true</TT> if user names are to be included in
	 *					aliases export.
	 */
	public void setIncludeUserNames(boolean value)
	{
		_includeUserNames = value;
	}

	/**
	 * If <TT>true</TT> then include passwords in aliases export.
	 *
	 * @return	<TT>true</TT> if passwords are to be included in aliases
	 *			export.
	 */
	public boolean getIncludePasswords()
	{
		return _includePasswords;
	}

	/**
	 * Specify whether to include passwords in aliases export.
	 *
	 * @param	value	<TT>true</TT> if passwords are to be included in
	 *					aliases export.
	 */
	public void setIncludePasswords(boolean value)
	{
		_includePasswords = value;
	}

	/**
	 * Retrieve the fully qualified name of the file to save preferences to.
	 * 
	 * @return	the fully qualified name of the file to save preferences to.
	 */
	public String getPreferencesFileName()
	{
		return _preferencesFileName;
	}

	/**
	 * Specify the fully qualified name of the file to save preferences to.
	 *
	 * @param	value	the fully qualified name of the file to save preferences
	 *					to.
	 */
	public void setPreferencesFileName(String value)
	{
		_preferencesFileName = value;
	}

	/**
	 * Retrieve the fully qualified name of the file to save drivers to.
	 * 
	 * @return	the fully qualified name of the file to save drivers to.
	 */
	public String getDriversFileName()
	{
		return _driversFileName;
	}

	/**
	 * Specify the fully qualified name of the file to save drivers to.
	 *
	 * @param	value	the fully qualified name of the file to save drivers
	 *					to.
	 */
	public void setDriversFileName(String value)
	{
		_driversFileName = value;
	}

	/**
	 * Retrieve the fully qualified name of the file to save aliases to.
	 * 
	 * @return	the fully qualified name of the file to save aliases to.
	 */
	public String getAliasesFileName()
	{
		return _aliasesFileName;
	}

	/**
	 * Specify the fully qualified name of the file to save aliases to.
	 *
	 * @param	value	the fully qualified name of the file to save aliases
	 *					to.
	 */
	public void setAliasesFileName(String value)
	{
		_aliasesFileName = value;
	}

	private String getFileName(File dir, String name)
	{
		
		return getFileName(new File(dir, name));
	}

	private String getFileName(File file)
	{
		try
		{
			return file.getCanonicalPath();
		}
		catch (IOException ex)
		{
			// i18n[exportconfig.errorResolvingFile=Error resolving file name]
			s_log.error(s_stringMgr.getString("exportconfig.errorResolvingFile"), ex);
		}
		return file.getAbsolutePath();
	}
}
