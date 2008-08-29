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

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

import java.io.FileNotFoundException;
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
import net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallStatusListener;
import net.sourceforge.squirrel_sql.client.update.gui.installer.util.InstallFileOperationInfo;
import net.sourceforge.squirrel_sql.client.update.gui.installer.util.InstallFileOperationInfoFactory;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

public class ArtifactInstallerImplTest extends BaseSQuirreLJUnit4TestCase
{

	ArtifactInstallerImpl implUnderTest = null;

	EasyMockHelper helper = new EasyMockHelper();

	private ChangeListXmlBean mockChangeListBean = helper.createMock(ChangeListXmlBean.class);

	private UpdateUtil mockUpdateUtil = helper.createMock(UpdateUtil.class);

	private InstallStatusEventFactory mockInstallStatusEventFactory =
		helper.createMock(InstallStatusEventFactory.class);

	private InstallStatusEvent mockBackupStartedStatusEvent = helper.createMock(InstallStatusEvent.class);

	private InstallStatusEvent mockBackupCompletedStatusEvent = helper.createMock(InstallStatusEvent.class);

	private InstallStatusEvent mockInstallStartedStatusEvent = helper.createMock(InstallStatusEvent.class);

	private InstallStatusEvent mockInstallCompletedStatusEvent = helper.createMock(InstallStatusEvent.class);

	private InstallFileOperationInfoFactory mockInstallFileOperationInfoFactory =
		helper.createMock(InstallFileOperationInfoFactory.class);

	private InstallStatusListener mockInstallStatusListener = helper.createMock(InstallStatusListener.class);

	/* Test Constants */
	private static final String FW_JAR_FILENAME = "fw.jar";

	private static final String SQUIRREL_SQL_JAR_FILENAME = "squirrel-sql.jar";

	private static final String SPRING_JAR_FILENAME = "spring.jar";

	private static final String DBCOPY_ZIP_FILENAME = "dbcopy.zip";
	
	private static final String DBCOPY_JAR_FILENAME = "dbcopy.jar";
	
	private static final String DBCOPY_DIR_FILENAME = "dbcopy";

	private static final String SQUIRREL_SQL_ES_JAR_FILENAME = "squirrel-sql_es.jar";
	
	private static final String LOCAL_RELEASE_FILENAME = "release.xml";
	
	private FileWrapper mockSquirreLHomeDirFile = helper.createMock("mockSquirreHomeDirFile", FileWrapper.class);

	private FileWrapper mockSquirreLLibDirFile = helper.createMock("mockSquirreLLibDirFile", FileWrapper.class);

	private FileWrapper mockSquirrelPluginsDirFile = helper.createMock("mockSquirrelPluginsDirFile", FileWrapper.class);

	private FileWrapper mockUpdateRootDirFile = helper.createMock("mockUpdateRootDirFile", FileWrapper.class);

	private FileWrapper mockBackupRootDirFile = helper.createMock("mockBackupRootDirFile", FileWrapper.class);

	private FileWrapper mockBackupCoreDirFile = helper.createMock("mockBackupCoreDirFile", FileWrapper.class);

	private FileWrapper mockBackupPluginDirFile = helper.createMock("mockBackupPluginDirFile", FileWrapper.class);

	private FileWrapper mockBackupTranslationDirFile = helper.createMock("mockBackupTranslationDirFile", FileWrapper.class);

	private FileWrapper mockBackupFrameworkJarFile = helper.createMock("mockBackupFrameworkJarFile", FileWrapper.class);

	private FileWrapper mockBackupSquirrelSqlJarFile = helper.createMock("mockBackupSquirrelSqlJarFile", FileWrapper.class);

	private FileWrapper mockBackupDbCopyZipFile = helper.createMock("mockBackupDbCopyZipFile", FileWrapper.class);

	private FileWrapper mockInstalledSquirrelSqlJarFile = helper.createMock("mockInstalledSquirrelSqlJarFile", FileWrapper.class);

	private FileWrapper mockInstalledFrameworkJarFile = helper.createMock("mockInstalledFrameworkJarFile", FileWrapper.class);
	
	private FileWrapper mockInstalledDbCopyPluginDirFile = helper.createMock("mockInstalledDbCopyPluginDirFile", FileWrapper.class);

