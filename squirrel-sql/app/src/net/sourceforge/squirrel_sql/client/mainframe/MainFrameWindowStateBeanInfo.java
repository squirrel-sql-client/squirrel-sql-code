package net.sourceforge.squirrel_sql.client.mainframe;
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
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class MainFrameWindowStateBeanInfo extends SimpleBeanInfo {
    private static final Class s_actualClass = MainFrameWindowState.class;

    private static PropertyDescriptor[] s_dscrs;

    public MainFrameWindowStateBeanInfo() throws IntrospectionException {
        super();
        if (s_dscrs == null) {
            s_dscrs = new PropertyDescriptor[2];
            s_dscrs[0] = new PropertyDescriptor(MainFrameWindowState.IPropertyNames.ALIASES_WINDOW_LOCATION, s_actualClass, "getAliasesWindowLocation", "setAliasesWindowLocation");
            s_dscrs[1] = new PropertyDescriptor(MainFrameWindowState.IPropertyNames.DRIVERS_WINDOW_LOCATION, s_actualClass, "getDriversWindowLocation", "setDriversWindowLocation");
        }
    }

    public BeanInfo[] getAdditionalBeanInfo() {
        try {
            BeanInfo superBeanInfo = Introspector.getBeanInfo(s_actualClass.getSuperclass());
            return new BeanInfo[] {
                superBeanInfo
            };
        } catch(IntrospectionException ex) {
            return new BeanInfo[0];
        }
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        return s_dscrs;
    }
}
