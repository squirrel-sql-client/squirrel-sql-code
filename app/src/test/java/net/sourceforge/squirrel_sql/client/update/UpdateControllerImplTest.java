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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.IPluginManager;
import net.sourceforge.squirrel_sql.client.preferences.IUpdateSettings;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.preferences.UpdateSettings;
import net.sourceforge.squirrel_sql.client.preferences.UpdateChannelComboBoxEntry.ChannelType;
import net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloader;
import net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloaderFactory;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactAction;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ReleaseXmlBean;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.ProxySettings;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

public class UpdateControllerImplTest extends BaseSQuirreLJUnit4TestCase {

   private static final String TEST_CHANNEL = "testchannel";
	private static final String TEST_PATH = "testPath";
	private static final int TEST_UPDATE_SERVER_PORT = 80;
   private static final String TEST_UPDATE_SERVER_PORT_STR = "80";
   
	private static final String TEST_UPDATE_SERVER = "testUpdateServer";

	private static final String FILE_SYSTEM_UPDATE_PATH = "fileSystemUpdatePath";

	private static final String LOCAL_RELEASE_FILENAME = UpdateUtil.RELEASE_XML_FILENAME;

	UpdateControllerImpl underTest = null;
   
   EasyMockHelper mockHelper = new EasyMockHelper();
   
   /* Mock objects */
   IApplication mockApplication = null;
   SquirrelPreferences mockSquirrelPreferences = null;
   IUpdateSettings mockUpdateSettings = null;
   UpdateUtil mockUpdateUtil = null;
   ChannelXmlBean mockInstalledChannelXmlBean = null;
   ChannelXmlBean mockAvailableChannelXmlBean = null;
   IPluginManager mockPluginManager = null;
   FileWrapper mockLocalReleaseFile = null;
   
   @Before
   public void setUp() throws Exception {
      
      mockApplication = mockHelper.createMock("mockApplication", IApplication.class);
      mockUpdateSettings = mockHelper.createMock("mockUpdateSettings", UpdateSettings.class);
      mockSquirrelPreferences = mockHelper.createMock("mockSquirrelPreferences", SquirrelPreferences.class);
      mockPluginManager = mockHelper.createMock("mockPluginManager", IPluginManager.class);
      mockInstalledChannelXmlBean = setupSnapshotChannelXmlBean("Installed");
      mockAvailableChannelXmlBean = mockHelper.createMock(ChannelXmlBean.class);
      mockLocalReleaseFile = mockHelper.createMock("mockLocalReleaseFile", FileWrapper.class);
      expect(mockLocalReleaseFile.getAbsolutePath()).andStubReturn(LOCAL_RELEASE_FILENAME);
   }

   private IUpdateSettings setupUpdateSettings(boolean isRemoteUpdateSite)
	{
   	IUpdateSettings result = mockHelper.createMock(IUpdateSettings.class); 
   	expect(result.getUpdateServerPath()).andStubReturn("pathForUpdates");
   	expect(result.getFileSystemUpdatePath()).andStubReturn(FILE_SYSTEM_UPDATE_PATH);
   	expect(result.isRemoteUpdateSite()).andStubReturn(isRemoteUpdateSite);
		return result;
	}

	private void replayAll() {
   	mockHelper.replayAll();
   }
   
   private void verifyAll() {
   	mockHelper.verifyAll();
   }
   
   @After
   public void tearDown() throws Exception {
   	
   }

   @Test 
   public void testIsUpToDateLocalSiteUpdatesAvailable() throws Exception {
   	
   	/* setup expectations */
   	mockUpdateUtil =
			setupUpdateUtilLocalUpdateSite(mockInstalledChannelXmlBean, mockAvailableChannelXmlBean);
   	boolean isRemoteUpdateSite = false;
   	mockUpdateSettings = setupUpdateSettings(isRemoteUpdateSite);
   	ChannelType type = ChannelType.SNAPSHOT;
   	expect(mockUpdateSettings.getUpdateServerChannel()).andReturn(type.name());
   	
   	expect(mockApplication.getPluginManager()).andReturn(mockPluginManager).anyTimes();
   	expect(mockApplication.getSquirrelPreferences()).andReturn(mockSquirrelPreferences).anyTimes();
   	expect(mockSquirrelPreferences.getUpdateSettings()).andReturn(mockUpdateSettings).anyTimes();
   	mockSquirrelPreferences.setUpdateSettings(mockUpdateSettings);
   	mockUpdateSettings.setLastUpdateCheckTimeMillis(isA(String.class));   
   	
   	replayAll();
   	
   	underTest = new UpdateControllerImpl(mockApplication);
   	underTest.setUpdateUtil(mockUpdateUtil);
   	boolean result = underTest.isUpToDate();
   	assertFalse(result);
   	verifyAll();
   	
   }
   
   @Test
   public void testIsUpToDate_RemoteSiteUpdatesAvailable() throws Exception {
   	/* setup expectations */
   	mockUpdateUtil =
			setupUpdateUtilRemoteUpdateSite(mockInstalledChannelXmlBean, mockAvailableChannelXmlBean);
   	boolean isRemoteUpdateSite = true;
   	mockUpdateSettings = setupUpdateSettings(isRemoteUpdateSite);

   	underTest = new UpdateControllerImpl(mockApplication);
   	
   }
   