	private FileWrapper mockInstalledSquirrelSqlEsJarFile = helper.createMock("mockInstalledSquirrelSqlEsJarFile", FileWrapper.class);

	private FileWrapper mockInstalledDbCopyZipFile = helper.createMock("mockInstalledDbCopyZipFile", FileWrapper.class);
	
	private FileWrapper mockInstalledSpringJarFile = helper.createMock("mockInstalledSpringJarFile", FileWrapper.class);
	
	private FileWrapper mockBackupSquirrelSqlEsJarFile = helper.createMock("mockBackupSquirrelSqlEsJarFile", FileWrapper.class);

	private FileWrapper mockDownloadsRootDirFile = helper.createMock("mockDownloadsRootDirFile", FileWrapper.class);
	
	private FileWrapper mockDownloadsCoreDirFile = helper.createMock("mockDownloadsCoreDirFile", FileWrapper.class);

	private FileWrapper mockDownloadsPluginDirFile = helper.createMock("mockDownloadsPluginDirFile", FileWrapper.class);

	private FileWrapper mockDownloadsFrameworkJarFile = helper.createMock("mockDownloadsFrameworkJarFile", FileWrapper.class);

	private FileWrapper mockDownloadsSquirrelSqlJarFile = helper.createMock("mockDownloadsSquirrelSqlJarFile", FileWrapper.class);

	private FileWrapper mockDownloadsSpringJarFile = helper.createMock("mockDownloadsSpringJarFile", FileWrapper.class);

	private FileWrapper mockDownloadsDbCopyPluginZipFile = helper.createMock("mockDownloadsDbCopyPluginZipFile", FileWrapper.class);
	
	private FileWrapper mockDownloadsSquirrelSqlEsJarFile = helper.createMock("mockDownloadsSquirrelSqlEsJarFile", FileWrapper.class);
	
	private FileWrapper mockPathToInstalledDBCopyJarFile = helper.createMock("mockPathToInstalledDBCopyJarFile", FileWrapper.class);
	
	private FileWrapper mockPathToInstalledDBCopyDirectory = helper.createMock("mockPathToInstalledDBCopyDirectory", FileWrapper.class);
	
	private FileWrapper mockDownloadsI18nDirFile = helper.createMock("mockDownloadsI18nDirFile", FileWrapper.class);

	private FileWrapper mockLocalReleaseFile = helper.createMock("mockLocalReleaseFile", FileWrapper.class);
	
	private FileWrapper mockBackupReleaseFile = helper.createMock("mockBackupReleaseFile", FileWrapper.class);
	
	private FileWrapper mockDownloadReleaseFile = helper.createMock("mockDownloadReleaseFile", FileWrapper.class);
	
	private InstallFileOperationInfo mockInstallSquirrelSqlJarOperationInfo =
		helper.createMock(InstallFileOperationInfo.class);

	private InstallFileOperationInfo mockInstallFrameworkJarOperationInfo =
		helper.createMock(InstallFileOperationInfo.class);

	private InstallFileOperationInfo mockInstallSpringJarOperationInfo =
		helper.createMock(InstallFileOperationInfo.class);

	private InstallFileOperationInfo mockInstallDbCopyZipOperationInfo = 
		helper.createMock(InstallFileOperationInfo.class);

	private InstallFileOperationInfo mockInstallSquirrelSqlEsOperationInfo = 
		helper.createMock(InstallFileOperationInfo.class);

	
	
	@Before
	public void setUp() throws Exception
	{
		helper.resetAll();
		setupUpdateUtil();
		setupFileAbsolutePathExpectations();
		setupFileLengthExpectations();
		setupFileExistsExpectations();
		setupGetFileExpectations();
		makeCommonUpdateUtilAssertions();
	}
	
