/*
 * Copyright (C) 2008 Rob Manning
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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactAction;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallEventType;
import net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallStatusEvent;
import net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallStatusEventFactory;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import utils.EasyMockHelper;

public class ArtifactInstallerImplTest extends BaseSQuirreLJUnit4TestCase {

   ArtifactInstallerImpl implUnderTest = null;
   
   EasyMockHelper helper = new EasyMockHelper();
   
   private ChangeListXmlBean mockChangeListBean = helper.createMock(ChangeListXmlBean.class); 
   private UpdateUtil mockUpdateUtil = helper.createMock(UpdateUtil.class);
   private InstallStatusEventFactory mockInstallStatusEventFactory = 
      helper.createMock(InstallStatusEventFactory.class);
   private InstallStatusEvent mockInstallStatusEvent = 
      helper.createMock(InstallStatusEvent.class);
   
   /* Test Constants */
   private static final String FW_JAR_FILENAME = "fw.jar";
   private static final String SQUIRREL_SQL_JAR_FILENAME = "squirrel-sql.jar";
   private static final String SPRING_JAR_FILENAME = "spring.jar";
   private static final String DBCOPY_ZIP_FILENAME = "dbcopy.zip";
   private static final String SQUIRREL_SQL_ES_JAR_FILENAME = "squirrel-sql_es.jar";
   
