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

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactAction;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

public class ArtifactDownloaderImplTest extends BaseSQuirreLJUnit4TestCase {
	
	private static final String CORE_DOWNLOADS_DIR = "/core/Downloads/Dir/";

	private EasyMockHelper mockHelper = new EasyMockHelper();
	
	/* Class to test */
	private ArtifactDownloaderImpl classUnderTest = null;
	
	/* Common mock objects */
	private ArtifactStatus mockCoreArtifactStatus = 
		mockHelper.createMock("mockCoreArtifactStatus",ArtifactStatus.class);
	private ArtifactStatus mockPluginArtifactStatus = 
		mockHelper.createMock("mockPluginArtifactStatus",ArtifactStatus.class);
	private ArtifactStatus mockTranslationArtifactStatus = 
		mockHelper.createMock("mockTranslationArtifactStatus",ArtifactStatus.class);
	
	private UpdateUtil mockUpdateUtil = mockHelper.createMock("mockUpdateUtil", UpdateUtil.class);
	
	private final String coreFilename = "core.jar";
	private final long coreFileSize = 1000L; 
	private final int port = 80;
	private final String destDir = "/path/to/core/download/file";
	private final String destFileAbsPath = destDir + "/" + coreFilename;
	private final long checksum = 10L;

	private FileWrapper coreDownloadsDir = mockHelper.createMock("coreDownloadsDir", FileWrapper.class);
	
	
	
	
	@Before
	public void setUp() throws Exception {
		expect(mockCoreArtifactStatus.getArtifactAction()).andStubReturn(ArtifactAction.INSTALL);
		expect(mockCoreArtifactStatus.getChecksum()).andStubReturn(10L);
		expect(mockCoreArtifactStatus.getSize()).andStubReturn(1000L);
		expect(mockCoreArtifactStatus.getName()).andStubReturn(coreFilename);
		expect(mockCoreArtifactStatus.getType()).andStubReturn(UpdateUtil.CORE_ARTIFACT_ID);
		
		List<ArtifactStatus> artifactStatusList = new ArrayList<ArtifactStatus>();
		artifactStatusList.add(mockCoreArtifactStatus);
		
		expect(coreDownloadsDir.getAbsolutePath()).andStubReturn(CORE_DOWNLOADS_DIR);
		expect(mockUpdateUtil.getCoreDownloadsDir()).andStubReturn(coreDownloadsDir);
		expect(mockUpdateUtil.isPresentInDownloadsDirectory(mockCoreArtifactStatus)).andReturn(false);
		
		
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
		String host = "www.updateServer.com";
		
		classUnderTest.setHost(host);
		classUnderTest.setPort(port);
		classUnderTest.setChannelName("channelName");
		classUnderTest.setIsRemoteUpdateSite(true);
		
		expect(mockUpdateUtil.downloadHttpUpdateFile(eq(host), eq(port), EasyMock.contains(coreFilename), eq(CORE_DOWNLOADS_DIR), eq(coreFileSize), eq(checksum)));
		expectLastCall().andReturn(destFileAbsPath);
		
		mockHelper.replayAll();
		classUnderTest.run();
		mockHelper.verifyAll();
	}



}
