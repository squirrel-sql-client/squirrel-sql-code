package net.sourceforge.squirrel_sql.client.resources;
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
import java.net.URL;

import net.sourceforge.squirrel_sql.fw.util.Resources;

public class SquirrelResources extends Resources
{
	public final static int S_SPLASH_IMAGE_BACKGROUND = 0xAEB0C5;

	public static final String BUNDLE_BASE_NAME = "net.sourceforge.squirrel_sql.client.resources.squirrel";
	
	private final String _defaultsPath;

	public interface IMenuResourceKeys
	{
		String ALIASES = "aliases";
		String CLOSE_ALL_SQL_RESULTS = "close_all_sql_results";
		String DRIVERS = "drivers";
//		String EDIT = "edit";
		String OSX_FILE = "osxFile";
		String HELP = "help";
		String PLUGINS = "plugins";
		String PLUGIN_CHANGE_LOG = "pluginChangeLog";
		String PLUGIN_HELP = "pluginHelp";
		String PLUGIN_LICENCE = "pluginLicence";
		String SESSION = "session";
		String WINDOWS = "windows";
      String FILE = "file";
      String TRANSACTION = "transaction";
   }

	public interface IImageNames
	{
		String APPLICATION_ICON = "AppIcon";
		String COPY_SELECTED = "CopySelected";
      String SQL_HISTORY = "SQLHistory";
      String EMPTY16 = "Empty16";
		String HELP_TOPIC = "HelpTopic";
		String HELP_TOC_CLOSED = "HelpTocClosed";
		String HELP_TOC_OPEN = "HelpTocOpen";
		String PERFORMANCE_WARNING = "PerformanceWarning";
		String PLUGINS = "Plugins";
		String SPLASH_SCREEN = "SplashScreen";
		String VIEW = "View";

		String TRASH = "trash";

		String GREEN_GEM = "green_gem";
		String YELLOW_GEM = "yellow_gem";
		String RED_GEM = "red_gem";
		String WHITE_GEM = "white_gem";
		String LOGS = "logs";
		String ALIAS_PROPERTIES = "aliasProperties";

      String FIND = "find";
      String FILTER = "filter";

      String AUTOHIDE_ON = "autohideOn";
      String AUTOHIDE_OFF = "autohideOff";
      String MINIMIZE = "minimize";

      String PREV_SCALE = "prevScale";
      String NEXT_SCALE = "nextScale";
      String RERUN = "rerun";
      String CLOSE = "close";

      String SMALL_FILE = "smallFile";
      String SMALL_FILE_CHANGED = "smallFileChanged";

      String TAB_DETACH = "tabDetach";
      String TAB_DETACH_SMALL = "tabDetachSmall";

      String AGG_COUNT = "aggCount";
      String AGG_SUM = "aggSum";
      String AGG_XY = "aggXy";
      String AGG_MIN = "aggMin";
      String AGG_MAX = "aggMax";
   }

	public SquirrelResources(String rsrcBundleBaseName)
		throws IllegalArgumentException
	{
		super(rsrcBundleBaseName, SquirrelResources.class.getClassLoader());
		_defaultsPath = getBundle().getString("path.defaults");
	}

	public URL getDefaultDriversUrl()
	{
		return getClass().getResource(_defaultsPath + "default_drivers.xml");
	}

	public URL getCreditsURL()
	{
		return getClass().getResource(getBundle().getString("Credits.file"));
	}
}
