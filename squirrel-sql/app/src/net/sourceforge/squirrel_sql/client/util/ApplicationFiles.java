package net.sourceforge.squirrel_sql.client.util;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.io.File;

import net.sourceforge.squirrel_sql.fw.util.IJavaPropertyNames;
import net.sourceforge.squirrel_sql.fw.util.Logger;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.IApplication;

public class ApplicationFiles {
	/** Name of directory to contain users settings. */
	private String _userSettingsDir;

	/** Name of folder that contains Squirrel app. */
	public static final String SQUIRREL_FOLDER = System.getProperty(IJavaPropertyNames.USER_DIR);

	/** Name of folder that contains Squirrel libraries. */
	public static final String SQUIRREL_LIB_FOLDER = SQUIRREL_FOLDER + File.separator + "lib";

	/** Name of folder that contains plugins. */
	public static final String SQUIRREL_PLUGINS_FOLDER = SQUIRREL_FOLDER + File.separator + "plugins";

	/**
	 * Ctor.
	 * 
	 * @param	app	Application API
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>IApplication</TT>
	 * 			passed in.
	 */
	public ApplicationFiles(IApplication app)
			throws IllegalArgumentException {
		super();
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
		
		ApplicationArguments args = app.getArguments();
		
		_userSettingsDir = args.getUserSettingsDirectoryOverride();
		if (_userSettingsDir == null) {
			_userSettingsDir = System.getProperty(IJavaPropertyNames.USER_HOME) +
									File.separator + ".squirrel-sql";
		}
		try {
			new File(_userSettingsDir).mkdirs();
		} catch (Exception ex) {
			app.getLogger().showMessage(Logger.ILogTypes.ERROR,
				"Error creating user settings directory: " + _userSettingsDir);
			app.getLogger().showMessage(Logger.ILogTypes.ERROR, ex);
		}
	}
	
	public File getUserSettingsDirectory() {
		return new File(_userSettingsDir);
	}
	
	/**
	 * @return file that contains database aliases.
	 */
	public File getDatabaseAliasesFile() {
		return new File(_userSettingsDir + File.separator + "SQLAliases.xml");
	}
	
	/**
	 * @return file that contains JDBC driver definitions.
	 */
	public File getDatabaseDriversFile() {
		return new File(_userSettingsDir + File.separator + "SQLDrivers.xml");
	}
	
	/**
	 * @return file that contains JDBC driver definitions.
	 */
	public File getUserPreferencesFile() {
		return new File(_userSettingsDir + File.separator + "prefs.xml");
	}
	
	/**
	 * @return file to log execution information to.
	 */
	public File getExecutionLogFile() {
		return new File(_userSettingsDir + File.separator + "squirrel-sql.log");
	}
	
	/**
	 * @return file to log debug information to.
	 */
	public File getDebugLogFile() {
		return new File(_userSettingsDir + File.separator + "squirrel-sql-debug.log");
	}
	
	/**
	 * @return file to log JDBC debug information to.
	 */
	public File getJDBCDebugLogFile() {
		return new File(_userSettingsDir + File.separator + "squirrel-sql-jdbcdebug.log");
	}
	
	/**
	 * @return directory that contains plugin specific user settings
	 */
	public File getPluginsUserSettingsDirectory() {
		return new File(_userSettingsDir + File.separator + "plugins");
	}
}