	private void setupGetFileExpectations()
	{
		expect(mockUpdateUtil.getFile(mockSquirreLHomeDirFile, SQUIRREL_SQL_JAR_FILENAME)).andStubReturn(
			mockInstalledSquirrelSqlJarFile);		
	
		expect(mockUpdateUtil.getFile(mockBackupPluginDirFile, "dbcopy.zip")).andStubReturn(
			mockBackupDbCopyZipFile);
		
		expect(mockUpdateUtil.getFile(mockSquirrelPluginsDirFile, "dbcopy")).andStubReturn(
			mockInstalledDbCopyPluginDirFile);
		
		expect(mockUpdateUtil.getFile(mockSquirrelPluginsDirFile, "dbcopy.jar")).andStubReturn(
			mockPathToInstalledDBCopyJarFile);
		
		expect(mockUpdateUtil.getFile(mockSquirreLLibDirFile, SQUIRREL_SQL_ES_JAR_FILENAME)).andStubReturn(
			mockInstalledSquirrelSqlEsJarFile);
		
		expect(mockUpdateUtil.getFile(mockBackupTranslationDirFile, SQUIRREL_SQL_ES_JAR_FILENAME)).andStubReturn(
			mockBackupSquirrelSqlEsJarFile);
		
		expect(mockUpdateUtil.getFile(mockSquirreLLibDirFile, SPRING_JAR_FILENAME)).andStubReturn(
			mockInstalledSpringJarFile);
		
		expect(mockUpdateUtil.getFile(mockSquirreLLibDirFile, SQUIRREL_SQL_ES_JAR_FILENAME)).andStubReturn(
			mockInstalledSquirrelSqlEsJarFile);
		
		expect(mockUpdateUtil.getFile(mockSquirreLLibDirFile, FW_JAR_FILENAME)).andStubReturn(
			mockInstalledFrameworkJarFile);
		
		expect(mockUpdateUtil.getFile(mockDownloadsRootDirFile, LOCAL_RELEASE_FILENAME)).andStubReturn(
			mockDownloadReleaseFile);
		
		
	}

	private void setupFileExistsExpectations()
	{
		expect(mockSquirreLHomeDirFile.exists()).andStubReturn(true);
		expect(mockSquirreLLibDirFile.exists()).andStubReturn(true);
		expect(mockSquirrelPluginsDirFile.exists()).andStubReturn(true);
		
		expect(mockInstalledSquirrelSqlJarFile.exists()).andStubReturn(true);
		expect(mockInstalledFrameworkJarFile.exists()).andStubReturn(true);
		expect(mockInstalledSpringJarFile.exists()).andStubReturn(true);
		expect(mockInstalledSquirrelSqlEsJarFile.exists()).andStubReturn(true);
		expect(mockInstalledDbCopyPluginDirFile.exists()).andStubReturn(true);
		expect(mockInstalledDbCopyZipFile.exists()).andStubReturn(true);		
		
		expect(mockUpdateRootDirFile.exists()).andStubReturn(true);
		expect(mockBackupRootDirFile.exists()).andStubReturn(true);
		expect(mockBackupCoreDirFile.exists()).andStubReturn(true);
		expect(mockBackupPluginDirFile.exists()).andStubReturn(true);
		expect(mockBackupTranslationDirFile.exists()).andStubReturn(true);
		expect(mockBackupFrameworkJarFile.exists()).andStubReturn(true);
		expect(mockBackupSquirrelSqlJarFile.exists()).andStubReturn(true);
		expect(mockBackupDbCopyZipFile.exists()).andStubReturn(true);
		expect(mockBackupSquirrelSqlEsJarFile.exists()).andStubReturn(true);
		
		expect(mockDownloadsCoreDirFile.exists()).andStubReturn(true);
		expect(mockDownloadsPluginDirFile.exists()).andStubReturn(true);
		expect(mockDownloadsFrameworkJarFile.exists()).andStubReturn(true);
		expect(mockDownloadsSquirrelSqlJarFile.exists()).andStubReturn(true);
		expect(mockDownloadsSpringJarFile.exists()).andStubReturn(true);
		expect(mockDownloadsDbCopyPluginZipFile.exists()).andStubReturn(true);
		expect(mockDownloadsSquirrelSqlEsJarFile.exists()).andStubReturn(true);
		expect(mockPathToInstalledDBCopyJarFile.exists()).andStubReturn(true);
		expect(mockPathToInstalledDBCopyDirectory.exists()).andStubReturn(true);
	}

