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
import net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializer;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This class is used by the PreLaunchUpdateApplication to install artifact files
 * 
 * @author manningr
 */
public class ArtifactInstallerImpl implements ArtifactInstaller {
   
   /** Logger for this class. */
   private static ILogger s_log = 
      LoggerController.createLogger(ArtifactInstallerImpl.class);

   private UpdateUtil _util = null;
   private ChangeListXmlBean _changeListBean = null;
   
   private List<InstallStatusListener> _listeners = 
      new ArrayList<InstallStatusListener>();
   private File updateDir = null;
   
   // Backup directories
   File backupRootDir = null;
   File coreBackupDir = null;
   File pluginBackupDir = null;
   File translationBackupDir = null; 
   
   // Install directories
   
   /** the top-level SQuirreL installation direction where launch scripts are */
   File installRootDir = null;
   
   /** the lib directory where most core jars are */  
   File coreInstallDir = null;
   
   /** the plugins directory where all of the plugin files are */
   File pluginInstallDir = null;
   
   /** the lib directory where translation jars are */
   File translationInstallDir = null; 

   
   // Spring-Injected
   InstallStatusEventFactory installStatusEventFactory = null;
   public void setInstallStatusEventFactory(InstallStatusEventFactory installStatusEventFactory)
	{
		this.installStatusEventFactory = installStatusEventFactory;
	}

   // Spring-Injected
   InstallFileOperationInfoFactory installFileOperationInfoFactory = null;
   public void setInstallFileOperationInfoFactory(
         InstallFileOperationInfoFactory installFileOperationInfoFactory) {
      this.installFileOperationInfoFactory = installFileOperationInfoFactory;
   }

