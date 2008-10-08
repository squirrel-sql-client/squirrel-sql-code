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
package net.sourceforge.squirrel_sql.client.preferences;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.Version;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

public class UpdateSettingsTest extends BaseSQuirreLJUnit4TestCase
{

	private static final String FILE_SYSTEM_UPDATE_PATH = "";

	private static final boolean IS_REMOTE_UPDATE_SITE = true;

	private static final boolean IS_ENABLE_AUTOMATIC_UPDATES = true;

	private static final String LAST_UPDATE_CHECK_TIME_MILLIS = "LastUpdateCheckTimeMillis";

	private static final String UPDATE_CHECK_FREQUENCY = "UpdateCheckFrequency";

	private static final String UPDATE_SERVER_CHANNEL = "UpdateServerChannel";

	private static final String UPDATE_SERVER_PORT = "UpdateServerPort";

	private static final String UPDATE_SERVER_PATH = "UpdateServerPath";

	private static final String UPDATE_SERVER = "UpdateServer";

	private UpdateSettings classUnderTest = null;
	
	private EasyMockHelper mockHelper = new EasyMockHelper();
	
	private IUpdateSettings mockUpdateSettings = mockHelper.createMock(IUpdateSettings.class);
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new UpdateSettings();
		
		expect(mockUpdateSettings.getUpdateServer()).andStubReturn(UPDATE_SERVER);
		expect(mockUpdateSettings.getUpdateServerPath()).andStubReturn(UPDATE_SERVER_PATH);
		expect(mockUpdateSettings.getUpdateServerPort()).andStubReturn(UPDATE_SERVER_PORT);
		expect(mockUpdateSettings.getUpdateServerChannel()).andStubReturn(UPDATE_SERVER_CHANNEL);
		expect(mockUpdateSettings.isEnableAutomaticUpdates()).andStubReturn(IS_ENABLE_AUTOMATIC_UPDATES);
		expect(mockUpdateSettings.getUpdateCheckFrequency()).andStubReturn(UPDATE_CHECK_FREQUENCY);
		expect(mockUpdateSettings.getLastUpdateCheckTimeMillis()).andStubReturn(LAST_UPDATE_CHECK_TIME_MILLIS);
		expect(mockUpdateSettings.isRemoteUpdateSite()).andStubReturn(IS_REMOTE_UPDATE_SITE);
		expect(mockUpdateSettings.getFileSystemUpdatePath()).andStubReturn(FILE_SYSTEM_UPDATE_PATH);
		
		if (Version.isSnapshotVersion()) {
			
		} else {
			assertEquals("stable", classUnderTest.getUpdateServerChannel());
		}
		
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testUpdateSettingsIUpdateSettings()
	{
		mockHelper.replayAll();
		classUnderTest = new UpdateSettings(mockUpdateSettings);
		mockHelper.verifyAll();
		
		assertEquals(UPDATE_SERVER, classUnderTest.getUpdateServer());
		assertEquals(UPDATE_SERVER_PATH, classUnderTest.getUpdateServerPath());
		assertEquals(UPDATE_SERVER_PORT, classUnderTest.getUpdateServerPort());
		assertEquals(UPDATE_SERVER_CHANNEL, classUnderTest.getUpdateServerChannel());
		assertEquals(IS_ENABLE_AUTOMATIC_UPDATES, classUnderTest.isEnableAutomaticUpdates());
		assertEquals(UPDATE_CHECK_FREQUENCY, classUnderTest.getUpdateCheckFrequency());
		assertEquals(LAST_UPDATE_CHECK_TIME_MILLIS, classUnderTest.getLastUpdateCheckTimeMillis());
		assertEquals(IS_REMOTE_UPDATE_SITE, classUnderTest.isRemoteUpdateSite());
		assertEquals(FILE_SYSTEM_UPDATE_PATH, classUnderTest.getFileSystemUpdatePath());
	}

	@Test 
	public void testSetGetUpdateServer()
	{
		classUnderTest.setUpdateServer("test");
		assertEquals("test", classUnderTest.getUpdateServer());
	}

	@Test
	public void testSetGetUpdateServerPort()
	{
		classUnderTest.setUpdateServerPort("8080");
		assertEquals("8080", classUnderTest.getUpdateServerPort());
	}

	@Test
	public void testSetGetUpdateServerPath()
	{
		classUnderTest.setUpdateServerPath("test");
		assertEquals("test", classUnderTest.getUpdateServerPath());
	}

	@Test
	public void testGetSetUpdateServerChannel()
	{
		assertEquals("stable", classUnderTest.getUpdateServerChannel());
		classUnderTest.setUpdateServerChannel("snapshot");
		assertEquals("snapshot", classUnderTest.getUpdateServerChannel());
	}

	@Test
	public void testSetEnableAutomaticUpdates()
	{
		classUnderTest.setEnableAutomaticUpdates(true);
		assertTrue(classUnderTest.isEnableAutomaticUpdates());
	}

	@Test
	public void testSetUpdateCheckFrequency()
	{
		classUnderTest.setUpdateCheckFrequency("test");
		assertEquals("test", classUnderTest.getUpdateCheckFrequency());
	}

	@Test
	public void testSetGetLastUpdateCheckTimeMillis()
	{
		classUnderTest.setLastUpdateCheckTimeMillis("1111111");
		assertEquals("1111111", classUnderTest.getLastUpdateCheckTimeMillis());
	}

	@Test
	public void testSetRemoteUpdateSite()
	{
		classUnderTest.setRemoteUpdateSite(false);
		assertEquals(false, classUnderTest.isRemoteUpdateSite());
	}

	@Test
	public void testSetGetFileSystemUpdatePath()
	{
		classUnderTest.setFileSystemUpdatePath("test");
		assertEquals("test", classUnderTest.getFileSystemUpdatePath());
	}


}
