package net.sourceforge.squirrel_sql.fw.util.beanwrapper;
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
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * This is the <CODE>BeanInfo</CODE> class for <CODE>PointWrapper</CODE>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class PointWrapperBeanInfo extends SimpleBeanInfo {

    private static PropertyDescriptor[] s_descriptors;

    public PointWrapperBeanInfo() throws IntrospectionException {
        super();
        if (s_descriptors == null) {
            s_descriptors = new PropertyDescriptor[2];
            s_descriptors[0] = new PropertyDescriptor(PointWrapper.IPropertyNames.X, PointWrapper.class, "getX", "setX");
            s_descriptors[1] = new PropertyDescriptor(PointWrapper.IPropertyNames.Y, PointWrapper.class, "getY", "setY");
        }
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        return s_descriptors;
    }
}

