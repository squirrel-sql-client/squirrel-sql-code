package org.firebirdsql.squirrel.util;
/*
 * Copyright (C) 2002 Colin Bell
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
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
/**
 * This is the <CODE>BeanInfo</CODE> class for <CODE>IndexInfo</CODE>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class IndexInfoBeanInfo extends SimpleBeanInfo
{
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(IndexInfoBeanInfo.class);

    private static final Class<IndexInfo> CLAZZ = IndexInfo.class;

    private static interface IPropertyNames extends IndexInfo.IPropertyNames
    {
        // Empty body.
    }
    
    private static PropertyDescriptor[] s_descr;

    public IndexInfoBeanInfo() throws IntrospectionException
    {
        super();
        if (s_descr == null)
        {
            s_descr = new PropertyDescriptor[10];
            s_descr[0] = new PropertyDescriptor(IPropertyNames.NAME, CLAZZ);
            s_descr[0].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.name"));
            s_descr[1] = new PropertyDescriptor(IPropertyNames.DESCRIPTION, CLAZZ);
            s_descr[1].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.description"));
            s_descr[2] = new PropertyDescriptor(IPropertyNames.RELATION_NAME, CLAZZ);
            s_descr[2].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.relationname"));
            s_descr[3] = new PropertyDescriptor(IPropertyNames.ID, CLAZZ);
            s_descr[3].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.id"));
            s_descr[4] = new PropertyDescriptor(IPropertyNames.UNIQUE, CLAZZ);
            s_descr[4].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.unique"));
            s_descr[5] = new PropertyDescriptor(IPropertyNames.SEGMENT_COUNT, CLAZZ);
            s_descr[5].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.segmentcount"));
            s_descr[6] = new PropertyDescriptor(IPropertyNames.ACTIVE, CLAZZ);
            s_descr[6].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.active"));
            s_descr[7] = new PropertyDescriptor(IPropertyNames.EXPRESSION_SOURCE, CLAZZ);
            s_descr[7].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.expressionsource"));
            s_descr[8] = new PropertyDescriptor(IPropertyNames.FOREIGN_KEY_CONSTRAINT, CLAZZ);
            s_descr[8].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.foreignkeyconstraint"));
            s_descr[9] = new PropertyDescriptor(IPropertyNames.SYSTEM_DEFINED, CLAZZ);
            s_descr[9].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.systemdefined"));
        }
    }

    public PropertyDescriptor[] getPropertyDescriptors()
    {
        return s_descr;
    }
}

