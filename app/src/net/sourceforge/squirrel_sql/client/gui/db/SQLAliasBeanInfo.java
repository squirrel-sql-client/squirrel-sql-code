package net.sourceforge.squirrel_sql.client.gui.db;
/*
 * Copyright (C) 2001-2003 Colin Bell
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

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * This is the <CODE>BeanInfo</CODE> class for <CODE>SQLAlias</CODE>.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLAliasBeanInfo extends SimpleBeanInfo
{
   /**
    * If more than one thread is constructing, volatile tells them to check an 
    * otherwise cached value.
    */ 
   private static volatile PropertyDescriptor[] s_desc;
   private static Class<SQLAlias> CLAZZ = net.sourceforge.squirrel_sql.client.gui.db.SQLAlias.class;

   private interface IPropNames extends ISQLAlias.IPropertyNames
   {
      // Empty body.
   }

   public SQLAliasBeanInfo() throws IntrospectionException
   {
      super();
      if (s_desc == null)
      {
         s_desc = new PropertyDescriptor[]
            {
               new PropertyDescriptor(IPropNames.ID, CLAZZ, "getIdentifier", "setIdentifier"),
               new PropertyDescriptor(IPropNames.NAME, CLAZZ, "getName", "setName"),
               new PropertyDescriptor(IPropNames.URL, CLAZZ, "getUrl", "setUrl"),
               new PropertyDescriptor(IPropNames.USER_NAME, CLAZZ, "getUserName", "setUserName"),
               new PropertyDescriptor(IPropNames.DRIVER, CLAZZ, "getDriverIdentifier", "setDriverIdentifier"),
               new PropertyDescriptor(IPropNames.USE_DRIVER_PROPERTIES, CLAZZ, "getUseDriverProperties", "setUseDriverProperties"),
               new PropertyDescriptor(IPropNames.DRIVER_PROPERTIES, CLAZZ, "getDriverPropertiesClone", "setDriverProperties"),
               new PropertyDescriptor(IPropNames.PASSWORD, CLAZZ, "getPassword", "setPassword"),
               new PropertyDescriptor(IPropNames.AUTO_LOGON, CLAZZ, "isAutoLogon", "setAutoLogon"),
               new PropertyDescriptor(IPropNames.CONNECT_AT_STARTUP, CLAZZ, "isConnectAtStartup", "setConnectAtStartup"),
               new PropertyDescriptor(IPropNames.SCHEMA_PROPERTIES, CLAZZ, "getSchemaProperties", "setSchemaProperties")
            };
      }
   }

   public PropertyDescriptor[] getPropertyDescriptors()
   {
      return s_desc;
   }
}

