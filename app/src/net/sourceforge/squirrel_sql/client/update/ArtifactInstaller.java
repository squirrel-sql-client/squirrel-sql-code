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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializer;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This class is used by the updater to install artifact files
 * 
 * @author manningr
 */
public class ArtifactInstaller {
   
   /** Logger for this class. */
   private static ILogger s_log = 
      LoggerController.createLogger(ArtifactInstaller.class);

   
   private UpdateUtil _util = null;
   private ChangeListXmlBean _changeListBean = null;
   
   private UpdateXmlSerializer _serializer = new UpdateXmlSerializer();
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
   
   public ArtifactInstaller(UpdateUtil util, File changeList) 
      throws FileNotFoundException 
   {
      this._util = util;
      _changeListBean = _serializer.readChangeListBean(changeList);
      updateDir = _util.getSquirrelUpdateDir();
      backupRootDir = _util.checkDir(updateDir, "backup");
      
      coreBackupDir = 
         _util.checkDir(backupRootDir, ArtifactStatus.CORE_ARTIFACT_ID);
      pluginBackupDir = 
         _util.checkDir(backupRootDir, ArtifactStatus.PLUGIN_ARTIFACT_ID);
      translationBackupDir = 
         _util.checkDir(backupRootDir, ArtifactStatus.TRANSLATION_ARTIFACT_ID);
      
      installRootDir = _util.getSquirrelHomeDir();
      
      coreInstallDir = _util.getSquirrelLibraryDir();
      pluginInstallDir = _util.getSquirrelPluginsDir();
      translationInstallDir = _util.getSquirrelLibraryDir();
   }
   
   public void addListener(InstallStatusListener listener) {
      _listeners.add(listener);
   }
   
   public void backupFiles() throws FileNotFoundException, IOException {
      sendBackupStarted();
      List<ArtifactStatus> stats = 
         (List<ArtifactStatus>)_changeListBean.getChanges();
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
         if (ArtifactStatus.CORE_ARTIFACT_ID.equals(artifactType)) {
            File coreFile = new File(coreInstallDir, artifactName);
            if (artifactName.equals("squirrel-sql.jar")) {
               coreFile = new File(installRootDir, artifactName);
            }
            if (!coreFile.exists()) {
               // a new core file? - skip files that don't exist
               if (s_log.isInfoEnabled()) {
                  s_log.info("Skipping backup of file (" + artifactName
                        + ") which doesn't exist.");
               }               
               continue;
            }
            File backupFile = new File(coreBackupDir, artifactName);
            _util.copyFile(coreFile, backupFile);
         }
         if (ArtifactStatus.PLUGIN_ARTIFACT_ID.equals(artifactType)) {
            // artifact name for plugins is <plugin internal name>.zip
            File pluginBackupFile = new File(pluginBackupDir, artifactName);
            String pluginDirectory = artifactName.replace(".zip", "");
            String pluginJarFilename = artifactName.replace(".zip", ".jar"); 
            File[] sourceFiles = new File[2];
            sourceFiles[0] = new File(pluginInstallDir, pluginDirectory);
            sourceFiles[1] = new File(pluginInstallDir, pluginJarFilename);
            _util.createZipFile(pluginBackupFile, sourceFiles);
         }
         if (ArtifactStatus.TRANSLATION_ARTIFACT_ID.equals(artifactType)) {
            File translationFile = new File(translationInstallDir, artifactName);
            File backupFile = new File(translationBackupDir, artifactName);
            if (translationFile.exists()) {
               _util.copyFile(translationFile, backupFile);
            }
         }
      }
      
      sendBackupComplete();
   }
   
   public void installFiles() {
      sendInstallStarted();
      
      // TODO
      
      sendInstallComplete();
   }
   
   private void sendBackupStarted() {
      if (s_log.isInfoEnabled()) {
         s_log.info("Backup started");
      }
      InstallStatusEvent evt = 
         new InstallStatusEvent(InstallEventType.BACKUP_STARTED);
      sendEvent(evt);
   }
   
   private void sendBackupComplete() {
      if (s_log.isInfoEnabled()) {
         s_log.info("Backup complete");
      }      
      InstallStatusEvent evt = 
         new InstallStatusEvent(InstallEventType.BACKUP_COMPLETE);
      sendEvent(evt);
   }   

   private void sendInstallStarted() {
      if (s_log.isInfoEnabled()) {
         s_log.info("Install started");
      }
      InstallStatusEvent evt = 
         new InstallStatusEvent(InstallEventType.INSTALL_STARTED);
      sendEvent(evt);
   }

   private void sendInstallComplete() {
      if (s_log.isInfoEnabled()) {
         s_log.info("Install completed");
      }      
      InstallStatusEvent evt = 
         new InstallStatusEvent(InstallEventType.INSTALL_COMPLETE);
      sendEvent(evt);
   }
   
   private void sendEvent(InstallStatusEvent evt) { 
      for (InstallStatusListener listener : _listeners) {
         listener.handleInstallStatusEvent(evt);
      }
   }
}
