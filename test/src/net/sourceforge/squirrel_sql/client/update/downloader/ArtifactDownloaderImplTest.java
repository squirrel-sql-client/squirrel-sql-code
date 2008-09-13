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
package net.sourceforge.squirrel_sql.client.update.downloader;

import static org.easymock.EasyMock.contains;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.downloader.event.DownloadEventType;
import net.sourceforge.squirrel_sql.client.update.downloader.event.DownloadStatusEvent;
import net.sourceforge.squirrel_sql.client.update.downloader.event.DownloadStatusListener;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactAction;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

public class ArtifactDownloaderImplTest extends BaseSQuirreLJUnit4TestCase {
	
	private static final String CORE_DOWNLOADS_DIR = "/core/Downloads/Dir/";
	private static final String PLUGIN_DOWNLOADS_DIR = "/plugin/Downloads/Dir";
	
	private EasyMockHelper mockHelper = new EasyMockHelper();
	
	/* Class to test */
	private ArtifactDownloaderImpl classUnderTest = null;
	
	/* Common mock objects */
	private ArtifactStatus mockCoreArtifactStatus = 
		mockHelper.createMock("mockCoreArtifactStatus",ArtifactStatus.class);
	private ArtifactStatus mockPluginArtifactStatus = 
		mockHelper.createMock("mockPluginArtifactStatus",ArtifactStatus.class);
	
	private UpdateUtil mockUpdateUtil = mockHelper.createMock("mockUpdateUtil", UpdateUtil.class);
	
	private final String host = "www.updateServer.com";
	private final String coreFilename = "core.jar";
	private final long coreFileSize = 1000L; 
	private final int port = 80;
	private final String destDir = "/path/to/core/download/file";
	private final String destFileAbsPath = destDir + "/" + coreFilename;
	private final long checksum = 10L;

	private FileWrapper coreDownloadsDir = mockHelper.createMock("coreDownloadsDir", FileWrapper.class);
	private FileWrapper pluginDownloadsDir = 
		mockHelper.createMock("pluginDownloadsDir", FileWrapper.class);
	
	
	
	
	@Before
	public void setUp() throws Exception {
		expect(mockCoreArtifactStatus.getArtifactAction()).andStubReturn(ArtifactAction.INSTALL);
		expect(mockCoreArtifactStatus.getChecksum()).andStubReturn(10L);
		expect(mockCoreArtifactStatus.getSize()).andStubReturn(1000L);
		expect(mockCoreArtifactStatus.getName()).andStubReturn(coreFilename);
		expect(mockCoreArtifactStatus.getType()).andStubReturn(UpdateUtil.CORE_ARTIFACT_ID);
		
		expect(mockPluginArtifactStatus.getName()).andStubReturn("aPluginName");
		expect(mockPluginArtifactStatus.getType()).andStubReturn(UpdateUtil.PLUGIN_ARTIFACT_ID);
		
		
		List<ArtifactStatus> artifactStatusList = new ArrayList<ArtifactStatus>();
		artifactStatusList.add(mockCoreArtifactStatus);
		artifactStatusList.add(mockPluginArtifactStatus);
		
		expect(coreDownloadsDir.getAbsolutePath()).andStubReturn(CORE_DOWNLOADS_DIR);
		expect(pluginDownloadsDir.getAbsolutePath()).andStubReturn(PLUGIN_DOWNLOADS_DIR);
		expect(mockUpdateUtil.getCoreDownloadsDir()).andStubReturn(coreDownloadsDir);
		expect(mockUpdateUtil.isPresentInDownloadsDirectory(mockCoreArtifactStatus)).andReturn(false);
		expect(mockUpdateUtil.getPluginDownloadsDir()).andStubReturn(pluginDownloadsDir);
		expect(mockUpdateUtil.isPresentInDownloadsDirectory(mockPluginArtifactStatus)).andStubReturn(true);		
		
		classUnderTest = new ArtifactDownloaderImpl(artifactStatusList);
		classUnderTest.setUtil(mockUpdateUtil);
	}

	@After
	public void tearDown() throws Exception {
		classUnderTest = null;
		mockHelper.resetAll();
	}

	@Test
	public final void testRunSuccess() throws Exception {
		classUnderTest.setHost(host);
		classUnderTest.setPort(port);
		classUnderTest.setChannelName("channelName");
		classUnderTest.setIsRemoteUpdateSite(true);
		
		expect(mockUpdateUtil.downloadHttpUpdateFile(eq(host), eq(port), contains(coreFilename), 
			eq(CORE_DOWNLOADS_DIR), eq(coreFileSize), eq(checksum)));
		expectLastCall().andReturn(destFileAbsPath);
		
		DownloadStatusTypeCheckListenerStub listener = 
			new DownloadStatusTypeCheckListenerStub();
		listener.expectEventType(DownloadEventType.DOWNLOAD_STARTED);
		listener.expectDownloadFileSequence(2);
		listener.expectEventType(DownloadEventType.DOWNLOAD_COMPLETED);
		classUnderTest.addDownloadStatusListener(listener);
		
		mockHelper.replayAll();
		classUnderTest.run();
		mockHelper.verifyAll();
	}
	
	@Test
	public final void testRunFailure() throws Exception {
		classUnderTest.setHost(host);
		classUnderTest.setPort(port);
		classUnderTest.setChannelName("channelName");
		classUnderTest.setIsRemoteUpdateSite(true);

		expect(mockUpdateUtil.downloadHttpUpdateFile(eq(host), eq(port), contains(coreFilename), 
			eq(CORE_DOWNLOADS_DIR), eq(coreFileSize), eq(checksum)));
		expectLastCall().andThrow(new Exception("Test exception for run() method failure test"));

		
		mockHelper.replayAll();
		classUnderTest.run();
		mockHelper.verifyAll();		
	}


	private class DownloadStatusTypeCheckListenerStub implements DownloadStatusListener {

		List<DownloadEventType> events = new ArrayList<DownloadEventType>();
		
		int currentEventIdx = 0;
		
		public void expectEventType(DownloadEventType e) {
			events.add(e);
		}
		
		public void expectDownloadFileSequence(int times) {
			for (int i = 0; i < times; i++) {
				events.add(DownloadEventType.DOWNLOAD_FILE_STARTED);
				events.add(DownloadEventType.DOWNLOAD_FILE_COMPLETED);
			}
		}
		
		public void handleDownloadStatusEvent(DownloadStatusEvent actual) {
			if (currentEventIdx >= events.size()) {
				fail("Not enough downloadStatusEvent expectations set.  " +
						"Next expect needed appears to be : " + actual.getType());
			}
			DownloadEventType expected = events.get(currentEventIdx++);
	      assertEquals(expected, actual.getType());
      }
		
	}
}
