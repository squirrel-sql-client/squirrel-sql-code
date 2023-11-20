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

import net.sourceforge.squirrel_sql.fw.resources.Resources;

import java.net.URL;

public class SquirrelResources extends Resources
{
	public final static int S_SPLASH_IMAGE_BACKGROUND = 0xAEB0C5;

	public static final String BUNDLE_BASE_NAME = "net.sourceforge.squirrel_sql.client.resources.squirrel";
	
	private final String _defaultsPath;

   public interface IMenuResourceKeys
	{
		String ALIASES = "aliases";
		String RESULTS_TABS = "result_tabs";
		String DRIVERS = "drivers";
//		String EDIT = "edit";
		String APP_FILE_MENU = "appFileMenu";
		String HELP = "help";
		String PLUGINS = "plugins";
		String PLUGIN_CHANGE_LOG = "pluginChangeLog";
		String PLUGIN_HELP = "pluginHelp";
		String PLUGIN_LICENCE = "pluginLicence";
		String SESSION = "session";
		String WINDOWS = "windows";
      String FILE = "file";
      String SAVED_SESSION = "savedSession";
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
		String SPLASH_SCREEN_LESS_HIGH = "SplashScreenLessHigh";


		String VIEW = "View";

		String TRASH = "trash";

		String GREEN_GEM = "green_gem";
		String YELLOW_GEM = "yellow_gem";
		String RED_GEM = "red_gem";
		String WHITE_GEM = "white_gem";
		String LOGS = "logs";
		String ALIAS_PROPERTIES = "aliasProperties";

      String FIND = "find";
      String FIND_COLUMN = "findColumn";

      String DUPLICATE = "duplicate";
      String DUPLICATE_VALUES_IN_COLUMNS = "duplicateValuesInColumns";
      String DUPLICATE_VALUES_IN_COLUMNS_IF_CONSECUTIVE = "duplicateValuesInColumnsIfConsecutive";
      String DUPLICATE_ROWS = "duplicateRows";
      String DUPLICATE_ROWS_IF_CONSECUTIVE = "duplicateRowsIfConsecutive";
		String DUPLICATE_CELLS_IN_ROW = "duplicateCellsInRow";
		String DUPLICATE_CONSECUTIVE_CELLS_IN_ROW = "duplicateCellsInRowIfConsecutive";

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
      String SMALL_FILE_INTERNAL = "smallFileInternal";
      String SMALL_FILE_INTERNAL_CHANGED = "smallFileInternalChanged";

		String SMALL_REFRESH = "smallRefresh";

		String SMALL_CHOOSE_SCHEMA = "smallChooseSchema";

		String SMALL_PLUS = "smallPlus";
		String SMALL_MINUS = "smallMinus";

		String SMALL_INFO = "small_info";

		String LABELED = "labeled";
      String NAMED = "named";

      String TAB_DETACH = "tabDetach";
      String TAB_DETACH_SMALL = "tabDetachSmall";
      String TAB_DETACH_SMALL_REVERT = "tabDetachSmallRevert";

      String AGG_COUNT = "aggCount";
      String AGG_SUM = "aggSum";
      String AGG_XY = "aggXy";
      String AGG_MIN = "aggMin";
      String AGG_MAX = "aggMax";

      String ARROW_DOWN = "next_nav";
      String ARROW_UP = "prev_nav";

      String UNMARK = "unmark";
      String TABLE = "table";
      String HIDE = "hide";
      String LOADING_GIF = "loading_gif";

      String DIR_GIF = "dir_obj";
      String FILE_GIF = "file_obj";

      String ADD_TAB = "add_tab";
      String NEW_SQL_WORKSHEET = "new_sql_worksheet";

      String PLUS = "plus";
      String MINUS = "minus";
		String FILE_ARROW = "file_arrow";

		String UNDEFINED = "undefined";

		String REVERT = "revert";

		String COPY = "copy";

		String CHANGE_TRACK_MANUAL = "changeTrackManual";
		String CHANGE_TRACK_FILE = "changeTrackFile";
		String CHANGE_TRACK_GIT = "changeTrackGit";

		String BOLD = "bold";
		String ITALIC = "italic";
		String PEN = "pen";
		String FILL = "fill";

		String SAVE = "save";
		String OPEN = "open";
		String MAGIC_WAND = "magic_wand";

		String LOCK = "lock";
		String UNLOCK = "unlock";

		String SAVE_ALL = "save_all";

		String STOP = "stop";
		String COLOR_CHOOSE = "colorChoose";

		String VIEW_DETAILS = "view_details";

		String PERCENT = "percent";
		String PERCENT_NEGATED = "percent_negated";

		String COPY_SQL = "copySQL";
		String SQL = "sql";

		String SESSION_SAVE = "session_save";
		String SESSION_OPEN = "session_open";
		String SESSION_MANAGE = "session_manage";

		String SMALL_COPY_PASSWORD = "small_copy_password";
		String SMALL_SHOW_PASSWORD = "small_show_password";

		String EDIT = "edit";
		String EDIT_RED_DOT = "editRedDot";

		String THREE_DOTS = "threedots";
		String THREE_DOTS_CHECKED = "threedots_checked";

		String DELETE = "delete";

		String ALIAS_CONNECT = "aliasConnect";
		String ALIAS_MODIFY = "aliasModify";

		String CLIPBOARD = "clipboard";

	}

	public SquirrelResources(String rsrcBundleBaseName)
		throws IllegalArgumentException
	{
		super(rsrcBundleBaseName, SquirrelResources.class.getClassLoader());
		_defaultsPath = getBundleHandler().getString("path.defaults");
	}

	public URL getDefaultDriversUrl()
	{
		return getClass().getResource(_defaultsPath + "default_drivers.xml");
	}

	public URL getCreditsURL()
	{
		return getClass().getResource(getBundleHandler().getString("Credits.file"));
	}
}
