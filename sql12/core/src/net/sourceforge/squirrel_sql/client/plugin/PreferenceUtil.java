/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.client.plugin;

import java.io.File;
import java.io.IOException;

import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactoryImpl;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class PreferenceUtil
{

	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(PreferenceUtil.class);

	/** Name of preferences file. */
	private static final String USER_PREFS_FILE_NAME = "prefs.xml";

	/** factory for creating FileWrappers which insulate the application from direct reference to File */
	private static FileWrapperFactory fileWrapperFactory = new FileWrapperFactoryImpl();

	/**
	 * Check to see if the user wants to migrate previous settings. If yes, then then oldSQuirreLInstallDir
	 * contains the user's latest settings so prefer that one, unless the user settings directory has a file
	 * that is newer.
	 * 
	 * @return
	 */
	public static FileWrapper getPreferenceFileToReadFrom(IPlugin p) throws IOException
	{
		FileWrapper userSettingsFolder = p.getPluginUserSettingsFolder();
		final FileWrapper newUserPreferenceFile =
			fileWrapperFactory.create(userSettingsFolder, USER_PREFS_FILE_NAME);

		FileWrapper result = newUserPreferenceFile;

		String migratePrefsProperty = System.getProperty("migratePreferences", "false");
		if (migratePrefsProperty != null && migratePrefsProperty.equalsIgnoreCase("true"))
		{
			String oldSquirrelLocation = System.getProperty("oldSQuirreLInstallDir");

			if (oldSquirrelLocation == null || oldSquirrelLocation.equals(""))
			{
				throw new IllegalStateException("migratePreferences was set to true, but "
					+ "oldSQuirreLInstallDir wasn't set.");
			}

			final FileWrapper oldAppPreferenceFile =
				fileWrapperFactory.create(oldSquirrelLocation + File.separator + "plugins" + File.separator
					+ p.getInternalName() + File.separator + USER_PREFS_FILE_NAME);

			// if the old preference file exists and is newer than the
			// newUserPreference file, then use it for reading preferences
			if (oldAppPreferenceFile.exists())
			{

				if (oldAppPreferenceFile.lastModified() > newUserPreferenceFile.lastModified())
				{
					result = oldAppPreferenceFile;
					s_log.info("-DmigratePreferences was specified; using "
						+ oldAppPreferenceFile.getAbsolutePath() + " as the source for preferences - will save "
						+ "them to " + newUserPreferenceFile.getAbsolutePath());
				}
				else
				{
					s_log.info("-DmigratePreferences was specified, but file "
						+ newUserPreferenceFile.getAbsolutePath() + " is newer " + "than "
						+ oldAppPreferenceFile.getAbsolutePath() + ": migration will be skipped");
				}

			}
			else
			{
				s_log.info("-DmigratePreferences was specified, but file "
					+ oldAppPreferenceFile.getAbsolutePath() + " does not "
					+ "exist! Please remove -DmigratePreferences from the "
					+ "launch script, or fix -DoldSquirrelLocation to "
					+ "point to a valid previous SQuirreL installation " + "directory");
			}
		}
		return result;
	}

}
