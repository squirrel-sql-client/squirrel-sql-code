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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ArtifactXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ModuleXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ReleaseXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializer;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.client.util.IOUtilities;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * Low-level utility methods for the UpdateController.
 * 
 * @author manningr
 */
public class UpdateUtilImpl implements UpdateUtil {

   /** Logger for this class. */
   private final static ILogger s_log = 
      LoggerController.createLogger(UpdateUtilImpl.class);
   
   /** the PluginManager that tells us what plugins are installed */
   private PluginManager _pluginManager = null;
   
   /**
    * the utility class that reads and writes release info from/to the
    * release.xml file
    */
   private final UpdateXmlSerializer serializer = new UpdateXmlSerializer();

   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#downloadCurrentRelease(java.lang.String, int, java.lang.String, java.lang.String)
    */
   public ChannelXmlBean downloadCurrentRelease(final String host,
         final int port, final String path, final String fileToGet)
            throws Exception
   {
      ChannelXmlBean result = null;
      result = downloadCurrentReleaseHttp(host, port, path, fileToGet);
      return result;
   }
   
   /**
    * Loads the channel xml bean from the file system.
    * 
    * @param path the directory to find release.xml in
    * 
    * @return the ChannelXmlBean that represents the specified path.
    */
   public ChannelXmlBean loadUpdateFromFileSystem(final String path) 
   {
      ChannelXmlBean result = null;
      try {
         throw new IOException();
      } catch (IOException e) {
         
      }
     
      return result;
   }
   
   
   private ChannelXmlBean downloadCurrentReleaseHttp(final String host,
         final int port, final String path, final String fileToGet)
         throws Exception {
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
         result = serializer.readChannelBean(is);
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
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#downloadHttpFile(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
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
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getLocalReleaseInfo(java.lang.String)
    */
   public ChannelXmlBean getLocalReleaseInfo(String localReleaseFile) {
      ChannelXmlBean result = null;
      if (s_log.isDebugEnabled()) {
         s_log.debug("Attempting to read local release file: "
               + localReleaseFile);
      }
      try {
         result = serializer.readChannelBean(localReleaseFile);
      } catch (IOException e) {
         s_log.error("Unable to read local release file: " + e.getMessage(), e);
      }
      return result;
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getSquirrelHomeDir()
    */
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

   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getSquirrelPluginsDir()
    */
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
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getSquirrelLibraryDir()
    */
   public File getSquirrelLibraryDir()  {
      ApplicationFiles appFiles = new ApplicationFiles();
      File squirrelLibDir = appFiles.getLibraryDirectory();      
      if (!squirrelLibDir.isDirectory()) {
         s_log.error("SQuirreL Library Directory ("
               + squirrelLibDir.getAbsolutePath()
               + " doesn't appear to be a directory");
      }
      return squirrelLibDir;
   }   

   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getSquirrelUpdateDir()
    */
   public File getSquirrelUpdateDir()  {
      ApplicationFiles appFiles = new ApplicationFiles();
      File squirrelUpdateDir = appFiles.getUpdateDirectory();      
      if (!squirrelUpdateDir.isDirectory()) {
         s_log.error("SQuirreL Update Directory ("
               + squirrelUpdateDir.getAbsolutePath()
               + " doesn't appear to be a directory");
      }
      return squirrelUpdateDir;
   }   

   public File getChangeListFile() {
       File updateDir = getSquirrelUpdateDir();
       File changeListFile = new File(updateDir, CHANGE_LIST_FILENAME);
       return changeListFile; 
   }
   
   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#saveChangeList(java.util.List)
    */
   public void saveChangeList(List<ArtifactStatus> changes)
         throws FileNotFoundException {
      ChangeListXmlBean changeBean = new ChangeListXmlBean();
      changeBean.setChanges(changes);
      serializer.write(changeBean, getChangeListFile());
   }
   
   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getChangeList()
    */
   public ChangeListXmlBean getChangeList() throws FileNotFoundException {
       return serializer.readChangeListBean(getChangeListFile());
   }
   
   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getLocalReleaseFile()
    */
   public String getLocalReleaseFile() throws FileNotFoundException {
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
      if (result == null) {
         throw new FileNotFoundException("File " + RELEASE_XML_FILENAME
               + " could not be found");
      }
      return result;
   }
   
   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getArtifactStatus(net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean)
    */
   public List<ArtifactStatus> getArtifactStatus(ChannelXmlBean channelXmlBean) {
      
      ReleaseXmlBean releaseXmlBean = channelXmlBean.getCurrentRelease();
      return getArtifactStatus(releaseXmlBean);
   }
   
   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getArtifactStatus(net.sourceforge.squirrel_sql.client.update.xmlbeans.ReleaseXmlBean)
    */
   public List<ArtifactStatus> getArtifactStatus(ReleaseXmlBean releaseXmlBean) {
      Set<String> installedPlugins = getInstalledPlugins();
      Set<String> installedTranslations = getInstalledTranslations();
      ArrayList<ArtifactStatus> result = new ArrayList<ArtifactStatus>();
      Set<ModuleXmlBean> currentModuleBeans = releaseXmlBean.getModules();
      for (ModuleXmlBean module : currentModuleBeans) {
         Set<ArtifactXmlBean> artifactBeans = module.getArtifacts();
         String moduleName = module.getName();
         for (ArtifactXmlBean artifact: artifactBeans) {
            String name = artifact.getName();
            String type = moduleName;
            boolean installed = artifact.isInstalled();
            ArtifactStatus status = new ArtifactStatus();
            status.setName(name);
            status.setType(type);
            status.setInstalled(installed);
            if (status.isCoreArtifact()) {
               status.setInstalled(true);
            }
            if (status.isPluginArtifact() && installedPlugins.contains(name)) {
               status.setInstalled(true);
            }
            if (status.isTranslationArtifact() 
                  && installedTranslations.contains(name)) 
            {
               status.setInstalled(true);  
            }
            result.add(status);
         }
      }      
      return result;
   }
   
   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getInstalledPlugins()
    */
   public Set<String> getInstalledPlugins() {
      HashSet<String> result = new HashSet<String>();
      
      for (PluginInfo info : _pluginManager.getPluginInformation()) {
         result.add(info.getInternalName() + ".zip");
      }
      return result;
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getInstalledTranslations()
    */
   public Set<String> getInstalledTranslations() {
      HashSet<String> result = new HashSet<String>();
      File libDir = getSquirrelLibraryDir();
      for (String filename : libDir.list()) {
         if (filename.startsWith(" squirrel-sql_")) {
            result.add(filename);
         }
      }
      return result;      
   }
   
   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getPluginManager()
    */
   public PluginManager getPluginManager() {
      return _pluginManager;
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#setPluginManager(net.sourceforge.squirrel_sql.client.plugin.PluginManager)
    */
   public void setPluginManager(PluginManager manager) {
      _pluginManager = manager;
   }
   

}
