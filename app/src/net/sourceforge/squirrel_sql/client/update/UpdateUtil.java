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
package net.sourceforge.squirrel_sql.client.update;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;

import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ReleaseXmlBean;

public interface UpdateUtil {

   /**
    * The protocol we expect the update site to be using. Only HTTP at the
    * moment
    */
   public static final String HTTP_PROTOCOL_PREFIX = "http";

   /**
    * where we expect to find release.xml, which describes what the user has
    * installed previously.
    */
   public static final String LOCAL_UPDATE_DIR_NAME = "update";

   /**
    * The name of the release xml file that describes the installed version
    */
   public static final String RELEASE_XML_FILENAME = "release.xml";

   /** 
    * The name of the file that stores the user's desired actions with respect 
    * to a set of available updates.
    */
   public static final String CHANGE_LIST_FILENAME = "changeList.xml";

   /**
    * Downloads the current release available at the specified host and path.
    * 
    * @param host
    *           the host to open an HTTP connection to.
    * @param port
    *           the port to open an HTTP connection to.
    * @param path
    *           the path on the host's webserver to the file.
    * @param fileToGet
    *           the file to get.
    * @return
    */
   ChannelXmlBean downloadCurrentRelease(final String host, final int port,
         final String path, final String fileToGet) throws Exception;

   /**
    * Loads the channel xml bean from the file system.
    * 
    * @param path the directory to find release.xml in
    * 
    * @return the ChannelXmlBean that represents the specified path.
    */
   ChannelXmlBean loadUpdateFromFileSystem(final String path); 
   
   
   /**
    * 
    * @param host
    * @param fileToGet
    * @param destDir
    */
   boolean downloadHttpFile(String host, String path, String fileToGet,
         String destDir);

   /**
    * Returns an ChannelXmlBean that describes the locally installed release.
    * 
    * @param localReleaseFile
    *           the xml file to decode into an xmlbean.
    * 
    * @return a ChannelXmlBean
    */
   ChannelXmlBean getLocalReleaseInfo(String localReleaseFile);

   /**
    * Returns the top-level directory in which all installed components of 
    * SQuirreL live under.
    * 
    * @return a File representing the home directory of SQuirreL
    */
   File getSquirrelHomeDir();

   File getSquirrelPluginsDir();

   File getSquirrelLibraryDir();

   /**
    * Returns the update directory in which all information about available 
    * updates and the user's desired actions are located.
    * 
    * @return a File representing the update directory.
    */
   File getSquirrelUpdateDir();

   /**
    * Create and save a ChangeListXmlBean to the update directory.
    * @param changes the list of changes to be persisted
    * 
    * @throws FileNotFoundException if the file to be written couldn't be found.
    */
   void saveChangeList(List<ArtifactStatus> changes)
         throws FileNotFoundException;

   /**
    * Retrieves the change list (if one exists) from the update directory.
    *  
    * @return a change list bean.
    *
    * @throws FileNotFoundException if the file couldn't be found.
    */
   ChangeListXmlBean getChangeList() throws FileNotFoundException;

   /**
    * Returns the absolute path to the release xml file that describes what
    * release the user currently has.
    * 
    * @return the absolute path to the release xml file
    * 
    * @throws FileNotFoundException if the release xml file couldn't be found.
    */
   String getLocalReleaseFile() throws FileNotFoundException;

   List<ArtifactStatus> getArtifactStatus(ChannelXmlBean channelXmlBean);

   List<ArtifactStatus> getArtifactStatus(ReleaseXmlBean releaseXmlBean);

   /**
    * Returns a set of plugin archive filenames - one for each installed plugin. 
    */
   Set<String> getInstalledPlugins();

   /**
    * Returns a set of translation filenames - one jar for each translation.
    * @return
    */
   Set<String> getInstalledTranslations();

   /**
    * @return the _pluginManager
    */
   PluginManager getPluginManager();

   /**
    * @param manager the _pluginManager to set
    */
   void setPluginManager(PluginManager manager);

}