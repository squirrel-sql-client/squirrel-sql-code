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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import net.sourceforge.squirrel_sql.client.plugin.IPluginManager;
import net.sourceforge.squirrel_sql.client.preferences.IUpdateSettings;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ReleaseXmlBean;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.IProxySettings;

public interface UpdateUtil
{

	/**
	 * where we expect to find release.xml, which describes what the user has installed previously.
	 */
	public static final String LOCAL_UPDATE_DIR_NAME = "update";

	/**
	 * The directory under the local update directory where previous versions of artifacts are saved for
	 * recovery purposes or if the user decides to revert to the previous version.
	 */
	public static final String BACKUP_ROOT_DIR_NAME = "backup";

	/**
	 * The name of the release xml file that describes the installed version
	 */
	public static final String RELEASE_XML_FILENAME = "release.xml";

	/**
	 * The name of the file that stores the user's desired actions with respect to a set of available updates.
	 */
	public static final String CHANGE_LIST_FILENAME = "changeList.xml";

	/**
	 * This is the directory below the update directory where updated files are downloaded for installation.
	 */
	public final static String DOWNLOADS_DIR_NAME = "downloads";

	/** the value for artifact type that identifies it as a core artifact */
	public static final String CORE_ARTIFACT_ID = "core";

	/** the value for artifact type that identifies it as a plugin artifact */
	public static final String PLUGIN_ARTIFACT_ID = "plugin";

	/** the value for artifact type that identifies it as a plugin artifact */
	public static final String TRANSLATION_ARTIFACT_ID = "i18n";

	/** The SQuirreL jar that contains the core classes in the "app" module */
	public static final String SQUIRREL_SQL_JAR_FILENAME = "squirrel-sql.jar";

	/** The documentation archive that contains all of core documentation - assume that top directory is doc */
	public static final String DOCS_ARCHIVE_FILENAME = "doc.zip";

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
	ChannelXmlBean downloadCurrentRelease(final String host, final int port, final String path,
		final String fileToGet, final IProxySettings proxySettings) throws Exception;

	/**
	 * Loads the channel xml bean from the file system.
	 * 
	 * @param path
	 *           the directory to find release.xml in
	 * @return the ChannelXmlBean that represents the specified path.
	 */
	ChannelXmlBean loadUpdateFromFileSystem(final String path);

	/**
	 * Downloads the specified file from the specified server and stores it by the same name in the specified
	 * destination directory.
	 * 
	 * @param host
	 *           the name of the server
	 * @param port
	 *           the port on the server
	 * @param fileToGet
	 *           the name of the file to download
	 * @param fileSize
	 *           the size of the file in bytes
	 * @param checksum
	 *           the checksum of the file
	 * @param proxySettings
	 *           information about the web-proxy to use if any
	 * @return a string representing the full local path to where the file was downloaded to
	 * @throws Exception
	 */
	String downloadHttpUpdateFile(String host, int port, String fileToGet, String destDir, long fileSize,
		long checksum, IProxySettings proxySettings) throws Exception;

	/**
	 * Downloads the a file from a local directory into our update downloads directory.
	 * 
	 * @param fileToGet
	 *           the file to retreive.
	 * @param destDir
	 *           the destination directory into which to place the file.
	 * @return true if the download succeeded; false otherwise.
	 */
	boolean downloadLocalUpdateFile(String fileToGet, String destDir) throws FileNotFoundException,
		IOException;

	/**
	 * Copies the specified from file to the specified to file. If "to" is a directory, then this will copy
	 * "from" into that directory and the resulting file will have the same name.
	 * 
	 * @param from
	 *           the file to copy from
	 * @param to
	 *           the file to copy to
	 */
	void copyFile(final FileWrapper from, final FileWrapper to) throws FileNotFoundException, IOException;


	/**
	 * Lists the specified fromDir and moves all of the files found in that directory that match the specified
	 * filePattern to the specified toDir
	 * directory.
	 * 
	 * @param fromDir
	 *           the directory to copy files from
	 * @param toDir
	 *           the directory to copy files to
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	void moveFiles(final FileWrapper fromDir, final String filePattern, boolean matchPattern,
		final FileWrapper toDir) throws FileNotFoundException, IOException;

	
	/**
	 * Lists the specified fromDir and copies all of the files found in that directory to the specified toDir
	 * directory.
	 * 
	 * @param fromDir
	 *           the directory to copy files from
	 * @param toDir
	 *           the directory to copy files to
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	void copyDir(final FileWrapper fromDir, final FileWrapper toDir) throws FileNotFoundException, IOException;

	/**
	 * Lists the specified fromDir and copies all of the files found in that directory to the specified toDir
	 * directory.
	 * 
	 * @param fromDir
	 *           the directory to copy files from
	 * @param filePattern
	 *           the pattern against which to compare the name of the file
	 * @param matchPattern
	 *           a boolean value indicating whether or not to match the specified file pattern. If true, then
	 *           only filenames that match the pattern are copied. If false, the only filenames that do not
	 *           match the pattern are copied.
	 * @param toDir
	 *           the directory to copy files to
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	void copyDir(final FileWrapper fromDir, final String filePattern, boolean matchPattern,
		final FileWrapper toDir) throws FileNotFoundException, IOException;

	/**
	 * Returns an ChannelXmlBean that describes the locally installed release.
	 * 
	 * @param localReleaseFile
	 *           the xml file to decode into an xmlbean.
	 * @return a ChannelXmlBean
	 */
	ChannelXmlBean getLocalReleaseInfo(String localReleaseFile);