	private void setupUpdateUtil()
	{
		expect(mockUpdateUtil.getSquirrelHomeDir()).andReturn(mockSquirreLHomeDirFile).anyTimes();
		expect(mockUpdateUtil.getSquirrelUpdateDir()).andReturn(mockUpdateRootDirFile).anyTimes();
		expect(mockUpdateUtil.getSquirrelLibraryDir()).andReturn(mockSquirreLLibDirFile).anyTimes();
		expect(mockUpdateUtil.getSquirrelPluginsDir()).andReturn(mockSquirrelPluginsDirFile).anyTimes();
		
		implUnderTest = new ArtifactInstallerImpl();
		implUnderTest.setInstallStatusEventFactory(mockInstallStatusEventFactory);
		implUnderTest.setInstallFileOperationInfoFactory(mockInstallFileOperationInfoFactory);
		implUnderTest.addListener(mockInstallStatusListener);
	}

	private void setupFileLengthExpectations() {
		expect(mockInstalledSquirrelSqlJarFile.length()).andReturn(10L).anyTimes();
		expect(mockInstalledFrameworkJarFile.length()).andReturn(10L).anyTimes();
		expect(mockInstalledSpringJarFile.length()).andReturn(10L).anyTimes();
		expect(mockInstalledSquirrelSqlEsJarFile.length()).andReturn(10L).anyTimes();
	}
	
	private void setupFileAbsolutePathExpectations() {
		
		expect(mockSquirreLHomeDirFile.getAbsolutePath()).andReturn("mockSquirreHomeDirFile").anyTimes();

		expect(mockSquirreLLibDirFile.getAbsolutePath()).andReturn("mockSquirreLLibDirFile").anyTimes();

		expect(mockSquirrelPluginsDirFile.getAbsolutePath()).andReturn("mockSquirrelPluginsDirFile").anyTimes();

		expect(mockUpdateRootDirFile.getAbsolutePath()).andReturn("mockUpdateRootDirFile").anyTimes();

		expect(mockBackupRootDirFile.getAbsolutePath()).andReturn("mockBackupRootDirFile").anyTimes();

		expect(mockBackupCoreDirFile.getAbsolutePath()).andReturn("mockBackupCoreDirFile").anyTimes();

		expect(mockBackupPluginDirFile.getAbsolutePath()).andReturn("mockBackupPluginDirFile").anyTimes();

		expect(mockBackupTranslationDirFile.getAbsolutePath()).andReturn("mockBackupTranslationDirFile")
			.anyTimes();

		expect(mockInstalledFrameworkJarFile.getAbsolutePath()).andReturn("mockInstalledFrameworkJarFile")
			.anyTimes();

		expect(mockBackupFrameworkJarFile.getAbsolutePath()).andReturn("mockBackupFrameworkJarFile").anyTimes();

		expect(mockInstalledSquirrelSqlJarFile.getAbsolutePath()).andReturn("mockInstalledSquirrelSqlJarFile")
			.anyTimes();

		expect(mockBackupSquirrelSqlJarFile.getAbsolutePath()).andReturn("mockBackupSquirrelSqlJarFile")
			.anyTimes();

		expect(mockBackupDbCopyZipFile.getAbsolutePath()).andReturn("mockBackupDbCopyZipFile").anyTimes();

		expect(mockInstalledDbCopyPluginDirFile.getAbsolutePath()).andReturn("mockInstalledDbCopyPluginDirFile")
			.anyTimes();

		expect(mockInstalledSquirrelSqlEsJarFile.getAbsolutePath()).andReturn(
			"mockInstalledSquirrelSqlEsJarFile").anyTimes();

		expect(mockInstalledDbCopyZipFile.getAbsolutePath()).andReturn("mockInstalledDbCopyZipFile").anyTimes();

		expect(mockBackupSquirrelSqlEsJarFile.getAbsolutePath()).andReturn("mockBackupSquirrelSqlEsJarFile")
			.anyTimes();

		expect(mockDownloadsCoreDirFile.getAbsolutePath()).andReturn("mockDownloadsCoreDirFile").anyTimes();

		expect(mockDownloadsPluginDirFile.getAbsolutePath()).andReturn("mockDownloadsPluginDirFile").anyTimes();

		expect(mockDownloadsFrameworkJarFile.getAbsolutePath()).andReturn("mockDownloadsFrameworkJarFile")
			.anyTimes();

		expect(mockDownloadsSquirrelSqlJarFile.getAbsolutePath()).andReturn("mockDownloadsSquirrelSqlJarFile")
			.anyTimes();

		expect(mockDownloadsSpringJarFile.getAbsolutePath()).andReturn("mockDownloadsSpringJarFile").anyTimes();

		expect(mockDownloadsDbCopyPluginZipFile.getAbsolutePath()).andReturn("mockDownloadsDbCopyPluginZipFile")
			.anyTimes();

		expect(mockDownloadsSquirrelSqlEsJarFile.getAbsolutePath()).andReturn(
			"mockDownloadsSquirrelSqlEsJarFile").anyTimes();

		expect(mockPathToInstalledDBCopyJarFile.getAbsolutePath()).andReturn("mockPathToInstalledDBCopyJarFile")
			.anyTimes();

		expect(mockPathToInstalledDBCopyDirectory.getAbsolutePath()).andReturn(
			"mockPathToInstalledDBCopyDirectory").anyTimes();
		
		expect(mockInstalledSpringJarFile.getAbsolutePath()).andReturn("mockInstalledSpringJarFile").anyTimes();
	}
	
