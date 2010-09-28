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

import java.io.File;
import java.io.IOException;
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


      File xmlFile = new File(pluginUserSettingsFolder.getPath(), HibernateConfigController.HIBERNATE_CONFIGS_XML_FILE);

      if (false == xmlFile.exists())
      {
         return null;
      }

      try
      {
         reader.load(xmlFile, plugin.getClass().getClassLoader());
         return reader;
      }
      catch (Exception e)
      {
         String message =
               "Cold not load Hibernate configuration. " +
               "ClassNotFoundException may be thrown when a version higher then 3.1.x is used for first time. " +
               "Please define your Hibernate configurations again or edit\n" +
               plugin.getPluginUserSettingsFolder() + File.separator + HibernateConfigController.HIBERNATE_CONFIGS_XML_FILE +
               "\nand replace\n" +
               "net.sourceforge.squirrel_sql.plugins.hibernate.configuration.HibernateConfiguration\n" +
               "with\n" +
               "net.sourceforge.squirrel_sql.plugins.hibernate.server.HibernateConfiguration\n" +
               "Sorry for the inconvenience.";
         s_log.error(message, e);
         plugin.getApplication().getMessageHandler().showErrorMessage(e + "\n" + message);

         return null;
      }



   }

   public static String getSimpleClassName(String mappedClassName)
   {
      String[] cpTokens = mappedClassName.split("\\.");
      return cpTokens[cpTokens.length - 1];
   }
}