//   private static final String TEST_SQUIRREL_HOME_DIR_NAME = "/squirrel/updates";
//   private static final File TEST_UPDATE_DIR = new File(TEST_SQUIRREL_HOME_DIR_NAME);
//   
//   private static final String TEST_BACKUP_ROOT_DIR_NAME = 
//      TEST_SQUIRREL_HOME_DIR_NAME + "/" + UpdateUtil.BACKUP_ROOT_DIR_NAME;
//   private static final File TEST_BACKUP_ROOT_DIR = new File(TEST_BACKUP_ROOT_DIR_NAME);
//   
//   private static final String TEST_BACKUP_CORE_DIR_NAME = 
//      TEST_BACKUP_ROOT_DIR_NAME + "/" + UpdateUtil.CORE_ARTIFACT_ID; 
//   private static final File TEST_BACKUP_CORE_DIR = new File(TEST_BACKUP_CORE_DIR_NAME);
//   private static final File TEST_BACKUP_PLUGINS_DIR = new File("/squirrel/updates/backup/plugins");

   private File mockSquirreHomeDirFile = helper.createMock(File.class);
   private File mockSquirreLibDirFile = helper.createMock(File.class);
   private File mockSquirrelPluginsDirFile = helper.createMock(File.class); 
   private File mockUpdateRootDirFile = helper.createMock(File.class);
   private File mockBackupRootDirFile = helper.createMock(File.class);
   private File mockBackupCoreDirFile = helper.createMock(File.class);
   private File mockBackupPluginDirFile = helper.createMock(File.class);
   private File mockBackupTranslationDirFile = helper.createMock(File.class);
   private File mockInstalledFrameworkJarFile = helper.createMock(File.class);
   private File mockBackupFrameworkJarFile = helper.createMock(File.class);
   private File mockInstalledSquirrelSqlJarFile = helper.createMock(File.class);
   private File mockBackupSquirrelSqlJarFile = helper.createMock(File.class);
   private File mockBackupDbCopyZipFile = helper.createMock(File.class);
   private File mockInstalledDbCopyPluginDirFile = helper.createMock(File.class);
	private File mockInstalledDbCopyPluginJarFile = helper.createMock(File.class);
	private File mockInstalledSquirrelSqlEsJarFile = helper.createMock(File.class);
	private File mockBackupSquirrelSqlEsJarFile = helper.createMock(File.class);
   
   @Before
   public void setUp() throws Exception {
      helper.resetAll();
      setupUpdateUtil();
   }

   private void setupUpdateUtil() {
      expect(mockUpdateUtil.getSquirrelHomeDir()).andReturn(mockSquirreHomeDirFile).anyTimes();
      expect(mockUpdateUtil.getSquirrelUpdateDir()).andReturn(mockUpdateRootDirFile).anyTimes();
      expect(mockUpdateUtil.getSquirrelLibraryDir()).andReturn(mockSquirreLibDirFile).anyTimes();
      expect(mockUpdateUtil.getSquirrelPluginsDir()).andReturn(mockSquirrelPluginsDirFile).anyTimes();
      
   }

   @After
   public void tearDown() throws Exception {
      implUnderTest = null;
   }

   @Test
   public final void testBackupFiles() throws Exception {
      
      /* expectations that are specific to this test */
      expect(mockUpdateUtil.checkDir(mockUpdateRootDirFile, UpdateUtil.BACKUP_ROOT_DIR_NAME))
         .andReturn(mockBackupRootDirFile);
      expect(mockUpdateUtil.checkDir(mockBackupRootDirFile, UpdateUtil.CORE_ARTIFACT_ID))
         .andReturn(mockBackupCoreDirFile);
      expect(mockUpdateUtil.checkDir(mockBackupRootDirFile, UpdateUtil.PLUGIN_ARTIFACT_ID))
         .andReturn(mockBackupPluginDirFile);
      expect(mockUpdateUtil.checkDir(mockBackupRootDirFile, UpdateUtil.TRANSLATION_ARTIFACT_ID))
         .andReturn(mockBackupTranslationDirFile);
      
      expect(mockUpdateUtil.getFile(mockSquirreLibDirFile, FW_JAR_FILENAME)).andReturn(
		   mockInstalledFrameworkJarFile);
		expect(mockUpdateUtil.fileExists(mockInstalledFrameworkJarFile)).andReturn(true);
		expect(mockUpdateUtil.getFile(mockBackupCoreDirFile, FW_JAR_FILENAME)).andReturn(
		   mockBackupFrameworkJarFile);
		mockUpdateUtil.copyFile(mockInstalledFrameworkJarFile, mockBackupFrameworkJarFile);

		expect(mockUpdateUtil.getFile(mockSquirreHomeDirFile, SQUIRREL_SQL_JAR_FILENAME)).andReturn(
		   mockInstalledSquirrelSqlJarFile);
		expect(mockUpdateUtil.fileExists(mockInstalledSquirrelSqlJarFile)).andReturn(true);
		expect(mockUpdateUtil.getFile(mockBackupCoreDirFile, SQUIRREL_SQL_JAR_FILENAME)).andReturn(
		   mockBackupSquirrelSqlJarFile);
		mockUpdateUtil.copyFile(mockInstalledSquirrelSqlJarFile, mockBackupSquirrelSqlJarFile);      
      
		expect(mockUpdateUtil.getFile(mockBackupPluginDirFile, DBCOPY_ZIP_FILENAME)).andReturn(
		   mockBackupDbCopyZipFile);
		
		expect(mockUpdateUtil.getFile(mockSquirrelPluginsDirFile, "dbcopy")).andReturn(
		   mockInstalledDbCopyPluginDirFile);
		expect(mockUpdateUtil.getFile(mockSquirrelPluginsDirFile, "dbcopy.jar")).andReturn(
		   mockInstalledDbCopyPluginJarFile);
		mockUpdateUtil.createZipFile(isA(File.class), isA(File[].class));
		
		expect(mockUpdateUtil.getFile(mockSquirreLibDirFile, SQUIRREL_SQL_ES_JAR_FILENAME)).andReturn(
		   mockInstalledSquirrelSqlEsJarFile);
		expect(mockUpdateUtil.getFile(mockBackupTranslationDirFile, SQUIRREL_SQL_ES_JAR_FILENAME)).andReturn(
		   mockBackupSquirrelSqlEsJarFile);
		expect(mockUpdateUtil.fileExists(mockInstalledSquirrelSqlEsJarFile)).andReturn(true);
		mockUpdateUtil.copyFile(mockInstalledSquirrelSqlEsJarFile, mockBackupSquirrelSqlEsJarFile);
		
      expect(mockInstallStatusEventFactory.create(InstallEventType.BACKUP_STARTED))
         .andReturn(mockInstallStatusEvent);
      expect(mockInstallStatusEventFactory.create(InstallEventType.BACKUP_COMPLETE))
         .andReturn(mockInstallStatusEvent);
      
      
      
      expect(mockChangeListBean.getChanges()).andReturn(buildChangeList());
      
      helper.replayAll();
      implUnderTest = new ArtifactInstallerImpl(mockUpdateUtil, mockChangeListBean);
      implUnderTest.setInstallStatusEventFactory(mockInstallStatusEventFactory);
      
      implUnderTest.backupFiles();
      helper.verifyAll();
   }

   private List<ArtifactStatus> buildChangeList() {
      ArrayList<ArtifactStatus> result = new ArrayList<ArtifactStatus>();
      
      final boolean installed = true;
      final boolean notInstalled = false;
      final String coreType = UpdateUtil.CORE_ARTIFACT_ID;
      final String pluginType = UpdateUtil.PLUGIN_ARTIFACT_ID;
      final String i18nType = UpdateUtil.TRANSLATION_ARTIFACT_ID;
      
      ArtifactStatus newSquirrelSqlJar = getArtifactToInstall(SQUIRREL_SQL_JAR_FILENAME, true, coreType);
		ArtifactStatus newFrameworkJar = getArtifactToInstall(FW_JAR_FILENAME, installed, coreType);
		ArtifactStatus newSpringJar = getArtifactToInstall(SPRING_JAR_FILENAME, notInstalled, coreType);
      ArtifactStatus newDbcopyZip = getArtifactToInstall(DBCOPY_ZIP_FILENAME, installed, pluginType);
		ArtifactStatus newSquirrelSqlEsJar = getArtifactToInstall(
		   SQUIRREL_SQL_ES_JAR_FILENAME, installed, i18nType);
      
      result.add(newSquirrelSqlJar);
      result.add(newFrameworkJar);
      result.add(newSpringJar);
      result.add(newDbcopyZip);
      result.add(newSquirrelSqlEsJar);
      return result;
   }

   private ArtifactStatus getArtifactToInstall(String name, boolean installed, String type) {
      ArtifactStatus result = new ArtifactStatus();
      result.setArtifactAction(ArtifactAction.INSTALL);
      result.setName(name);
      result.setInstalled(installed);
      result.setType(type);
      return result;
   }
   
   @Test @Ignore
   public final void testInstallFiles() throws IOException {
      helper.replayAll();
      implUnderTest = new ArtifactInstallerImpl(mockUpdateUtil, mockChangeListBean);
      implUnderTest.setInstallStatusEventFactory(mockInstallStatusEventFactory);
      
      implUnderTest.installFiles();
      helper.verifyAll();
   }

}
