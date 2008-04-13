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
package net.sourceforge.squirrel_sql.client.update.gui.installer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactAction;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallEventType;
import net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallStatusEvent;
import net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallStatusEventFactory;
import net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallStatusListener;
import net.sourceforge.squirrel_sql.client.update.gui.installer.util.InstallFileOperationInfo;
import net.sourceforge.squirrel_sql.client.update.gui.installer.util.InstallFileOperationInfoFactory;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This class is used by the PreLaunchUpdateApplication to install artifact files that belong to a particular
 * change list.
 * 
 * @author manningr
 */
public class ArtifactInstallerImpl implements ArtifactInstaller
{

	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(ArtifactInstallerImpl.class);


	/** bean which describes all files that are a part of the change set to be applied */
	private ChangeListXmlBean _changeListBean = null;

	/** listeners to notify of important install events */
	private List<InstallStatusListener> _listeners = new ArrayList<InstallStatusListener>();

	/**
	 * the top-level directory beneath which reside all files needed for updating the application (e.g.
	 * /opt/squirrel/update)
	 */
	private File updateDir = null;

	// Download directories
	
	/** the downlaods root directory (e.g. /opt/squirrel/update/downloads) */
	private File downloadsRootDir = null;
	
	/** the core sub-directory of the backup directory (e.g. /opt/squirrel/update/downloads/core) */
	private File coreDownloadsDir = null;

	/** the plugin sub-directory of the backup directory (e.g. /opt/squirrel/update/downloads/plugin) */
	private File pluginDownloadsDir = null;
	
	/** the i18n sub-directory of the backup directory (e.g. /opt/squirrel/update/downloads/i18n) */
	private File i18nDownloadsDir = null;	
	
	// Backup directories

	/** the backup directory (e.g. /opt/squirrel/update/backup) */
	private File backupRootDir = null;

	/** the core sub-directory of the backup directory (e.g. /opt/squirrel/update/backup/core) */
	private File coreBackupDir = null;

	/** the plugin sub-directory of the backup directory (e.g. /opt/squirrel/update/backup/plugin) */
	private File pluginBackupDir = null;

	/** the i18n sub-directory of the backup directory (e.g. /opt/squirrel/update/backup/i18n) */
	private File translationBackupDir = null;

	// Install directories

	/** the top-level SQuirreL installation direction where launch scripts are (e.g. /opt/squirrel) */
	private File installRootDir = null;

	/** the lib directory where most core jars are (e.g. /opt/squirrel/lib) */
	private File coreInstallDir = null;

	/** the plugins directory where all of the plugin files are (e.g. /opt/squirrel/plugins) */
	private File pluginInstallDir = null;

	/** the lib directory where translation jars are (e.g. /opt/squirrel/lib) */
	private File i18nInstallDir = null;
	
	
	/* Spring-injected dependencies */

	/** Spring-injected factory for creating install events */
	private InstallStatusEventFactory installStatusEventFactory = null;
	public void setInstallStatusEventFactory(InstallStatusEventFactory installStatusEventFactory)
	{
		this.installStatusEventFactory = installStatusEventFactory;
	}

	/** Spring-injected factory for creating file operation infos */
	private InstallFileOperationInfoFactory installFileOperationInfoFactory = null;
	public void setInstallFileOperationInfoFactory(
		InstallFileOperationInfoFactory installFileOperationInfoFactory)
	{
		this.installFileOperationInfoFactory = installFileOperationInfoFactory;
	}

