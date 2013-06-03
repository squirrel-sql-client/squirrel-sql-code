package org.squirrelsql.drivers;
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

import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;
import org.squirrelsql.services.MyURLClassLoader;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;


public class SQLDriverClassLoader extends MyURLClassLoader
{
   public SQLDriverClassLoader(SQLDriver sqlDriver) throws MalformedURLException
   {
      super(createURLs(sqlDriver.getName(), sqlDriver.getJarFileNamesList()));
   }

   public SQLDriverClassLoader(List<URL> urls)
   {
      super(urls);
   }

   public SQLDriverClassLoader(URL url)
   {
      super(url);
   }

   public ArrayList<Class> getDriverClasses()
   {
      ArrayList<Class> classes = getAssignableClasses(Driver.class);
      ArrayList<Class> list = new ArrayList<>();

      for (Class clazz : classes)
      {
         if (!Modifier.isAbstract(clazz.getModifiers()))
         {
            list.add(clazz);
         }
      }
      return list;
   }

   private static List<URL> createURLs(String driverName, List<String> fileNames)
         throws MalformedURLException
   {
      MessageHandler mh = new MessageHandler(SQLDriverClassLoader.class, MessageHandlerDestination.MESSAGE_PANEL);

      ArrayList<URL> urls = new ArrayList<>();


      for (String fileName : fileNames)
      {
         File f = new File(fileName);
         if (!f.exists())
         {
            mh.warning(
                  "For driver '" + driverName + "', the JVM says file doesn't exist: " +
                        fileName);
         }
         if (f.isDirectory())
         {
            mh.warning(
                  "For driver '" + driverName + "', the JVM says the file is a directory: " +
                        fileName);
         }
         if (!f.canRead())
         {
            mh.warning(
                  "For driver '" + driverName + "', the JVM says the file can't be read: " +
                        fileName);
         }

         urls.add(f.toURI().toURL());

      }

      return urls;
   }
}
