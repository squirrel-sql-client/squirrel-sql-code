package net.sourceforge.squirrel_sql.client.preferences;
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
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
/**
 * This is the <CODE>BeanInfo</CODE> class for <CODE>SquirrelPreferences</CODE>.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SquirrelPreferencesBeanInfo extends SimpleBeanInfo
	implements SquirrelPreferences.IPropertyNames
{
	private static PropertyDescriptor[] s_dscrs;

	private static Class CLS = SquirrelPreferences.class;

	public SquirrelPreferencesBeanInfo() throws IntrospectionException
	{
		super();
		if (s_dscrs == null)
		{
			s_dscrs = new PropertyDescriptor[20];
			s_dscrs[0] = new PropertyDescriptor(SESSION_PROPERTIES, CLS,
									"getSessionProperties", "setSessionProperties");
			s_dscrs[1] = new PropertyDescriptor(MAIN_FRAME_STATE, CLS,
								"getMainFrameWindowState", "setMainFrameWindowState");
			s_dscrs[2] = new PropertyDescriptor(SHOW_CONTENTS_WHEN_DRAGGING, CLS,
								"getShowContentsWhenDragging", "setShowContentsWhenDragging");
			s_dscrs[3] = new PropertyDescriptor(LOGIN_TIMEOUT, CLS,
								"getLoginTimeout", "setLoginTimeout");
			s_dscrs[4] = new PropertyDescriptor(JDBC_DEBUG_TYPE, CLS,
									"getJdbcDebugType", "setJdbcDebugType");
			s_dscrs[5] = new PropertyDescriptor(SHOW_MAIN_STATUS_BAR, CLS,
								"getShowMainStatusBar", "setShowMainStatusBar");
			s_dscrs[6] = new PropertyDescriptor(SHOW_MAIN_TOOL_BAR, CLS,
								"getShowMainToolBar", "setShowMainToolBar");
			s_dscrs[7] = new PropertyDescriptor(SHOW_ALIASES_TOOL_BAR, CLS,
								"getShowAliasesToolBar", "setShowAliasesToolBar");
			s_dscrs[8] = new PropertyDescriptor(SHOW_DRIVERS_TOOL_BAR, CLS,
								"getShowDriversToolBar", "setShowDriversToolBar");
			s_dscrs[9] = new PropertyDescriptor(SHOW_TOOLTIPS, CLS,
								"getShowToolTips", "setShowToolTips");
			s_dscrs[10] = new PropertyDescriptor(SCROLLABLE_TABBED_PANES, CLS,
								"useScrollableTabbedPanes", "setUseScrollableTabbedPanes");
			s_dscrs[11] = new IndexedPropertyDescriptor(ACTION_KEYS, CLS,
									"getActionKeys", "setActionKeys",
									"getActionKeys", "setActionKeys");
			s_dscrs[12] = new PropertyDescriptor(PROXY, CLS,
								"getProxySettings", "setProxySettings");
			s_dscrs[13] = new PropertyDescriptor(ALIASES_SELECTED_INDEX, CLS,
								"getAliasesSelectedIndex", "setAliasesSelectedIndex");
			s_dscrs[14] = new PropertyDescriptor(DRIVERS_SELECTED_INDEX, CLS,
								"getDriversSelectedIndex", "setDriversSelectedIndex");
			s_dscrs[15] = new PropertyDescriptor(SHOW_LOADED_DRIVERS_ONLY, CLS,
								"getShowLoadedDriversOnly", "setShowLoadedDriversOnly");
			s_dscrs[16] = new PropertyDescriptor(MAXIMIMIZE_SESSION_SHEET_ON_OPEN, CLS,
								"getMaximizeSessionSheetOnOpen", "setMaximizeSessionSheetOnOpen");
			s_dscrs[17] = new PropertyDescriptor(SHOW_COLOR_ICONS_IN_TOOLBAR, CLS,
								"getShowColoriconsInToolbar", "setShowColoriconsInToolbar");
 			s_dscrs[18] = new PropertyDescriptor(FIRST_RUN, CLS,
								"isFirstRun", "setFirstRun");
 			s_dscrs[19] = new PropertyDescriptor(CONFIRM_SESSION_CLOSE, CLS,
					"getConfirmSessionClose", "setConfirmSessionClose");
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_dscrs;
	}
}
