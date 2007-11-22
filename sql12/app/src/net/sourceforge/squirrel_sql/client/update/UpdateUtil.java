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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ArtifactXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ModuleXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ReleaseXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializer;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.client.util.IOUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * Utility methods for the UpdateManager.
 * 
 * @author manningr
 */
public class UpdateUtil {

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

   /** Logger for this class. */
   private final static ILogger s_log = LoggerController.createLogger(UpdateUtil.class);

   /** the PluginManager that tells us what plugins are installed */
   private PluginManager _pluginManager = null;
   
   /**
    * the utility class that reads and writes release info from/to the
    * release.xml file
    */
   private final UpdateXmlSerializer serializer = new UpdateXmlSerializer();

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
   public ChannelXmlBean downloadCurrentRelease(final String host,
         final int port, final String path, final String fileToGet)
         throws Exception 
   {
      ChannelXmlBean result = null;
      BufferedInputStream is = null;
      String pathToFile = path + fileToGet;
      
      try {
         String server = host;
         if (server.startsWith(HTTP_PROTOCOL_PREFIX)) {
            int beginIdx = server.indexOf("://") + 3;
            server = server.substring(beginIdx, host.length());
         }
         URL url = new URL(HTTP_PROTOCOL_PREFIX, server, port, pathToFile);
         is = new BufferedInputStream(url.openStream());
         result = serializer.read(is);
      } catch (Exception e) {
         s_log.error("downloadCurrentRelease: Unexpected exception while "
               + "attempting to open an HTTP connection to host (" + host
               + ") " + "to download a file (" + pathToFile + "): "+
               e.getMessage(), e);
         throw e;
      } finally {
         IOUtilities.closeInputStream(is);
      }
      return result;
   }
   
   /**
    * 
    * @param host
    * @param fileToGet
    * @param destDir
    */
   public boolean downloadHttpFile(String host, String path, String fileToGet,
         String destDir) {
      boolean result = false;
      BufferedInputStream is = null;
      BufferedOutputStream os = null;
      try {
         URL url = new URL(HTTP_PROTOCOL_PREFIX, host, path + fileToGet);
         is = new BufferedInputStream(url.openStream());
         File localFile = new File(destDir, fileToGet);
         os = new BufferedOutputStream(new FileOutputStream(localFile));
         byte[] buffer = new byte[8192];
         int length = 0;
         while ((length = is.read(buffer)) != -1) {
            os.write(buffer, 0, length);
         }
         result = true;
      } catch (Exception e) {
         s_log.error("Exception encountered while attempting to "
               + "download file " + fileToGet + " from host " + host
               + " and path " + path + " to destDir " + destDir + ": "
               + e.getMessage(), e);
      } finally {
         IOUtilities.closeInputStream(is);
         IOUtilities.closeOutpuStream(os);
      }
      return result;
   }

   /**
    * Returns an ChannelXmlBean that describes the locally installed release.
    * 
    * @param localReleaseFile
    *           the xml file to decode into an xmlbean.
    * 
    * @return a ChannelXmlBean
    */
   public ChannelXmlBean getLocalReleaseInfo(String localReleaseFile) {
      ChannelXmlBean result = null;
      if (s_log.isDebugEnabled()) {
         s_log.debug("Attempting to read local release file: "
               + localReleaseFile);
      }
      try {
         result = serializer.read(localReleaseFile);
      } catch (IOException e) {
         s_log.error("Unable to read local release file: " + e.getMessage(), e);
      }
      return result;
   }

   public File getSquirrelHomeDir()  {
      ApplicationFiles appFiles = new ApplicationFiles();
      File squirrelHomeDir = appFiles.getSquirrelHomeDir();      
      if (!squirrelHomeDir.isDirectory()) {
         s_log.error("SQuirreL Home Directory ("
               + squirrelHomeDir.getAbsolutePath()
               + " doesn't appear to be a directory");
      }
      return squirrelHomeDir;
   }

   public File getSquirrelPluginsDir()  {
      ApplicationFiles appFiles = new ApplicationFiles();
      File squirrelHomeDir = appFiles.getPluginsDirectory();      
      if (!squirrelHomeDir.isDirectory()) {
         s_log.error("SQuirreL Plugins Directory ("
               + squirrelHomeDir.getAbsolutePath()
               + " doesn't appear to be a directory");
      }
      return squirrelHomeDir;
   }   
   
   /**
    * Returns the absolute path to the release xml file that describes what
    * release the user currently has.
    * 
    * @return
    */
   public String getLocalReleaseFile() {
      String result = null;
      try {
         File[] files = getSquirrelHomeDir().listFiles();
         for (File file : files) {
            if (LOCAL_UPDATE_DIR_NAME.equals(file.getName())) {
               File[] updateFiles = file.listFiles();
               for (File updateFile : updateFiles) {
                  if (RELEASE_XML_FILENAME.equals(updateFile.getName())) {
                     result = updateFile.getAbsolutePath();
                  }
               }
            }
         }
      } catch (Exception e) {
         s_log.error("getLocalReleaseFile: Exception encountered while "
               + "attempting to find "+RELEASE_XML_FILENAME+" file");
      }
      return result;
   }
   
   public List<ArtifactStatus> getArtifactStatus(ChannelXmlBean channelXmlBean) {
      
      ReleaseXmlBean releaseXmlBean = channelXmlBean.getCurrentRelease();
      return getArtifactStatus(releaseXmlBean);
   }
   
   public List<ArtifactStatus> getArtifactStatus(ReleaseXmlBean releaseXmlBean) {
      Set<String> installedPlugins = getInstalledPlugins();
      ArrayList<ArtifactStatus> result = new ArrayList<ArtifactStatus>();
      Set<ModuleXmlBean> currentModuleBeans = releaseXmlBean.getModules();
      for (ModuleXmlBean module : currentModuleBeans) {
         Set<ArtifactXmlBean> artifactBeans = module.getArtifacts();
         String moduleName = module.getName();
         for (ArtifactXmlBean artifact: artifactBeans) {
            String name = artifact.getName();
            String type = moduleName;
            boolean installed = artifact.isInstalled();
            if (moduleName.equals("core")) {
               installed = true;
            }
            if (moduleName.equals("plugin") && installedPlugins.contains(name)) {
               installed = true;
            }
            ArtifactStatus status = new ArtifactStatus(name, type, installed);
            result.add(status);
         }
      }      
      return result;
   }
   
   /**
    * Returns a set of plugin archive filenames - one for each installed plugin. 
    */
   public Set<String> getInstalledPlugins() {
      HashSet<String> result = new HashSet<String>();
      
      for (PluginInfo info : _pluginManager.getPluginInformation()) {
         result.add(info.getInternalName() + ".zip");
      }
      return result;
   }

   /**
    * @return the _pluginManager
    */
   public PluginManager getPluginManager() {
      return _pluginManager;
   }

   /**
    * @param manager the _pluginManager to set
    */
   public void setPluginManager(PluginManager manager) {
      _pluginManager = manager;
   }
   

}