	@After
	public void tearDown() throws Exception
	{
		implUnderTest = null;
	}

	/**
	 * @param filename
	 *           The name of the file to copy such that when it is appended to the installedDir, it yields the
	 *           specified installedFile
	 * @param installedDir
	 *           the directory in which the file to be backed resides
	 * @param installedFile
	 *           the File that is returned from concatenating installedDir and filename
	 * @param backupDir
	 *           the directory into which the backup file will be copied.
	 * @param backupFile
	 *           the File that represents the file after it is copied into the backup directory
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void setupFileCopyExpectations(String filename, FileWrapper installedDir, FileWrapper installedFile,
			FileWrapper backupDir, FileWrapper backupFile) throws FileNotFoundException, IOException
	{
		expect(mockUpdateUtil.getFile(installedDir, filename)).andReturn(
			installedFile).atLeastOnce();
		
		expect(mockUpdateUtil.getFile(backupDir, filename)).andReturn(
			backupFile).atLeastOnce();
		//expect(backupFile.getAbsolutePath()).andReturn("mockBackupFrameworkJarFile");
		mockUpdateUtil.copyFile(installedFile, backupFile);
	}
	
	
	@Test
	public final void testBackupFiles() throws Exception
	{
		setupFileCopyExpectations(FW_JAR_FILENAME, mockSquirreLLibDirFile, mockInstalledFrameworkJarFile,
			mockBackupCoreDirFile, mockInstalledFrameworkJarFile);

		setupFileCopyExpectations(SQUIRREL_SQL_JAR_FILENAME, mockSquirreLHomeDirFile, 
			mockInstalledSquirrelSqlJarFile, mockBackupCoreDirFile, mockBackupSquirrelSqlJarFile);
		
		
		mockUpdateUtil.createZipFile(mockBackupDbCopyZipFile, mockInstalledDbCopyPluginDirFile, mockPathToInstalledDBCopyJarFile);


		mockUpdateUtil.copyFile(mockInstalledSquirrelSqlEsJarFile, mockBackupSquirrelSqlEsJarFile);

		expect(mockInstallStatusEventFactory.create(InstallEventType.BACKUP_STARTED)).andReturn(
			mockBackupStartedStatusEvent);
		expect(mockInstallStatusEventFactory.create(InstallEventType.BACKUP_COMPLETE)).andReturn(
			mockBackupCompletedStatusEvent);

		expect(mockChangeListBean.getChanges()).andReturn(buildChangeList());

		mockInstallStatusListener.handleInstallStatusEvent(mockBackupStartedStatusEvent);
		mockInstallStatusListener.handleInstallStatusEvent(mockBackupCompletedStatusEvent);

		expect(mockUpdateUtil.getBackupDir()).andReturn(mockBackupRootDirFile);
		mockUpdateUtil.copyFile(isA(FileWrapper.class), eq(mockBackupRootDirFile));
				
		expect(mockUpdateUtil.checkDir(mockUpdateRootDirFile, UpdateUtil.DOWNLOADS_DIR_NAME)).andReturn(
			mockDownloadsRootDirFile);

		
		helper.replayAll();
		implUnderTest.setUpdateUtil(mockUpdateUtil);
		implUnderTest.setChangeList(mockChangeListBean);
		implUnderTest.backupFiles();
		helper.verifyAll();
	}
	
	@Test
	public final void testInstallFiles() throws IOException
	{
		expect(mockInstallStatusEventFactory.create(InstallEventType.INSTALL_STARTED)).andReturn(
			mockInstallStartedStatusEvent);
		expect(mockInstallStatusEventFactory.create(InstallEventType.INSTALL_COMPLETE)).andReturn(
			mockInstallCompletedStatusEvent);

		mockInstallStatusListener.handleInstallStatusEvent(mockInstallStartedStatusEvent);
		mockInstallStatusListener.handleInstallStatusEvent(mockInstallCompletedStatusEvent);

		expect(mockChangeListBean.getChanges()).andReturn(buildChangeList());
		
		/* expect getFile for updated files that will be removed */
		expect(mockUpdateUtil.deleteFile(mockInstalledSquirrelSqlJarFile)).andReturn(true);
		