   /**
	 * @param util
	 * @param changeList
	 * @throws FileNotFoundException
	 */
	public ArtifactInstallerImpl(UpdateUtil util, ChangeListXmlBean changeList) 
      throws FileNotFoundException 
   {
      setUpdateUtil(util);
      setChangeList(changeList);
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
	 * @param util
	 */
	public void setUpdateUtil(UpdateUtil util)
	{
		this._util = util;
      updateDir = _util.getSquirrelUpdateDir();
      backupRootDir = _util.checkDir(updateDir, UpdateUtil.BACKUP_ROOT_DIR_NAME);
      
      coreBackupDir = 
         _util.checkDir(backupRootDir, UpdateUtil.CORE_ARTIFACT_ID);
      pluginBackupDir = 
         _util.checkDir(backupRootDir, UpdateUtil.PLUGIN_ARTIFACT_ID);
      translationBackupDir = 
         _util.checkDir(backupRootDir, UpdateUtil.TRANSLATION_ARTIFACT_ID);
      
      installRootDir = _util.getSquirrelHomeDir();
      
      coreInstallDir = _util.getSquirrelLibraryDir();
      pluginInstallDir = _util.getSquirrelPluginsDir();
      translationInstallDir = _util.getSquirrelLibraryDir();		
	}
   
   /**
	 * @see net.sourceforge.squirrel_sql.client.update.gui.installer.ArtifactInstaller#addListener(net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallStatusListener)
	 */
   public void addListener(InstallStatusListener listener) {
      _listeners.add(listener);
   }
   
   /**
	 * @see net.sourceforge.squirrel_sql.client.update.gui.installer.ArtifactInstaller#backupFiles()
	 */
   public boolean backupFiles() throws FileNotFoundException, IOException {
      boolean result = true;
      sendBackupStarted();
      
      List<ArtifactStatus> stats = _changeListBean.getChanges();
      for (ArtifactStatus status : stats) {
         String artifactName = status.getName();
         String artifactType = status.getType();
         // Skip files that are not installed - new files
         if (!status.isInstalled()) {
            if (s_log.isInfoEnabled()) {
               s_log.info("Skipping backup of file (" + artifactName
                     + ") which isn't installed.");
            }
            continue;
         }
         if (UpdateUtil.CORE_ARTIFACT_ID.equals(artifactType)) {
            
            File installDir = coreInstallDir;
            if (artifactName.equals("squirrel-sql.jar")) {
               installDir = installRootDir;
            }
            File coreFile = _util.getFile(installDir, artifactName);
               
            if (!_util.fileExists(coreFile)) {
               // a new core file? - skip files that don't exist
               if (s_log.isInfoEnabled()) {
                  s_log.info("Skipping backup of file (" + artifactName
                        + ") which doesn't exist.");
               }               
               continue;
            }
            File backupFile = _util.getFile(coreBackupDir, artifactName);
            _util.copyFile(coreFile, backupFile);
         }
         if (UpdateUtil.PLUGIN_ARTIFACT_ID.equals(artifactType)) {
            // artifact name for plugins is <plugin internal name>.zip
            File pluginBackupFile = _util.getFile(pluginBackupDir, artifactName);
            String pluginDirectory = artifactName.replace(".zip", "");
            String pluginJarFilename = artifactName.replace(".zip", ".jar"); 
            File[] sourceFiles = new File[2];
            sourceFiles[0] = _util.getFile(pluginInstallDir, pluginDirectory);
            sourceFiles[1] = _util.getFile(pluginInstallDir, pluginJarFilename);
            _util.createZipFile(pluginBackupFile, sourceFiles);
         }
         if (UpdateUtil.TRANSLATION_ARTIFACT_ID.equals(artifactType)) {
            File translationFile = _util.getFile(translationInstallDir, artifactName);
            File backupFile = _util.getFile(translationBackupDir, artifactName);
            if (_util.fileExists(translationFile)) {
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
   public void installFiles() {
      sendInstallStarted();
      
      List<File> filesToRemove = new ArrayList<File>();
      List<InstallFileOperationInfo> filesToInstall = 
         new ArrayList<InstallFileOperationInfo>();
      
      for (ArtifactStatus status : _changeListBean.getChanges()) {
      	ArtifactAction action = status.getArtifactAction();
      	File installDir = null;
      	File fileToCopy = null;
      	File fileToRemove = null;
      	String artifactName = status.getName();
      	switch (action) {
      		case INSTALL:
      			if (status.isCoreArtifact()) {
      				installDir = _util.getSquirrelLibraryDir();
      				fileToCopy = new File(_util.getCoreDownloadsDir(), artifactName);
      				
      			}
      			if (status.isPluginArtifact()) {
      				installDir = _util.getSquirrelPluginsDir();
      				fileToCopy = new File(_util.getPluginDownloadsDir(), artifactName);      				
      			}
      			if (status.isTranslationArtifact()) {
      				installDir = _util.getSquirrelLibraryDir();
      				fileToCopy = new File(_util.getI18nDownloadsDir(), artifactName);
      			}
      			break;
      		case REMOVE:
      			
      			break;
      		default:
      			// log error
      	}
      	fileToRemove = new File(installDir, artifactName);
      	filesToRemove.add(fileToRemove);
      	
      	InstallFileOperationInfo info = installFileOperationInfoFactory.create(fileToCopy, installDir);
      	filesToInstall.add(info);
      }
      removeOldFiles(filesToRemove);
      installFiles(filesToInstall);      
      sendInstallComplete();
   }
   
   // Helper methods
   
   private void removeOldFiles(List<File> filesToRemove) {
      // TODO: complete    
   }
   
   private void removeOldFile(File fileToRemove) {
   	// TODO: complete   	
   }
   
   private void installFiles(List<InstallFileOperationInfo> filesToInstall) {
      // TODO: complete          
   }
   
   private void installFile(File installDir, File FileToCopy) {
   	// TODO: complete
   }
   
   private void sendBackupStarted() {
      if (s_log.isInfoEnabled()) {
         s_log.info("Backup started");
      }
      
      InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.BACKUP_STARTED);
      sendEvent(evt);
   }
   
   private void sendBackupComplete() {
      if (s_log.isInfoEnabled()) {
         s_log.info("Backup complete");
      }      
      InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.BACKUP_COMPLETE);
      sendEvent(evt);
   }   

   private void sendInstallStarted() {
      if (s_log.isInfoEnabled()) {
         s_log.info("Install started");
      }
      InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.INSTALL_STARTED);
      sendEvent(evt);
   }

   private void sendInstallComplete() {
      if (s_log.isInfoEnabled()) {
         s_log.info("Install completed");
      }      
      InstallStatusEvent evt = installStatusEventFactory.create(InstallEventType.INSTALL_COMPLETE);
      sendEvent(evt);
   }
   
   private void sendEvent(InstallStatusEvent evt) { 
      for (InstallStatusListener listener : _listeners) {
         listener.handleInstallStatusEvent(evt);
      }
   }
   
}
