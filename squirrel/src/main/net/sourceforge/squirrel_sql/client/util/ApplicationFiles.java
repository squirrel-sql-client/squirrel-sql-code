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

public class ApplicationFiles {
    /** Name of folder to contain users settings. */
    public static final String USER_SETTINGS_FOLDER =
                            System.getProperty(IJavaPropertyNames.USER_HOME) +
                            File.separator + ".squirrel-sql";

    /** Name of folder that contains Squirrel app. */
    public static final String SQUIRREL_FOLDER = System.getProperty(IJavaPropertyNames.USER_DIR);

    /** Name of folder that contains Squirrel libraries. */
    public static final String SQUIRREL_LIB_FOLDER = SQUIRREL_FOLDER + File.separator + "lib";

    /** Name of folder that contains plugins. */
    public static final String SQUIRREL_PLUGINS_FOLDER = SQUIRREL_FOLDER + File.separator + "plugins";

    /** Name of file that contains database aliases. */
    public static final String USER_ALIAS_FILE_NAME = USER_SETTINGS_FOLDER + File.separator + "SQLAliases.xml";

    /** Name of file that contains user settings. */
    public static final String USER_PREFS_FILE_NAME = USER_SETTINGS_FOLDER + File.separator + "prefs.xml";

    /** Name of file that contains users database driver information. */
    public static final String USER_DRIVER_FILE_NAME = USER_SETTINGS_FOLDER + File.separator + "SQLDrivers.xml";

    /** Flle to log execution information to. */
    public static final String EXECUTION_LOG_FILE = USER_SETTINGS_FOLDER + File.separator + "squirrel-sql.log";
    //private static ApplicationFiles _instance = new ApplicationFiles();

    /** Name of folder that contains plugin specific user settings. */
    public static final String PLUGINS_USER_SETTINGS_FOLDER = USER_SETTINGS_FOLDER + File.separator + "plugins";

    static {
        new File(USER_SETTINGS_FOLDER).mkdir();
    }

    private ApplicationFiles() {
        super();
    }
}