	/** Utility which provides path information and abstraction to file operations */
	private UpdateUtil _util = null;
	public void setUpdateUtil(UpdateUtil util)
	{
		this._util = util;
		updateDir = _util.getSquirrelUpdateDir();
		backupRootDir = _util.checkDir(updateDir, UpdateUtil.BACKUP_ROOT_DIR_NAME);

		coreBackupDir = _util.checkDir(backupRootDir, UpdateUtil.CORE_ARTIFACT_ID);
		pluginBackupDir = _util.checkDir(backupRootDir, UpdateUtil.PLUGIN_ARTIFACT_ID);
		translationBackupDir = _util.checkDir(backupRootDir, UpdateUtil.TRANSLATION_ARTIFACT_ID);

		installRootDir = _util.getSquirrelHomeDir();

		coreInstallDir = _util.getSquirrelLibraryDir();
		pluginInstallDir = _util.getSquirrelPluginsDir();
		i18nInstallDir = _util.getSquirrelLibraryDir();
		
		coreDownloadsDir = _util.getCoreDownloadsDir();
		pluginDownloadsDir = _util.getPluginDownloadsDir();
		i18nDownloadsDir = _util.getI18nDownloadsDir();
	}	
	
	
	/**
	 * @param changeList
	 * @throws FileNotFoundException
	 */
	public void setChangeList(ChangeListXmlBean changeList) throws FileNotFoundException
	{
		_changeListBean = changeList;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.gui.installer.ArtifactInstaller#addListener(net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallStatusListener)
	 */
	public void addListener(InstallStatusListener listener)
	{
		_listeners.add(listener);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.gui.installer.ArtifactInstaller#backupFiles()
	 */
	public boolean backupFiles() throws FileNotFoundException, IOException
	{
		boolean result = true;
		sendBackupStarted();

		List<ArtifactStatus> stats = _changeListBean.getChanges();
		for (ArtifactStatus status : stats)
		{
			String artifactName = status.getName();
			String artifactType = status.getType();
			// Skip files that are not installed - new files
			if (!status.isInstalled())
			{
				if (s_log.isInfoEnabled())
				{
					s_log.info("Skipping backup of artifact (" + status + ") which isn't installed.");
				}
				continue;
			}
			if (status.isCoreArtifact())
			{

				File installDir = coreInstallDir;
				if (artifactName.equals("squirrel-sql.jar"))
				{
					installDir = installRootDir;
				}
				File coreFile = _util.getFile(installDir, artifactName);
				File backupFile = _util.getFile(coreBackupDir, artifactName);
				_util.copyFile(coreFile, backupFile);
			}
			if (status.isPluginArtifact())
			{
				// artifact name for plugins is <plugin internal name>.zip
				File pluginBackupFile = _util.getFile(pluginBackupDir, artifactName);
				String pluginDirectory = artifactName.replace(".zip", "");
				String pluginJarFilename = artifactName.replace(".zip", ".jar");
				File[] sourceFiles = new File[2];
				sourceFiles[0] = _util.getFile(pluginInstallDir, pluginDirectory);
				sourceFiles[1] = _util.getFile(pluginInstallDir, pluginJarFilename);
				_util.createZipFile(pluginBackupFile, sourceFiles);
			}
			if (status.isTranslationArtifact())
			{
				File translationFile = _util.getFile(i18nInstallDir, artifactName);
				File backupFile = _util.getFile(translationBackupDir, artifactName);
				if (_util.fileExists(translationFile))
				{
					_util.copyFile(translationFile, backupFile);
				}
			}
		}

		sendBackupComplete();
		return result;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.gui.installer.ArtifactInstaller#installFiles()
	 */
	public void installFiles()
	{
		sendInstallStarted();

		List<File> filesToRemove = new ArrayList<File>();
		List<InstallFileOperationInfo> filesToInstall = new ArrayList<InstallFileOperationInfo>();

		for (ArtifactStatus status : _changeListBean.getChanges())
		{
			ArtifactAction action = status.getArtifactAction();
			File installDir = null;
			File fileToCopy = null;
			File fileToRemove = null;
			String artifactName = status.getName();
			boolean isPlugin = false;
			switch (action)
			{
			case INSTALL:
				if (status.isCoreArtifact())
				{
					/* Handle squirrel-sql.jar specially - it lives at the top */
					if ("squirrel-sql.jar".equals(status.getName())) {
						installDir = installRootDir;
					} else {
						installDir = coreInstallDir;
					}
					fileToCopy = _util.getFile(coreDownloadsDir, artifactName);
					fileToRemove = _util.getFile(installDir, artifactName);
					filesToRemove.add(fileToRemove);					
				}
				if (status.isPluginArtifact())
				{
					isPlugin = true;
					installDir = pluginInstallDir;
					
					// no, the file is a zip, need to extract it to the plugins directory.  All zips are packaged
					// in such a way that the extraction beneath plugins directory is all that is required.
					fileToCopy = _util.getFile(pluginDownloadsDir, artifactName);
					
					// Need to remove the existing jar in the plugins directory 
					fileToRemove = _util.getFile(installDir, artifactName);
					filesToRemove.add(fileToRemove);
					
				}
				if (status.isTranslationArtifact())
				{
					installDir = i18nInstallDir;
					fileToCopy = _util.getFile(i18nDownloadsDir, artifactName);
					fileToRemove = _util.getFile(installDir, artifactName);
					filesToRemove.add(fileToRemove);
				}
				InstallFileOperationInfo info = installFileOperationInfoFactory.create(fileToCopy, installDir);
				info.setPlugin(isPlugin);
				filesToInstall.add(info);
				break;
			case REMOVE:
				if (status.isCoreArtifact()) {
					s_log.error("Skipping core artifact ("+status.getName()+") that was marked for removal");
				}
				break;
			default:
				// log error
			}
		}
		removeOldFiles(filesToRemove);
		installFiles(filesToInstall);
		sendInstallComplete();
	}

	// Helper methods

	private void removeOldFiles(List<File> filesToRemove)
	{
		for (File fileToRemove : filesToRemove) {
			removeOldFile(fileToRemove);			
		}
	}

	private void removeOldFile(File fileToRemove)
	{
		if (!fileToRemove.exists()) {
			if (s_log.isInfoEnabled()) {
				s_log.info("Skipping delete of file doesn't appear to exist: "+fileToRemove.getAbsolutePath());
			}
			return;
		}
		try {
			boolean success = fileToRemove.delete();
			if (!success) {
				s_log.error("Delete operation failed for file: "+fileToRemove.getAbsolutePath());
			}
		} catch (SecurityException e) {
			s_log.error("Unexpected security exception: "+e.getMessage());
		}
	}

	private void installFiles(List<InstallFileOperationInfo> filesToInstall)
	{
		for (InstallFileOperationInfo info : filesToInstall) {
			File installDir = info.getInstallDir();
			File fileToCopy = info.getFileToInstall();
			installFile(installDir, fileToCopy);
		}
	}

	private void installFile(File installDir, File fileToCopy)
	{
		// TODO: implement.
	}

	private void sendBackupStarted()
	{
		if (s_log.isInfoEnabled())
		{
			s_log.info("Backup started");
		}

		InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.BACKUP_STARTED);
		sendEvent(evt);
	}

	private void sendBackupComplete()
	{
		if (s_log.isInfoEnabled())
		{
			s_log.info("Backup complete");
		}
		InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.BACKUP_COMPLETE);
		sendEvent(evt);
	}

	private void sendInstallStarted()
	{
		if (s_log.isInfoEnabled())
		{
			s_log.info("Install started");
		}
		InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.INSTALL_STARTED);
		sendEvent(evt);
	}

	private void sendInstallComplete()
	{
		if (s_log.isInfoEnabled())
		{
			s_log.info("Install completed");
		}
		InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.INSTALL_COMPLETE);
		sendEvent(evt);
	}

	private void sendEvent(InstallStatusEvent evt)
	{
		for (InstallStatusListener listener : _listeners)
		{
			listener.handleInstallStatusEvent(evt);
		}
	}

}
