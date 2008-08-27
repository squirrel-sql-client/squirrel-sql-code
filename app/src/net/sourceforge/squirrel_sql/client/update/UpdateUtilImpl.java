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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import net.sourceforge.squirrel_sql.client.plugin.IPluginManager;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.util.PathUtils;
import net.sourceforge.squirrel_sql.client.update.util.PathUtilsImpl;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ArtifactXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ModuleXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ReleaseXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializer;
import net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers;
import net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappersImpl;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactoryImpl;
import net.sourceforge.squirrel_sql.fw.util.IOUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * Low-level utility methods for the UpdateController.  Among other things this class provides file locations
 * for important directories that are needed for backup/restore, installation/removal of updates and existing
 * software. The following is a pictorial anatomy of the update directory.
 * 
 * SQUIRREL_SQL_HOME/
 *   |
 *   + update/ (root of the update hierarchy)
 *       |
 *       + backup/ (original files that are to be updated are copied here for recovery purposes)
 *       |   |
 *       |   + core/
 *       |   |
 *       |   + i18n/
 *       |   |
 *       |   + plugin/
 *       |   
 *       + downloads/
 *       |   |
 *       |   + core/
 *       |   |
 *       |   + i18n/
 *       |   |
 *       |   + plugin/
 *       |
 *       + changeList.xml (describes what is in downloads to be installed - deleted after update)
 *       |
 *       + release.xml (describes the release that is currently installed)
 * 
 * @author manningr
 */
public class UpdateUtilImpl implements UpdateUtil {

   /** Logger for this class. */
   private final static ILogger s_log = 
      LoggerController.createLogger(UpdateUtilImpl.class);
   
   /** the PluginManager that tells us what plugins are installed */
   private IPluginManager _pluginManager = null;
   
   /**
    * the utility class that reads and writes release info from/to the
    * release.xml file
    */
   private final UpdateXmlSerializer _serializer = new UpdateXmlSerializer();
   
   /** The size of the buffer to use when extracting files from a ZIP archive */
   public final static int ZIP_EXTRACTION_BUFFER_SIZE = 8192;
   
	/**
	 * Since it can take a while to compute a checksum for large jars, here we cache them for later use.
	 * The key is the absolute path to the file.  The value is it's checksum
	 */
	private HashMap<String, Long> fileChecksumMap = new HashMap<String, Long>();

   /** TODO: Spring-inject when this class is a Spring bean */
   private PathUtils _pathUtils = new PathUtilsImpl();
   public void setPathUtils(PathUtils pathUtils) {
   	this._pathUtils = pathUtils;
   }
	
	/** TODO: Spring-inject when this class is a Spring bean */
	private FileWrapperFactory _fileWrapperFactory = new FileWrapperFactoryImpl();
	public void setFileWrapperFactory(FileWrapperFactory factory) {
		_fileWrapperFactory = factory;
	}
	
	private ApplicationFileWrappers _appFileWrappers = new ApplicationFileWrappersImpl();
	public void setApplicationFileWrappers(ApplicationFileWrappers appFileWrappers) {
		_appFileWrappers = appFileWrappers;
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getCheckSum(java.io.File)
	 */
	public long getCheckSum(File f) {
		String absPath = f.getAbsolutePath();
		
		Long result = -1L; 
		if (fileChecksumMap.containsKey(absPath)) {
			result = fileChecksumMap.get(absPath);
		} else {
			try
			{
				result = IOUtilities.getCheckSum(f);
			}
			catch (IOException e)
			{
				s_log.error("getCheckSum: failed to compute the checksum for file ("+f.getAbsolutePath()+
								"): "+e.getMessage(), e);
			}
			// -1 is stored if the checksum operation failed.  This will ensure that comparison with any other
			// file's checksum will be different - even if they happen to be the same file.
			fileChecksumMap.put(absPath, result);
		}
		return result;
	}
   
   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#downloadCurrentRelease(java.lang.String, 
    * int, java.lang.String, java.lang.String)
    */
   public ChannelXmlBean downloadCurrentRelease(final String host,
         final int port, final String path, final String fileToGet)
            throws Exception
   {
      ChannelXmlBean result = null;
      if (s_log.isDebugEnabled()) {
      	s_log.debug("downloadCurrentRelease: host=" + host + " port=" + port + " path=" + path
				+ " fileToGet=" + fileToGet);
      }
      result = downloadCurrentReleaseHttp(host, port, path, fileToGet);      
      return result;
   }
   
