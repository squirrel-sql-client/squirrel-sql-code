package net.sourceforge.squirrel_sql.client.preferences;
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
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * This is the <CODE>BeanInfo</CODE> class for <CODE>SquirrelPreferences</CODE>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SquirrelPreferencesBeanInfo extends SimpleBeanInfo {

    private static PropertyDescriptor[] s_dscrs;

    private static Class cls = SquirrelPreferences.class;

    public SquirrelPreferencesBeanInfo() throws IntrospectionException {
        super();
        if (s_dscrs == null) {
            s_dscrs = new PropertyDescriptor[7];
            int idx = 0;
            s_dscrs[idx++] = new PropertyDescriptor(SquirrelPreferences.IPropertyNames.SESSION_PROPERTIES, cls, "getSessionProperties", "setSessionProperties");
            s_dscrs[idx++] = new PropertyDescriptor(SquirrelPreferences.IPropertyNames.MAIN_FRAME_STATE, cls, "getMainFrameWindowState", "setMainFrameWindowState");
            s_dscrs[idx++] = new PropertyDescriptor(SquirrelPreferences.IPropertyNames.SHOW_CONTENTS_WHEN_DRAGGING, cls, "getShowContentsWhenDragging", "setShowContentsWhenDragging");
            s_dscrs[idx++] = new PropertyDescriptor(SquirrelPreferences.IPropertyNames.LOGIN_TIMEOUT, cls, "getLoginTimeout", "setLoginTimeout");
            s_dscrs[idx++] = new PropertyDescriptor(SquirrelPreferences.IPropertyNames.DEBUG_JDBC, cls, "getDebugJdbc", "setDebugJdbc");
            s_dscrs[idx++] = new PropertyDescriptor(SquirrelPreferences.IPropertyNames.DEBUG_MODE, cls, "isDebugMode", "setDebugMode");
            s_dscrs[idx++] = new PropertyDescriptor(SquirrelPreferences.IPropertyNames.SHOW_TOOLTIPS , cls, "getShowToolTips", "setShowToolTips");
        }
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        return s_dscrs;
    }
}