		expect(mockUpdateUtil.getFile(mockSquirreLLibDirFile, FW_JAR_FILENAME)).andReturn(
			mockInstalledFrameworkJarFile);
		expect(mockUpdateUtil.deleteFile(mockInstalledFrameworkJarFile)).andReturn(true);
		
		expect(mockUpdateUtil.getFile(mockSquirreLLibDirFile, SPRING_JAR_FILENAME)).andReturn(
			mockInstalledSpringJarFile);
		expect(mockUpdateUtil.deleteFile(mockInstalledSpringJarFile)).andReturn(true);
				
		expect(mockUpdateUtil.getFile(mockSquirreLLibDirFile, SQUIRREL_SQL_ES_JAR_FILENAME
			)).andReturn(
			mockInstalledSquirrelSqlEsJarFile);
		expect(mockInstalledSquirrelSqlEsJarFile.exists()).andReturn(false);
		
		/* expect getFile for updated files that were downloaded */
		expect(mockUpdateUtil.getFile(mockDownloadsCoreDirFile, SQUIRREL_SQL_JAR_FILENAME)).andReturn(
			mockDownloadsSquirrelSqlJarFile);
		
		expect(mockUpdateUtil.getFile(mockDownloadsCoreDirFile, FW_JAR_FILENAME)).andReturn(
			mockDownloadsFrameworkJarFile);
		
		expect(mockUpdateUtil.getFile(mockDownloadsCoreDirFile, SPRING_JAR_FILENAME)).andReturn(
			mockDownloadsSpringJarFile);
		
		expect(mockUpdateUtil.getFile(mockDownloadsPluginDirFile, DBCOPY_ZIP_FILENAME)).andReturn(
			mockDownloadsDbCopyPluginZipFile).anyTimes();
		
		expect(mockUpdateUtil.getFile(mockSquirrelPluginsDirFile, DBCOPY_JAR_FILENAME)).andReturn(
			mockPathToInstalledDBCopyJarFile);
		expect(mockPathToInstalledDBCopyJarFile.exists()).andReturn(true);
		expect(mockUpdateUtil.deleteFile(mockPathToInstalledDBCopyJarFile)).andReturn(true);
		
		expect(mockUpdateUtil.getFile(mockSquirrelPluginsDirFile, DBCOPY_DIR_FILENAME)).andReturn(
			mockPathToInstalledDBCopyDirectory);		
		expect(mockPathToInstalledDBCopyDirectory.exists()).andReturn(true);
		
		expect(mockUpdateUtil.deleteFile(mockPathToInstalledDBCopyDirectory)).andReturn(true);
		
		expect(mockUpdateUtil.getFile(mockDownloadsI18nDirFile, SQUIRREL_SQL_ES_JAR_FILENAME)).andReturn(
			mockDownloadsSquirrelSqlEsJarFile);

		boolean isPlugin = true;
		boolean isNotPlugin = false;
		