	/**
	 * Returns the top-level directory in which all installed components of SQuirreL live under.
	 * 
	 * @return a File representing the home directory of SQuirreL
	 */
	FileWrapper getSquirrelHomeDir();

	/**
	 * Returns the top-level directory in which all installed plugins of SQuirreL live under.
	 * 
	 * @return a File representing the plugins directory of SQuirreL
	 */
	FileWrapper getSquirrelPluginsDir();

	/**
	 * Returns the top-level directory in which all core libraries (and possibly translations) of SQuirreL live
	 * under.
	 * 
	 * @return a File representing the core library directory of SQuirreL
	 */
	FileWrapper getSquirrelLibraryDir();

	/**
	 * Returns the file that represents the list of changes to make when running the prelaunch update
	 * application
	 * 
	 * @return a File representing the change list.
	 */
	FileWrapper getChangeListFile();

	FileWrapper checkDir(FileWrapper parent, String child);

	void createZipFile(FileWrapper zipFile, FileWrapper... sourceFiles) throws FileNotFoundException,
		IOException;

	/**
	 * Returns the update directory in which all information about available updates and the user's desired
	 * actions are located.
	 * 
	 * @return a File representing the update directory.
	 */
	FileWrapper getSquirrelUpdateDir();

	/**
	 * Create and save a ChangeListXmlBean to the update directory.
	 * 
	 * @param changes
	 *           the list of changes to be persisted
	 * @throws FileNotFoundException
	 *            if the file to be written couldn't be found.
	 */
	void saveChangeList(List<ArtifactStatus> changes) throws FileNotFoundException;

	/**
	 * Retrieves the change list (if one exists) from the update directory.
	 * 
	 * @return a change list bean.
	 * @throws FileNotFoundException
	 *            if the file couldn't be found.
	 */
	ChangeListXmlBean getChangeList() throws FileNotFoundException;

	/**
	 * Returns the absolute path to the release xml file that describes what release the user currently has.
	 * 
	 * @return the absolute path to the release xml file
	 * @throws FileNotFoundException
	 *            if the release xml file couldn't be found.
	 */
	FileWrapper getLocalReleaseFile() throws FileNotFoundException;

	/**
	 * Builds a list of ArtifactStatus objects from the specified ChannelXmlBean
	 * 
	 * @param channelXmlBean
	 *           the bean that represents the channel that the user is pulling updates from.
	 * @return a list of ArtifactStatus objects that describe all of the available artifacts from the specified
	 *         channel.
	 */
	List<ArtifactStatus> getArtifactStatus(ChannelXmlBean channelXmlBean);

	List<ArtifactStatus> getArtifactStatus(ReleaseXmlBean releaseXmlBean);

	/**
	 * Returns a set of plugin archive filenames - one for each installed plugin.
	 */
	Set<String> getInstalledPlugins();

	/**
	 * Returns a set of translation filenames - one jar for each translation.
	 * 
	 * @return
	 */
	Set<String> getInstalledTranslations();

	/**
	 * @return the _pluginManager
	 */
	IPluginManager getPluginManager();

	/**
	 * @param manager
	 *           the _pluginManager to set
	 */
	void setPluginManager(IPluginManager manager);

	/**
	 * Returns the top-level directory that contains artifact type sub-folders into which downloaded files are
	 * copied.
	 * 
	 * @return a File representing the root directory of the download tree.
	 */
	FileWrapper getDownloadsDir();

	FileWrapper getCoreDownloadsDir();

	FileWrapper getPluginDownloadsDir();

	FileWrapper getI18nDownloadsDir();

	FileWrapper getBackupDir();

	FileWrapper getCoreBackupDir();

	FileWrapper getPluginBackupDir();

	FileWrapper getI18nBackupDir();

	/**
	 * Returns the absolute path to the location of the squirrel-sql.jar file.
	 * 
	 * @return a File representing the current installed squirrel-sql.jar file.
	 */
	FileWrapper getInstalledSquirrelMainJarLocation();

	ChangeListXmlBean getChangeList(FileWrapper changeListFile) throws FileNotFoundException;

	FileWrapper getFile(FileWrapper installDir, String artifactName);

	/**
	 * This function will recursivly delete directories and files.
	 * 
	 * @param path
	 *           File or Directory to be deleted
	 * @return true indicates success.
	 */
	boolean deleteFile(FileWrapper path);

	/**
	 * Extracts the specified zip file to the specified output directory.
	 * 
	 * @param zipFile
	 *           the compressed archive file to extract
	 * @param outputDirectory
	 *           the directory into which to extract
	 * @throws IOException
	 *            if an error occurs
	 */
	void extractZipFile(FileWrapper zipFile, FileWrapper outputDirectory) throws IOException;

	/**
	 * Returns the absolute path to the file in the downloads section for the specified ArtifactStatus
	 * 
	 * @param status
	 *           the ArtifactStatus that describes the type and name of this artifact.
	 * @return a File object representing the location of the artifact in the downloads directory.
	 */
	FileWrapper getDownloadFileLocation(ArtifactStatus status);

	boolean isPresentInDownloadsDirectory(ArtifactStatus status);

	/**
	 * Get the checksum for the specified file. This has a side effect in that it caches the checksum for
	 * speedier lookup on subsequent calls.
	 * 
	 * @param f
	 *           the file to get the checksum for
	 * @return the checksum as a long. If an error occurs, this method will return -1
	 */
	public long getCheckSum(FileWrapper f);

	/**
	 * Returns an Enum value representing the users preference for how often to automatically check for
	 * updates.
	 * 
	 * @param settings
	 *           the settings to look in.
	 * @return the Enum value
	 */
	public UpdateCheckFrequency getUpdateCheckFrequency(IUpdateSettings settings);
}