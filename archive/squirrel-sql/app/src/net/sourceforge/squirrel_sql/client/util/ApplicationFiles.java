package net.sourceforge.squirrel_sql.client.util;
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

import net.sourceforge.squirrel_sql.fw.util.IJavaPropertyNames;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;

public class ApplicationFiles
{
	/** Name of directory to contain users settings. */
	private String _userSettingsDir;

	/** Name of folder that contains Squirrel app. */
	private final String _squirrelHomeDir;

	/** Name of folder that contains Squirrel libraries. */
	private final String _squirrelLibrariesDir;

	/** Name of folder that contains plugins. */
	private String _squirrelPluginsDir;

	/** Documentation folder. */
	private String _documentationDir;

	/**
	 * Ctor.
	 */
	public ApplicationFiles()
	{
		super();
		ApplicationArguments args = ApplicationArguments.getInstance();

		final String homeDir = args.getSquirrelHomeDirectory();
		_squirrelHomeDir = homeDir != null ? homeDir : System.getProperty(IJavaPropertyNames.USER_DIR);
		_squirrelPluginsDir = _squirrelHomeDir + File.separator + "plugins";
		_squirrelLibrariesDir = _squirrelHomeDir + File.separator + "lib";
		_documentationDir = _squirrelHomeDir + File.separator + "doc";

		_userSettingsDir = args.getUserSettingsDirectoryOverride();
		if (_userSettingsDir == null)
		{
			_userSettingsDir = System.getProperty(IJavaPropertyNames.USER_HOME)
											+ File.separator + ".squirrel-sql";
		}
		try
		{
			new File(_userSettingsDir).mkdirs();
		}
		catch (Exception ex)
		{
			System.out.println(
				"Error creating user settings directory: " + _userSettingsDir);
			System.out.println(ex.toString());
		}
		try
		{
			final File logsDir = getExecutionLogFile().getParentFile();
			logsDir.mkdirs();
		}
		catch (Exception ex)
		{
			System.out.println("Error creating logs directory");
			System.out.println(ex.toString());
		}
	}

	public File getUserSettingsDirectory()
	{
		return new File(_userSettingsDir);
	}

	public File getPluginsDirectory()
	{
		return new File(_squirrelPluginsDir);
	}

	/**
	 * @return file that contains database aliases.
	 */
	public File getDatabaseAliasesFile()
	{
		return new File(_userSettingsDir + File.separator + "SQLAliases.xml");
	}

	/**
	 * @return file that contains JDBC driver definitions.
	 */
	public File getDatabaseDriversFile()
	{
		return new File(_userSettingsDir + File.separator + "SQLDrivers.xml");
	}

	/**
	 * @return file that contains JDBC driver definitions.
	 */
	public File getUserPreferencesFile()
	{
		return new File(_userSettingsDir + File.separator + "prefs.xml");
	}

	/**
	 * @return file to log execution information to.
	 */
	public File getExecutionLogFile()
	{
		return new File(_userSettingsDir + File.separator + "logs"
							+ File.separator + "squirrel-sql.log");
	}

	/**
	 * @return file to log debug information to.
	 */
	public File getDebugLogFile()
	{
		return new File(_userSettingsDir + File.separator + "squirrel-sql-debug.log");
	}

	/**
	 * @return file to log JDBC debug information to.
	 */
	public File getJDBCDebugLogFile()
	{
		return new File(_userSettingsDir + File.separator +
							"squirrel-sql-jdbcdebug.log");
	}

	/**
	 * @return directory that contains plugin specific user settings
	 */
	public File getPluginsUserSettingsDirectory()
	{
		return new File(_userSettingsDir + File.separator + "plugins");
	}

	/**
	 * @return the quickstart guide.
	 */
	public File getQuickStartGuideFile()
	{
		return new File(_documentationDir + File.separator + "quick_start.html");
	}

	/**
	 * @return the FAQ.
	 */
	public File getFAQFile()
	{
		return new File(_documentationDir + File.separator + "faq.html");
	}

	/**
	 * @return the changelog.
	 */
	public File getChangeLogFile()
	{
		return new File(_documentationDir + File.separator + "changes.txt");
	}

	/**
	 * @return the licence file.
	 */
	public File getLicenceFile()
	{
		return new File(_documentationDir + File.separator + "licences/squirrel_licence.txt");
	}
}