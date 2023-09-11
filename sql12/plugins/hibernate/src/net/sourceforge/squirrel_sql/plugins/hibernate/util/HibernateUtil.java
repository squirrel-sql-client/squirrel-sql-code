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

import net.sourceforge.squirrel_sql.fw.util.*;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernatePlugin;
import net.sourceforge.squirrel_sql.plugins.hibernate.configuration.HibernateConfigController;

import java.io.*;

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

	/** factory for creating FileWrappers which insulate the application from direct reference to File */
	private static FileWrapperFactory fileWrapperFactory = new FileWrapperFactoryImpl();


   private static IOUtilities ioutils = new IOUtilitiesImpl();
   public static final String OBJECT_IS_NULL = "<object is null>";
   public static final String UNITIALIZED_PERSISTENT_COLLECTION = "<unitialized persistent collection>";

   public static XMLBeanReader createHibernateConfigsReader(HibernatePlugin plugin)
         throws IOException, XMLException
   {
      XMLBeanReader reader = new XMLBeanReader();
      FileWrapper pluginUserSettingsFolder = plugin.getPluginUserSettingsFolder();


      FileWrapper xmlFile = getXmlFile(pluginUserSettingsFolder);


      if (false == xmlFile.exists())
      {
         return null;
      }

      reader.load(xmlFile, plugin.getClass().getClassLoader());
      return reader;
   }

   private static FileWrapper getXmlFile(FileWrapper pluginUserSettingsFolder)
   {
   	FileReader fr = null;
   	BufferedReader br = null;
   	FileWriter fw = null;
   	PrintWriter pw = null;
   	
   	FileWrapper xmlFile = null;
   	
      try
      {
			FileWrapper xmlFileOld =
				fileWrapperFactory.create(pluginUserSettingsFolder,
					HibernateConfigController.HIBERNATE_CONFIGS_XML_FILE_OLD);
			xmlFile =
				fileWrapperFactory.create(pluginUserSettingsFolder,
					HibernateConfigController.HIBERNATE_CONFIGS_XML_FILE);

         if(xmlFileOld.exists() && false == xmlFile.exists())
         {
            fr = xmlFileOld.getFileReader();
            br = new BufferedReader(fr);

            fw = xmlFile.getFileWriter();
            pw = new PrintWriter(fw);

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
      }
      catch (Exception e)
      {
			s_log.error("Unexpected exception while attempting to get hibernate config xml file ("
				+ HibernateConfigController.HIBERNATE_CONFIGS_XML_FILE + ") " + "from settings directory ("
				+ pluginUserSettingsFolder);
			throw new RuntimeException(e);
      } finally {
      	ioutils.closeReader(br);
      	ioutils.closeReader(fr);
      	ioutils.flushWriter(pw);
      	ioutils.flushWriter(fw);
      	ioutils.closeWriter(pw);
      	ioutils.closeWriter(fw);
      }
      
      return xmlFile;
   }

   public static String getSimpleClassName(String mappedClassName)
   {
      String[] cpTokens = mappedClassName.split("\\.");
      return cpTokens[cpTokens.length - 1];
   }
   
	/**
	 * @param fileWrapperFactory the fileWrapperFactory to set
	 */
	public static void setFileWrapperFactory(FileWrapperFactory fileWrapperFactory)
	{
		Utilities.checkNull("setFileWrapperFactory", "fileWrapperFactory", fileWrapperFactory);
		HibernateUtil.fileWrapperFactory = fileWrapperFactory;
	}

}
