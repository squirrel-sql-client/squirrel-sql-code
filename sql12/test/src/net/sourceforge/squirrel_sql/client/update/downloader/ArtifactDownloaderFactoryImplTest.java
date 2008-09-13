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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

public class ArtifactDownloaderFactoryImplTest extends BaseSQuirreLJUnit4TestCase {

	
	ArtifactDownloaderFactoryImpl classUnderTest = null; 
	
	EasyMockHelper mockHelper = new EasyMockHelper();
	
	@Before
	public void setUp() throws Exception {
		classUnderTest = new ArtifactDownloaderFactoryImpl();
	}

	@After
	public void tearDown() throws Exception {
		classUnderTest = null;
	}

	@Test
	public final void testCreate() {
		
		ArtifactStatus mockArtifactStatus = mockHelper.createMock(ArtifactStatus.class);
		List<ArtifactStatus> artifactStatusList = new ArrayList<ArtifactStatus>();
		artifactStatusList.add(mockArtifactStatus);
		ArtifactDownloader result = classUnderTest.create(artifactStatusList);
		List<ArtifactStatus> actualArtifactStatusList = result.getArtifactStatus();
		assertEquals(artifactStatusList.size(), actualArtifactStatusList.size());
		assertEquals(artifactStatusList.get(0), actualArtifactStatusList.get(0));
	}

}