		/* expected fileOperationInfos for files that will be installed */
		setupFileCopyOperationInfo(mockDownloadsSquirrelSqlJarFile, mockSquirreLHomeDirFile,
			mockInstallSquirrelSqlJarOperationInfo, isNotPlugin);
		setupFileCopyOperationInfo(mockDownloadsFrameworkJarFile, mockSquirreLLibDirFile,
			mockInstallFrameworkJarOperationInfo, isNotPlugin);
		setupFileCopyOperationInfo(mockDownloadsSpringJarFile, mockSquirreLLibDirFile,
			mockInstallSpringJarOperationInfo, isNotPlugin);
		setupFileCopyOperationInfo(mockDownloadsDbCopyPluginZipFile, mockSquirrelPluginsDirFile,
			mockInstallDbCopyZipOperationInfo, isPlugin);
		setupFileCopyOperationInfo(mockDownloadsSquirrelSqlEsJarFile, mockSquirreLLibDirFile,
			mockInstallSquirrelSqlEsOperationInfo, isNotPlugin);
									
		FileWrapper mockChangeListFile = helper.createMock("mockChangeListFile", FileWrapper.class);
		mockUpdateUtil.copyFile(mockChangeListFile, mockBackupRootDirFile);
		expect(mockUpdateUtil.deleteFile(mockChangeListFile)).andReturn(true);
		expect(mockUpdateUtil.deleteFile(mockLocalReleaseFile)).andReturn(true);
				
		mockUpdateUtil.copyFile(mockDownloadReleaseFile, mockUpdateRootDirFile);
		