   @Test
   public void testPullDownUpdateFiles_remoteSite() {
   	
   	List<ArtifactStatus> artifactStatusList = new ArrayList<ArtifactStatus>();
   	ArtifactStatus mockArtifactStatus = mockHelper.createMock("mockArtifactStatus", ArtifactStatus.class);
   	
   	DownloadStatusEventHandler mockDownloadStatusListener = 
   		mockHelper.createMock("mockDownloadStatusEventHandler", DownloadStatusEventHandler.class);
   	ProxySettings mockProxySettings = mockHelper.createMock("mockProxySettings", ProxySettings.class);
   	ArtifactDownloaderFactory mockArtifactDownloaderFactory = 
			mockHelper.createMock("ArtifactDownloaderFactory", ArtifactDownloaderFactory.class);		
		ArtifactDownloader mockArtifactDownloader = 
			mockHelper.createMock("mockArtifactDownloader", ArtifactDownloader.class);
		
   	expect(mockArtifactStatus.getArtifactAction()).andStubReturn(ArtifactAction.INSTALL);
   	
   	artifactStatusList.add(mockArtifactStatus);

		expect(mockArtifactDownloaderFactory.create(artifactStatusList)).andStubReturn(mockArtifactDownloader);
		
		mockArtifactDownloader.setUtil(mockUpdateUtil);
		mockArtifactDownloader.setProxySettings(mockProxySettings);
		mockArtifactDownloader.setIsRemoteUpdateSite(true);
		mockArtifactDownloader.setHost(TEST_UPDATE_SERVER);
		mockArtifactDownloader.setPort(TEST_UPDATE_SERVER_PORT);
		mockArtifactDownloader.setPath(TEST_PATH);
		mockArtifactDownloader.setFileSystemUpdatePath("");
		mockArtifactDownloader.addDownloadStatusListener(mockDownloadStatusListener);
		mockArtifactDownloader.setChannelName(TEST_CHANNEL);
		mockArtifactDownloader.setReleaseVersionWillChange(true);
		mockArtifactDownloader.start();
		
		
		mockDownloadStatusListener.setDownloader(mockArtifactDownloader);
		
		expect(mockApplication.getSquirrelPreferences()).andStubReturn(mockSquirrelPreferences);
		expect(mockSquirrelPreferences.getProxySettings()).andStubReturn(mockProxySettings);
		expect(mockSquirrelPreferences.getUpdateSettings()).andStubReturn(mockUpdateSettings);
		
		expect(mockUpdateSettings.isRemoteUpdateSite()).andStubReturn(true);
		expect(mockUpdateSettings.getUpdateServer()).andStubReturn(TEST_UPDATE_SERVER);
		expect(mockUpdateSettings.getUpdateServerPort()).andStubReturn(TEST_UPDATE_SERVER_PORT_STR);
		expect(mockUpdateSettings.getUpdateServerPath()).andStubReturn(TEST_PATH);
		expect(mockUpdateSettings.getFileSystemUpdatePath()).andStubReturn("");
		expect(mockUpdateSettings.getUpdateServerChannel()).andStubReturn(TEST_CHANNEL);
		
   	replayAll();
   	
   	underTest = new UpdateControllerImpl(mockApplication);
   	underTest.setArtifactDownloaderFactory(mockArtifactDownloaderFactory);
   	underTest.pullDownUpdateFiles(artifactStatusList, mockDownloadStatusListener, true);
		
   	verifyAll();
   }
   
   private UpdateUtil setupUpdateUtilLocalUpdateSite(ChannelXmlBean installed, ChannelXmlBean available)
		throws Exception
	{
		UpdateUtil mockUpdateUtil = setupUpdateUtil();
		expect(mockUpdateUtil.getLocalReleaseFile()).andStubReturn(mockLocalReleaseFile);
		expect(mockUpdateUtil.getLocalReleaseInfo(LOCAL_RELEASE_FILENAME)).andStubReturn(installed);
		expect(mockUpdateUtil.loadUpdateFromFileSystem(FILE_SYSTEM_UPDATE_PATH)).andReturn(available);
		return mockUpdateUtil;
	}

   private UpdateUtil setupUpdateUtilRemoteUpdateSite(ChannelXmlBean installed, ChannelXmlBean available)
		throws Exception
	{
   	UpdateUtil mockUpdateUtil = setupUpdateUtil();
   	return mockUpdateUtil;
	}
   
   private UpdateUtil setupUpdateUtil() {
		UpdateUtil mockUpdateUtil = mockHelper.createMock(UpdateUtil.class);
		mockUpdateUtil.setPluginManager(mockPluginManager);
		return mockUpdateUtil;
   }
   
   
   private ChannelXmlBean setupSnapshotChannelXmlBean(String name) {
   	ChannelXmlBean mockChannelXmlBean = mockHelper.createMock("mock"+name+"ChannelXmlBean", ChannelXmlBean.class);
   	ReleaseXmlBean mockReleaseXmlBean = mockHelper.createMock("mock"+name+"ReleaseXmlBean", ReleaseXmlBean.class);
   	expect(mockChannelXmlBean.getName()).andStubReturn("Snapshot");
   	expect(mockChannelXmlBean.getCurrentRelease()).andStubReturn(mockReleaseXmlBean);
   	return mockChannelXmlBean;
   }
}
