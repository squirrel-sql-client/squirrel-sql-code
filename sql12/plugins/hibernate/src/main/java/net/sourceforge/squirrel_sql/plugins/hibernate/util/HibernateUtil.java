/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.plugins.hibernate.util;

import java.io.*;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernatePlugin;
import net.sourceforge.squirrel_sql.plugins.hibernate.configuration.HibernateConfigController;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.DialectFactory;

/**
 * Class that provides utility methods for obtaining SessionFactory objects.
 *
 * @author manningr
 */
public class HibernateUtil
{

   /**
    * Logger for this class.
    */
   private static final ILogger s_log =
         LoggerController.createLogger(HibernateUtil.class);

   public static XMLBeanReader createHibernateConfigsReader(HibernatePlugin plugin)
         throws IOException, XMLException
   {
      XMLBeanReader reader = new XMLBeanReader();
      File pluginUserSettingsFolder = plugin.getPluginUserSettingsFolder();


      File xmlFile = getXmlFile(pluginUserSettingsFolder);


      if (false == xmlFile.exists())
      {
         return null;
      }

      reader.load(xmlFile, plugin.getClass().getClassLoader());
      return reader;
   }

   private static File getXmlFile(File pluginUserSettingsFolder)
   {
      try
      {
         File xmlFileOld = new File(pluginUserSettingsFolder.getPath(), HibernateConfigController.HIBERNATE_CONFIGS_XML_FILE_OLD);
         File xmlFile = new File(pluginUserSettingsFolder.getPath(), HibernateConfigController.HIBERNATE_CONFIGS_XML_FILE);


         if(xmlFileOld.exists() && false == xmlFile.exists())
         {
            FileReader fr = new FileReader(xmlFileOld);
            BufferedReader br = new BufferedReader(fr);

            FileWriter fw = new FileWriter(xmlFile);
            PrintWriter pw = new PrintWriter(fw);

            String line = br.readLine();
            while(null != line)
            {
               String s =
               line.replaceAll("net.sourceforge.squirrel_sql.plugins.hibernate.configuration.HibernateConfiguration",
                     "net.sourceforge.squirrel_sql.plugins.hibernate.server.HibernateConfiguration");

               pw.println(s);

               line = br.readLine();
            }

            br.close();
            fr.close();

            pw.flush();
            fw.flush();

            pw.close();
            fw.close();
         }


         return xmlFile;
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public static String getSimpleClassName(String mappedClassName)
   {
      String[] cpTokens = mappedClassName.split("\\.");
      return cpTokens[cpTokens.length - 1];
   }
}