		helper.replayAll();
		implUnderTest.setUpdateUtil(mockUpdateUtil);
		implUnderTest.setChangeList(mockChangeListBean);
		implUnderTest.setChangeListFile(mockChangeListFile);
		implUnderTest.installFiles();
		helper.verifyAll();
	}
	
	@Test
	public void testDisallowCoreTypeFileRemoval() throws Exception {
		/* expectations that are specific to this test */
		makeCommonUpdateUtilAssertions();
		setupInstallEventsAndListener();		
		
		expect(mockChangeListBean.getChanges()).andReturn(buildRemoveCoreFileChangeList());
		
		expect(mockUpdateUtil.deleteFile(mockLocalReleaseFile)).andReturn(true);
		mockUpdateUtil.copyFile(mockDownloadReleaseFile, mockUpdateRootDirFile);
		
		helper.replayAll();
		implUnderTest.setChangeList(mockChangeListBean);
		implUnderTest.setUpdateUtil(mockUpdateUtil);
		implUnderTest.installFiles();
		helper.verifyAll();		
	}
	
	// Failure Tests
	
	@Test
	public void testInstallFiles_FailedToRemoveExistingFiles() throws Exception {

		makeCommonUpdateUtilAssertions();
		setupInstallEventsAndListener();
				

		expect(mockUpdateUtil.deleteFile(mockInstalledSquirrelSqlEsJarFile)).andReturn(false);
		List<ArtifactStatus> mockChangeList = getSquirrelSqlEsJarChangeList();
		expect(mockChangeListBean.getChanges()).andReturn(mockChangeList);				
		expect(mockUpdateUtil.getFile(mockDownloadsI18nDirFile, SQUIRREL_SQL_ES_JAR_FILENAME)).andReturn(
			mockDownloadsSquirrelSqlEsJarFile);
		expect(mockUpdateUtil.getFile(mockSquirreLLibDirFile, SQUIRREL_SQL_ES_JAR_FILENAME)).andReturn(
			mockInstalledSquirrelSqlEsJarFile);
		
		expect(mockInstallFileOperationInfoFactory.create(mockDownloadsSquirrelSqlEsJarFile, mockSquirreLLibDirFile)).andReturn(
			mockInstallSquirrelSqlEsOperationInfo);
		mockInstallSquirrelSqlEsOperationInfo.setPlugin(false);
		
		helper.replayAll();
		implUnderTest.setUpdateUtil(mockUpdateUtil);
		implUnderTest.setChangeList(mockChangeListBean);
		implUnderTest.installFiles();
		helper.verifyAll();		
		
		
	}
	
	
	// Helper Methods
	
	private void setupInstallEventsAndListener() { 
		expect(mockInstallStatusEventFactory.create(InstallEventType.INSTALL_STARTED)).andReturn(
			mockInstallStartedStatusEvent);
		
		mockInstallStatusListener.handleInstallStatusEvent(mockInstallStartedStatusEvent);
		
		expect(mockInstallStatusEventFactory.create(InstallEventType.INSTALL_COMPLETE)).andReturn(
			mockInstallCompletedStatusEvent);				
		
		mockInstallStatusListener.handleInstallStatusEvent(mockInstallCompletedStatusEvent);
	}
	
	private void setupFileCopyOperationInfo(FileWrapper downloadsFile, FileWrapper installDir,
		InstallFileOperationInfo info, boolean isPlugin) throws IOException
	{
		expect(mockInstallFileOperationInfoFactory.create(downloadsFile, installDir)).andReturn(info);
		expect(info.getInstallDir()).andReturn(installDir);
		expect(info.getFileToInstall()).andReturn(downloadsFile);
		info.setPlugin(isPlugin);
		mockUpdateUtil.copyFile(downloadsFile, installDir);
	}
	
	private void makeCommonUpdateUtilAssertions() throws FileNotFoundException
	{
		expect(mockUpdateUtil.checkDir(mockUpdateRootDirFile, UpdateUtil.BACKUP_ROOT_DIR_NAME)).andStubReturn(
			mockBackupRootDirFile);
		expect(mockUpdateUtil.checkDir(mockUpdateRootDirFile, UpdateUtil.DOWNLOADS_DIR_NAME)).andStubReturn(
			mockDownloadsRootDirFile);		
		expect(mockUpdateUtil.checkDir(mockBackupRootDirFile, UpdateUtil.CORE_ARTIFACT_ID)).andStubReturn(
			mockBackupCoreDirFile);
		expect(mockUpdateUtil.checkDir(mockBackupRootDirFile, UpdateUtil.PLUGIN_ARTIFACT_ID)).andStubReturn(
			mockBackupPluginDirFile);
		expect(mockUpdateUtil.checkDir(mockBackupRootDirFile, UpdateUtil.TRANSLATION_ARTIFACT_ID)).andStubReturn(
			mockBackupTranslationDirFile);

		expect(mockUpdateUtil.getLocalReleaseFile()).andStubReturn(mockLocalReleaseFile);
		
		expect(mockUpdateUtil.getCoreDownloadsDir()).andStubReturn(mockDownloadsCoreDirFile);
		expect(mockUpdateUtil.getPluginDownloadsDir()).andStubReturn(mockDownloadsPluginDirFile);
		expect(mockUpdateUtil.getI18nDownloadsDir()).andStubReturn(mockDownloadsI18nDirFile);
	}

	private List<ArtifactStatus> buildRemoveCoreFileChangeList() {
		ArrayList<ArtifactStatus> result = new ArrayList<ArtifactStatus>();
		final String coreType = UpdateUtil.CORE_ARTIFACT_ID;
		final boolean installed = true;
		ArtifactStatus squirrelSqlJarToRemove =
			getArtifactToRemove(SQUIRREL_SQL_JAR_FILENAME, installed, coreType);
		result.add(squirrelSqlJarToRemove);
		return result;
	}
	
	private List<ArtifactStatus> buildChangeList()
	{
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
		ArtifactStatus newSquirrelSqlEsJar =
			getArtifactToInstall(SQUIRREL_SQL_ES_JAR_FILENAME, installed, i18nType);

		result.add(newSquirrelSqlJar);
		result.add(newFrameworkJar);
		result.add(newSpringJar);
		result.add(newDbcopyZip);
		result.add(newSquirrelSqlEsJar);
		return result;
	}

	private List<ArtifactStatus> getSquirrelSqlEsJarChangeList() {
		ArrayList<ArtifactStatus> result = new ArrayList<ArtifactStatus>();
		final boolean installed = true;
		final String i18nType = UpdateUtil.TRANSLATION_ARTIFACT_ID;
		result.add(getArtifactToInstall(SQUIRREL_SQL_ES_JAR_FILENAME, installed, i18nType));
		return result;
	}
	
	private ArtifactStatus getArtifactToInstall(String name, boolean installed, String type)
	{
		ArtifactStatus result = new ArtifactStatus();
		result.setArtifactAction(ArtifactAction.INSTALL);
		result.setName(name);
		result.setInstalled(installed);
		result.setType(type);
		return result;
	}

	private ArtifactStatus getArtifactToRemove(String name, boolean installed, String type)
	{
		ArtifactStatus result = new ArtifactStatus();
		result.setArtifactAction(ArtifactAction.REMOVE);
		result.setName(name);
		result.setInstalled(installed);
		result.setType(type);
		return result;
	}
	
}
