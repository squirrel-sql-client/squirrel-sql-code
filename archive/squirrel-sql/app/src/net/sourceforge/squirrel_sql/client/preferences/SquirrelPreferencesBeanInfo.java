package net.sourceforge.squirrel_sql.client.preferences;
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
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * This is the <CODE>BeanInfo</CODE> class for <CODE>SquirrelPreferences</CODE>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SquirrelPreferencesBeanInfo
	extends SimpleBeanInfo
	implements SquirrelPreferences.IPropertyNames
{
	private static PropertyDescriptor[] s_dscrs;

	private static Class CLS = SquirrelPreferences.class;

	public SquirrelPreferencesBeanInfo() throws IntrospectionException
	{
		super();
		if (s_dscrs == null)
		{
			s_dscrs = new PropertyDescriptor[13];
			int idx = 0;
			s_dscrs[idx++] = new PropertyDescriptor(SESSION_PROPERTIES, CLS,
									"getSessionProperties", "setSessionProperties");
			s_dscrs[idx++] = new PropertyDescriptor(MAIN_FRAME_STATE, CLS,
								"getMainFrameWindowState", "setMainFrameWindowState");
			s_dscrs[idx++] = new PropertyDescriptor(SHOW_CONTENTS_WHEN_DRAGGING, CLS,
								"getShowContentsWhenDragging", "setShowContentsWhenDragging");
			s_dscrs[idx++] = new PropertyDescriptor(LOGIN_TIMEOUT, CLS,
								"getLoginTimeout", "setLoginTimeout");
			s_dscrs[idx++] = new PropertyDescriptor(DEBUG_JDBC, CLS,
									"getDebugJdbc", "setDebugJdbc");
			s_dscrs[idx++] = new PropertyDescriptor(SHOW_MAIN_STATUS_BAR, CLS,
								"getShowMainStatusBar", "setShowMainStatusBar");
			s_dscrs[idx++] = new PropertyDescriptor(SHOW_MAIN_TOOL_BAR, CLS,
								"getShowMainToolBar", "setShowMainToolBar");
			s_dscrs[idx++] = new PropertyDescriptor(SHOW_ALIASES_TOOL_BAR, CLS,
								"getShowAliasesToolBar", "setShowAliasesToolBar");
			s_dscrs[idx++] = new PropertyDescriptor(SHOW_DRIVERS_TOOL_BAR, CLS,
								"getShowDriversToolBar", "setShowDriversToolBar");
			s_dscrs[idx++] = new PropertyDescriptor(SHOW_TOOLTIPS, CLS,
								"getShowToolTips", "setShowToolTips");
			s_dscrs[idx++] = new PropertyDescriptor(SCROLLABLE_TABBED_PANES, CLS,
								"useScrollableTabbedPanes", "setUseScrollableTabbedPanes");
			s_dscrs[idx++] = new IndexedPropertyDescriptor(ACTION_KEYS, CLS,
									"getActionKeys", "setActionKeys",
									"getActionKeys", "setActionKeys");
			s_dscrs[idx++] = new PropertyDescriptor(PROXY, CLS,
								"getProxySettings", "setProxySettings");
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_dscrs;
	}
}