   /**
    * Loads the channel xml bean from the file system.throw new IOException();
    * 
    * @param path the directory to find release.xml in
    * 
    * @return the ChannelXmlBean that represents the specified path.
    */
   public ChannelXmlBean loadUpdateFromFileSystem(final String path) 
   {
      ChannelXmlBean result = null;
      BufferedInputStream is = null;
      try {
         File f = new File(path);
         if (!f.isDirectory()) {
            s_log.error("FileSystem path ("+path+") is not a directory.");
         } else {
            f = new File(f, RELEASE_XML_FILENAME); 
            is = new BufferedInputStream(new FileInputStream(f));
            result = _serializer.readChannelBean(is);
         }            
      } catch (IOException e) {
         s_log.error("Unexpected exception while attempting "
               + "load updates from filesystem path (" + path + "): "
               + e.getMessage(), e);
      } finally {
         IOUtilities.closeInputStream(is);
      }
     
      return result;
   }
   
   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#downloadLocalFile(java.lang.String, java.lang.String)
    */
   public boolean downloadLocalFile(String fileToGet, String destDir) throws FileNotFoundException, IOException {
      boolean result = false;
      File fromFile = new File(fileToGet);
      if (fromFile.isFile() && fromFile.canRead()) {
         String filename = fromFile.getName();
         File toFile = new File(destDir, filename);
         copyFile(fromFile, toFile);
         result = true;
      } else {
         s_log.error("File "+fileToGet+" doesn't appear to be readable");
      }
      return result;
   }
   
   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#copyFile(java.io.File, java.io.File)
    */
   public void copyFile(final File from, final File to) throws FileNotFoundException, IOException {
      if (!from.exists()) {
      	s_log.error("Cannot copy from file ("+from.getAbsolutePath()+") which doesn't appear to exist.");
      	return;
      }
      File toFile = to;
      // Check to see if to is a directory and convert toFile to be the name of the file in that directory.
      if (to.isDirectory()) {
      	toFile = getFile(to, from.getName());
      }
      if (s_log.isDebugEnabled()) {
      	s_log.debug("Copying from file ("+from.getAbsolutePath()+") to file ("+toFile.getAbsolutePath()+")");
      }
      if (toFile.exists()) {
      	long fromCheckSum = getCheckSum(from);
      	long toCheckSum = getCheckSum(toFile);
      	if (fromCheckSum == toCheckSum) {
      		if (s_log.isInfoEnabled()) {
      			s_log.info("File to be copied("+from.getAbsolutePath()+") has the same checksum("+
      				fromCheckSum+") as the file to copy to ("+toFile.getAbsolutePath()+"). Skipping copy.");
      		}
      		return;
      	}
      }
      FileInputStream in = null;
      FileOutputStream out = null;
      try {
         in = new FileInputStream(from);
         out = new FileOutputStream(toFile);
         byte[] buffer = new byte[8192];
         int len;
         while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);         
         }
      } finally {
         IOUtilities.closeInputStream(in);
         IOUtilities.closeOutputStream(out);
      }
   }
   
   /**
	 * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#copyDir(java.io.File, java.io.File)
	 */
	public void copyDir(File fromDir, File toDir) throws FileNotFoundException, IOException
	{	
		if (!fromDir.isDirectory()) {
			throw new IllegalArgumentException("Expected fromDir("+fromDir.getAbsolutePath()+
				") to be a directory.");
		}
		if (!toDir.isDirectory()) {
			throw new IllegalArgumentException("Expected toDir("+toDir.getAbsolutePath()+
				") to be a directory.");
		}
		File[] files = toDir.listFiles();
		for (File sourceFile : files) {
			copyFile(sourceFile, toDir);
		}
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
         result = _serializer.readChannelBean(localReleaseFile);
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
	 * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getInstalledSquirrelMainJarLocation()
	 */
	public File getInstalledSquirrelMainJarLocation()
	{
		return new File(this.getSquirrelHomeDir(), UpdateUtil.SQUIRREL_SQL_JAR_FILENAME);
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
      return getDir(appFiles.getUpdateDirectory(), null, true);       
   }   

   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getDownloadsDir()
    */
   public File getDownloadsDir() {
   	return getDir(getSquirrelUpdateDir(), DOWNLOADS_DIR_NAME, true);
   }
   
   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getCoreDownloadsDir()
    */
   public File getCoreDownloadsDir() {
   	return getDir(getDownloadsDir(), CORE_ARTIFACT_ID, true);
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getPluginDownloadsDir()
    */
   public File getPluginDownloadsDir() {
   	return getDir(getDownloadsDir(), PLUGIN_ARTIFACT_ID, true);
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getI18nDownloadsDir()
    */
   public File getI18nDownloadsDir() {
   	return getDir(getDownloadsDir(), TRANSLATION_ARTIFACT_ID, true);
   }

   
	/**
	 * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getBackupDir()
	 */
	public File getBackupDir()
	{
		return getDir(getSquirrelUpdateDir(), BACKUP_ROOT_DIR_NAME, false);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getCoreBackupDir()
	 */
	public File getCoreBackupDir()
	{
		return getDir(getBackupDir(), CORE_ARTIFACT_ID, false);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getI18nBackupDir()
	 */
	public File getI18nBackupDir()
	{
		return getDir(getBackupDir(), TRANSLATION_ARTIFACT_ID, false);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getPluginBackupDir()
	 */
	public File getPluginBackupDir()
	{
		return getDir(getBackupDir(), PLUGIN_ARTIFACT_ID, false);
	}   
      
   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getChangeListFile()
    */
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
      _serializer.write(changeBean, getChangeListFile());
   }
   
   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getChangeList()
    */
   public ChangeListXmlBean getChangeList() throws FileNotFoundException {
       return _serializer.readChangeListBean(getChangeListFile());
   }
   
   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getLocalReleaseFile()
    */
   public File getLocalReleaseFile() throws FileNotFoundException {
      File result = null;
      try {
         File[] files = getSquirrelHomeDir().listFiles();
         for (File file : files) {
            if (LOCAL_UPDATE_DIR_NAME.equals(file.getName())) {
               File[] updateFiles = file.listFiles();
               for (File updateFile : updateFiles) {
                  if (RELEASE_XML_FILENAME.equals(updateFile.getName())) {
                     result = updateFile;
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
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#
    * getArtifactStatus(net.sourceforge.squirrel_sql.client.update.xmlbeans.ReleaseXmlBean)
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
            ArtifactStatus status = new ArtifactStatus(artifact);
            status.setType(moduleName);
            if (status.isCoreArtifact()) {
               status.setInstalled(true);
            }
            if (status.isPluginArtifact() && installedPlugins.contains(artifact.getName())) {
               status.setInstalled(true);
            }
            if (status.isTranslationArtifact() 
                  && installedTranslations.contains(artifact.getName())) 
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
         if (filename.startsWith("squirrel-sql_")) {
            result.add(filename);
         }
      }
      return result;      
   }
   
   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getPluginManager()
    */
   public IPluginManager getPluginManager() {
      return _pluginManager;
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#setPluginManager(net.sourceforge.squirrel_sql.client.plugin.PluginManager)
    */
   public void setPluginManager(IPluginManager manager) {
      _pluginManager = manager;
   }
   
   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#checkDir(java.io.File, java.lang.String)
    */
   public File checkDir(File parent, String child) {
      File dir = new File(parent, child);
      if (!dir.exists()) {
         dir.mkdir();
      }
      return dir;
   }
   
   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#createZipFile(java.io.File, java.io.File[])
    */
   public void createZipFile(File zipFile, File... sourceFiles) 
      throws FileNotFoundException, IOException  
   {
      ZipOutputStream os = 
         new ZipOutputStream(new FileOutputStream(zipFile));
      zipFileOs(os, sourceFiles);
      os.close();
   }
   
	/**
	 * This function will recursively delete directories and files.
	 * 
	 * @param path
	 *           File or Directory to be deleted
	 * @return true indicates successfully deleted the file or directory.
	 */
	public boolean deleteFile(File path)
	{
		boolean result = true;
		if (path.exists())
		{
			if (path.isFile()) {
				result = path.delete();
				if (s_log.isInfoEnabled()) {
					if (result) {
						s_log.info("deleteFile: successfully deleted file = "+path.getAbsolutePath());
					} else {
						s_log.info("deleteFile: failed to delete file = "+path.getAbsolutePath());
					}
				}
			} else {
				File[] files = path.listFiles();
				for (int i = 0; i < files.length; i++)
				{
					result = result && deleteFile(files[i]);
				}
				result = result && path.delete();
			} 
		}
		return result;
	}   
      
   /**
    * Extracts the specified zip file to the specified output directory.
    * @param zipFile
    * @param outputDirectory
    * @throws IOException 
    */
   public void extractZipFile(File zipFile, File outputDirectory) throws IOException {
   	if (!outputDirectory.isDirectory()) {
   		s_log.error("Output directory specified (" + outputDirectory.getAbsolutePath()
				+ ") doesn't appear to be a directory");
   		return;
   	}
   	FileInputStream fis = null;
   	ZipInputStream zis = null;
   	FileOutputStream fos = null; 
   	try {
   		fis = new FileInputStream(zipFile);
	   	zis = new ZipInputStream(fis);
	   	ZipEntry zipEntry = zis.getNextEntry(); 
	   	while (zipEntry != null) {
	   		String name = zipEntry.getName();
	   		if (zipEntry.isDirectory()) {
	   			checkDir(outputDirectory, name);
	   		} else {
	   			File newFile = new File(outputDirectory, name);
	   			if (newFile.exists()) {
	   				if (s_log.isInfoEnabled()) {
	   					s_log.info("Deleting extraction file that already exists:"+newFile.getAbsolutePath());
	   				}
	   				newFile.delete();
	   			}
	   			fos = new FileOutputStream(newFile); 
	   			byte[] buffer = new byte[ZIP_EXTRACTION_BUFFER_SIZE];
	   			int n = 0;
	   			while ((n = zis.read(buffer, 0, ZIP_EXTRACTION_BUFFER_SIZE)) > -1) {
	   				fos.write(buffer, 0, n);
	   			}
	   		   fos.close();
	   		}
	   		zipEntry = zis.getNextEntry();
	   	}
   	} finally {
   		IOUtilities.closeOutputStream(fos);
   		IOUtilities.closeInputStream(fis);
   		IOUtilities.closeInputStream(zis);
   	}
   }
   
   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getChangeList(java.io.File)
    */
   public ChangeListXmlBean getChangeList(File changeListFile) throws FileNotFoundException {
      return _serializer.readChangeListBean(changeListFile);
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getFile(java.io.File, java.lang.String)
    */
   public File getFile(File installDir, String artifactName) {
      return new File(installDir, artifactName);
   }

   /**
    * Writes the specified sourceFile(s) contents to the specified Zip output stream.
    * 
    * @param os the Zip OutputStream to write to
    * @param sourceFiles the files to read from
    * @throws FileNotFoundException if one of the files could not be found
    * @throws IOException if and IO error occurs
    */
   private void zipFileOs(ZipOutputStream os, File[] sourceFiles)
         throws FileNotFoundException, IOException {
      for (File file : sourceFiles) {
         if (file.isDirectory()) {
            zipFileOs(os, file.listFiles());
         } else {
            FileInputStream fis = null;
            try {
               fis = new FileInputStream(file);
               os.putNextEntry(new ZipEntry(file.getPath()));
               IOUtilities.copyBytes(fis, os);
            } finally {
               IOUtilities.closeInputStream(fis);
            }
         }
      }
   }
   
   /**
    * @param parent
    * @param dirName
    * @param create
    * @return
    */
   private File getDir(File parent, String dirName, boolean create) {
		File result = null;
		if (dirName != null) {
			result = new File(parent, dirName);
		} else {
			result = parent;
		}
		if (!result.isDirectory()) {
	   	if (result.exists()) {
	         // If the update dir, is actually a file, log an error.
				s_log.error(dirName + " directory (" + result.getAbsolutePath()
					+ ") doesn't appear to be a directory");	   		
	   	} else {
	   	   // If the downloads dir doesn't already exist, just create it.
	   		if (create) {
	   			result.mkdir();
	   		}
	   	}
		}
		return result;
   }
   
   /**
    * @param host
    * @param port
    * @param path
    * @param fileToGet
    * @return
    * @throws Exception if the current release file could not be downloaded
    */
   private ChannelXmlBean downloadCurrentReleaseHttp(final String host,
      final int port, final String path, final String file) throws Exception {
   	
   	ChannelXmlBean result = null;
   	InputStream is = null;
   	try {
   		String fileToGet = _pathUtils.buildPath(true, path, file); 
   		
   		// We set expected and checksum to -1 here, since we don't have that information for release.xml file
   		// TODO: Can HttpClient be used to get the byte-size of release.xml so we can perform this check?
   		String filename =
				downloadHttpFile(host, port, fileToGet, getDownloadsDir().getAbsolutePath(), -1, -1);
   		File releaseXmlFile = new File(filename);
   		if (releaseXmlFile.exists()) {
   			is = new BufferedInputStream(new FileInputStream(filename));
   			result = _serializer.readChannelBean(is);
   		} else {
   			throw new FileNotFoundException("Current release file couldn't be downloaded");
   		}
   	} finally {
   		IOUtilities.closeInputStream(is);
   	}
   	return result;
   }
      
   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#downloadHttpFile(java.lang.String, int, java.lang.String, java.lang.String, long, long)
    */
   public String downloadHttpFile(final String host, final int port, final String fileToGet, String destDir,
		long fileSize, long checksum) throws Exception
	{
      BufferedInputStream is = null;
      BufferedOutputStream os = null;
      URL url = null;
      HttpMethod method = null;
      int resultCode = -1;
      String result = null;
      try {
         String server = host;
         if (server.startsWith(HTTP_PROTOCOL_PREFIX)) {
            int beginIdx = server.indexOf("://") + 3;
            server = server.substring(beginIdx, host.length());
         }
         
         if (port == 80) {
         	url = new URL(HTTP_PROTOCOL_PREFIX, server, fileToGet);
         } else {
         	url = new URL(HTTP_PROTOCOL_PREFIX, server, port, fileToGet);
         }
          
         if (s_log.isDebugEnabled()) {
         	s_log.debug("downloadHttpFile: downloading file ("+fileToGet+") from url: "+url);
         }
         HttpClient client = new HttpClient();
         method = new GetMethod(url.toString());
         method.setFollowRedirects(true);
         
         resultCode = client.executeMethod(method);
         if (resultCode != 200) {
         	throw new FileNotFoundException("Failed to download file from url ("+url+
         		"): HTTP Response Code="+resultCode);
         }
         InputStream mis = method.getResponseBodyAsStream();
         
         if (s_log.isDebugEnabled()) {
         	s_log.debug("downloadHttpFile: response code was: "+resultCode);         	
         }
         is = new BufferedInputStream(mis);
			File resultFile = new File(destDir, _pathUtils.getFileFromPath(fileToGet));
			result = resultFile.getAbsolutePath();
			if (!resultFile.exists()) {
				resultFile.createNewFile();
			}
			os = new BufferedOutputStream(new FileOutputStream(resultFile));
			byte[] buffer = new byte[8192];
			int length = 0;
			
			if (s_log.isDebugEnabled()) {
				s_log.debug("downloadHttpFile: writing http response body to file: "+resultFile);
			}
			
			int totalLength = 0;
			while ((length = is.read(buffer)) != -1)
			{
				totalLength += length;
				os.write(buffer, 0, length);
			}
			verifySize(url, fileSize, totalLength);
      } catch (Exception e) {
         s_log.error("downloadCurrentRelease: Unexpected exception while "
               + "attempting to open an HTTP connection to host (" + host
               + ") on port ("+port+") to download a file (" + fileToGet + "): "+
               e.getMessage(), e);
         s_log.error("response code was: "+resultCode);
         throw e;
      } finally {
         IOUtilities.closeInputStream(is);
         IOUtilities.closeOutputStream(os);
         method.releaseConnection();
      }
      return result;
   }
   
	/**
	 * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#getDownloadFileLocation(
	 * net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus)
	 */
	public File getDownloadFileLocation(ArtifactStatus status)
	{
		File result = null;
		if (CORE_ARTIFACT_ID.equals(status.getType())) {
			result = getFile(getCoreDownloadsDir(), status.getName());
		}
		if (PLUGIN_ARTIFACT_ID.equals(status.getType())) {
			result = getFile(getPluginDownloadsDir(), status.getName());
		}
		if (TRANSLATION_ARTIFACT_ID.equals(status.getType())) {
			result = getFile(getI18nDownloadsDir(), status.getName());
		}
		return result;
	}
   
   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateUtil#isPresentInDownloadsDirectory(
    * net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus)
    */
   public boolean isPresentInDownloadsDirectory(ArtifactStatus status)
	{
		boolean result = false;

		File downloadFile = getDownloadFileLocation(status);
		
		if (downloadFile.exists())
		{
			long checkSum = getCheckSum(downloadFile);
			if (status.getChecksum() == checkSum)
			{
				if (downloadFile.length() == status.getSize())
				{
					result = true;
				}
			}
		}
		return result;
   }
   
	
	
   /* Helper Methods */
   
   /**
	 * Verifies that the byte size of what was downloaded matches the expected size of the artifact.  If 
	 * expected is -1, this check will be skipped; this is expected in the case where the release.xml file is
	 * being downloaded and there is no information about big it is. When -1 is expected, then a log message
	 * is created and this check is skipped.
	 * 
	 * @param url
	 *           the URL that was downloaded from
	 * @param expected
	 *           the number of bytes expected to be downloaded
	 * @param actual
	 *           the actual number of bytes downloaded
	 * @throws Exception
	 *            if the two counts do not match.
	 */
   private void verifySize(URL url, long expected, long actual) throws Exception {
   	if (expected == -1) {
   		if (s_log.isInfoEnabled()) {
   			s_log.info("verifySize: expected size was -1.  Skipping check for url: "+url.toString());
   		}
   		return;
   	}
   	if (expected != actual) {
   		throw new Exception("Attempt to get file contents from url ("+url.toString()+") resulted in "+actual+
   			" bytes downloaded, but "+expected+" bytes were expected.");
   	}
   }
      
}
